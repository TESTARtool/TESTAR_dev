module lang::testar::Compiler

import lang::testar::Oracle;
import lang::testar::Check;
import lang::testar::Model;
import IO;
import ParseTree;
import String;
import util::FileSystem;
import util::Maybe;


start[Oracle] desugarFlatContexts(start[Oracle] oracle) {
  // assumes contexts are not nested
  bool change = true;
  bool changed() {
    change = true;
    return true;
  }
  while (change) {
    change = false;
    oracle = visit (oracle) {
      case (start[Oracle])`<Package pkg> 
                          '<Decl* ds0>
                          'context <String+ p> 
                          '<Decl* ds1>
                          '<Decl* ds2>`
        => (start[Oracle])`<Package pkg>
                          '<Decl* ds0> 
                          'context <String+ p> {
                          '  <Decl* ds1>
                          '}
                          '<Decl* ds2>`
      when all(Decl d <- ds1, d is \assert), list[Decl] ds := [ d | Decl d <- ds2 ],
        ds == [] || ds[0] is flatContext || ds[0] is context, changed()
    }
  }
  return oracle;
}

start[Oracle] desugarUnless(start[Oracle] oracle) {
  return visit (oracle) {
    case (Predicate)`<Predicate x> unless <Widget w> <Cond c>`
      => (Predicate)`<Predicate x> when <Widget w> not(<Cond c>)`
  }
}

start[Oracle] desugarConds(start[Oracle] oracle) {
  return visit (oracle) {
    case (Cond)`starts with <String x>` => (Cond)`matches <String re>`
      when String re := [String]"\"^<"<x>"[1..-1]>\""
    case (Cond)`ends with <String x>` => (Cond)`matches <String re>`
      when String re := [String]"\"<"<x>"[1..-1]>$\""
  }
}




// assumes no cyclic imports! (Check checks for this)
start[Oracle] desugarImports(start[Oracle] oracle) {
  loc myLoc = oracle.src;
  
  for (Import imp <- oracle.top.imports) {
    loc l = resolveImport(imp, oracle);
    start[Oracle] imported = parseOracle(l);
    imported = desugarImports(imported);
    oracle = visit (oracle) {
      case (Oracle)`package <{Id "."}+ xs>; <Import* _> <Decl* ds>`
        => (Oracle)`package <{Id "."}+ xs>; 
                   '<Decl* dimp> 
                   '<Decl* ds>`
        when Decl* dimp := imported.top.decls
      case (Oracle)`<Import* _> <Decl* ds>`
        => (Oracle)`<Decl* dimp>
                   '<Decl* ds>`
        when Decl* dimp := imported.top.decls
    }
    oracle.top.src = myLoc; // visit removes it??
  }
  
  return oracle;
}

void compileToFile(start[Oracle] oracle) {
    loc l = oracle.src.top[extension="java"];
    writeFile(l, compileToJava(oracle));
}



// TODO: load default patterns from file and declare inside class
str compileToJava(start[Oracle] oracle) =
  "<if (pkg != "") {>
  'package <pkg>;
  '<}>
  'import org.testar.monkey.alayer.Verdict;
  'import org.testar.monkey.alayer.State;
  'import org.testar.monkey.alayer.Widget;
  'import org.testar.oracles.DslOracle;
  '
  'public class <className> {
  '   <for ((Decl)`pattern <Id x> = <String re>` <- pt.top.decls) {>
  '   private static String $<x>_RE = <re>;
  '   <}>
  '   <for (Decl d <- pt.top.decls, !(d is patternDef)) {>
  '   <compileDecl(d)>
  '   <}>  
  '}"
  when /^<className:.*>\.testar$/ := oracle.src.file,
    str pkg := getPackage(oracle),
    start[Oracle] pt := desugarUnless(desugarConds(desugarFlatContexts(desugarImports(oracle))));


str makeClassName(String s) {
  str name = escape("<s>"[1..-1], (":": " ", "-": " ", "?": " ", "\'": " ", "!": " ", ".": " ", "(": " ", ")": " "));
  name = squeeze(name, " ");
  return intercalate("", [ capitalize(w) | str w <- split(" ", name) ]) + "$<s.src.offset>";
}

str compileDecl((Decl)`<Assert a>`) = compileAssert(a, []);

str compileDecl((Decl)`context <String+ path> {<Decl* ds>}`) 
  = intercalate("\n", [ compileAssert(a, ctx) | (Decl)`<Assert a>` <- ds ])
  when 
    list[String] ctx := [ s | String s <- path ];

str compileAssert(Assert a, list[String] context)
  = "public static class <makeClassName(a.message)> extends DslOracle {
    '  /*
    '   <a>
    '  */
    '
    '  @Override
    '  public void initialize() {
    '     <if (context != []) {>
    '     addSectionConstraint(java.util.Arrays.asList(<intercalate(", ", [ "<s>" | String s <- context ])>));
    '     <}>
    '  }
    '
    '  @Override
    '  public String getMessage() {
    '    return <a.message>;
    '  }
    '
    '  @Override
    '  public Verdict getVerdict(State state) {
    '     Widget constraintWidget = getConstraintWidgetOrState(state);
    '     Verdict verdict = Verdict.OK;
    '     <assert2java(a)>
    '     return verdict;
    '  }
    '}"; 


str assert2java((Assert) `assert <Predicate p> <String msg>.`) 
  = pred2java(p);

str assert2java((Assert) `assert for all <Name elt>, <{Name ","}+ names> <Predicate p> <String msg>.`)
  = "<assert2java((Assert)`assert for all <Name elt> <Predicate p> <String msg>.`)>
    '<assert2java((Assert) `assert for all <{Name ","}+ names> <Predicate p> <String msg>.`)>";

str assert2java((Assert) `assert for all <Name elt> <Predicate p> <String msg>.`) 
  = "for (Widget $it: getWidgets(\"<elt>\", constraintWidget)) {
    '  <pred2java(p)>
    '}";


str widgetVar((Widget)`it`) = "$it";

// NB: also length in the name. Because of nesting
// different widgets can start at the same offset.
default str widgetVar(Widget w) = "widget$<w.src.offset>$<w.src.length>";


str condVar(Cond c) = "cond$<c.src.offset>";

str widget2java((Widget)`it`) = ""; // no decl when in for loop

str widget2java(w:(Widget)`<Name elementType> <String selector>`) = 
  "Widget <widgetVar(w)> = getWidget(\"<w.elementType>\", <w.selector>, constraintWidget);
  'if (<widgetVar(w)> == null) {
  '  return Verdict.OK;
  '}";

str widget2java(w:(Widget)`<Widget w0>.<Name fld>`) = 
  "<widget2java(w0)>
  'Object <widgetVar(w)> = getProperty(<widgetVar(w0)>, \"<fld>\");";

str offender(Widget w) = widgetVar(leftMostWidget(w));

str pred2java((Predicate)`<Widget w> <Cond c>`)
  = "<widget2java(w)>
    'boolean <condVar(c)> = <cond2java(c, widgetVar(w))>;
    'if (!<condVar(c)>) { 
    '  verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), <offender(w)>)); 
    '}
    'markAsNonVacuous();";
    // ^^^^^ note that as soon as verdict becomes fail, it stays fail.

// note how w is used as condvar for the when clause 
// even though it is on the right hand side.
str pred2java((Predicate)`<Widget w> <Cond c0> when it <Cond c>`)
  = "<widget2java(w)>
    'boolean <condVar(c)> = <cond2java(c, widgetVar(w))>;
    'if (<condVar(c)>) {
    '  boolean <condVar(c0)> = <cond2java(c0, widgetVar(w))>;
    '  if (!<condVar(c0)>) { 
    '    verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), <offender(w)>)); 
    '  }
    ' markAsNonVacuous();
    '}";

// rhs of when should not affect verdict, so don't reuse pred2java
default str pred2java((Predicate)`<Predicate p1> when <Widget w> <Cond c>`)
  = "<widget2java(w)>
    'boolean <condVar(c)> = <cond2java(c, widgetVar(w))>;
    'if (<condVar(c)>) {
    '  <pred2java(p1)>
    '}";


str cond2java((Cond)`is <Name status>`, str w)
  = "evaluateIsStatus(<w>, \"<status>\")";

str cond2java((Cond)`are <Name status>`, str w)
  = "evaluateAreStatus((java.util.List\<Object\>)<w>, \"<status>\")";

str cond2java((Cond)`is equal to <Expr e>`, str w)
  = "evaluateIsEqualTo(<w>, <expr2java(e)>)";


str cond2java((Cond)`has nonempty <Name attr>`, str w)
  = "evaluateHasAttribute(<w>, \"<attr>\")";


str cond2java((Cond)`matches <RegExp re>`, str w)
  = "evaluateMatches(<w>, \"<javaEscape(re)>\")";

str cond2java((Cond)`matches <String re>`, str w)
  = "evaluateMatches(<w>, <re>)";

str cond2java((Cond)`matches <Id x>`, str w)
  = "evaluateMatchesWithName(<w>, $<x>_RE, \"<x>\")";



str cond2java((Cond)`contains <String string>`, str w)
  = "evaluateContains(<w>, <string>)";


str cond2java((Cond)`spell checks`, str w)
  = "evaluateSpellChecks(<w>, null)";

str cond2java((Cond)`spell checks in <Id locale>`, str w)
  = "evaluateSpellChecks(<w>, \"<locale>\")";



str cond2java((Cond)`is one of [<{String ","}* ss>]`, str w)
  = "evaluateIsOneOf(<w>, java.util.Arrays.asList(<ss>))";


// todo: this is incorrect, but one should use the "" notation 
str javaEscape(RegExp re) {
  str src = "<re>"[1..-1];
  src = replaceAll(src, "\\/", "/"); // unescape forward slash
  src = replaceAll(src, "\\", "\\\\"); // escape \
  src = replaceAll(src, "\"", "\\\""); // escape "
  return src;
}
   


str cond2java((Cond)`not <Cond c>`, str w)
  = "!(<cond2java(c, w)>)";

str cond2java((Cond)`(<Cond c>)`, str w)
  = cond2java(c, w);

str cond2java((Cond)`<Cond a> and <Cond b>`, str w)
  = "(<cond2java(a, w)> && <cond2java(b, w)>)";

str cond2java((Cond)`<Cond a> or <Cond b>`, str w)
  = "(<cond2java(a, w)> || <cond2java(b, w)>)";

default str cond2java(Cond c, str w) 
  = "UNKNOWN(\"<c>\", <w>)"
  when bprintln("WARNING missing case in compiler for <c>");


str expr2java((Expr)`<String s>`) = "<s>";

str expr2java((Expr)`<Integer i>`) = "<i>";

str expr2java((Expr)`<Widget w>`) = 
  "new java.util.function.Supplier\<Object\>() {
  ' public Object get() {
  '   <widget2java(w)>
  '   return <widgetVar(w)>; 
  ' }  
  '}.get()";

test bool smokeTest() {
  start[Model] m = parse(#start[Model], |project://testar-oracle/src/main/rascal/lang/testar/testar.model|);
  list[loc] dirs = [|project://testar-oracle/examples_for_testing/|
    , |project://testar-oracle/src/main/rascal/lang/ql/examples/|];
  for (loc d <- dirs, loc x <- files(d), x.extension == "testar") {
    println("smoke testing: <x>");
    try {
      start[Oracle] ast = parseOracle(x);
      set[Message] msgs = check(ast, m);
      if (Message m <- msgs, m is error) {
        println("errors for <x>; skipping");
        iprintln(msgs);
      }
      else compileToFile(ast);
    }
    catch value x: {
      println("thrown: <x>");
      continue;
    }
  }
  return true;
}