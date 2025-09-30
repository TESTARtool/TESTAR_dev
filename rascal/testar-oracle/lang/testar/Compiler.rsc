module lang::testar::Compiler

import lang::testar::Oracle;
import lang::testar::Check;
import lang::testar::Model;
import IO;
import ParseTree;
import String;
import util::FileSystem;


start[Oracle] desugarUnless(start[Oracle] oracle) {
  return visit (oracle) {
    case (Predicate)`<Predicate x> unless <Widget w> <Cond c>`
      => (Predicate)`<Predicate x> when <Widget w> not(<Cond c>)`
  }
}

void compileToFile(start[Oracle] oracle) {
    loc l = oracle.src.top[extension="java"];
    writeFile(l, compileToJava(oracle));
}


str getPackage(start[Oracle] oracle) {
  if ((Oracle)`package <{Id "."}+ ids>; <Assert* _>` := oracle.top) {
    return "<ids>";
  }
  return "";
}

str compileToJava(start[Oracle] oracle) =
  "<if (pkg != "") {>
  'package <pkg>;
  '<}>
  'import org.testar.monkey.alayer.Verdict;
  'import org.testar.monkey.alayer.State;
  'import org.testar.monkey.alayer.Widget;
  'import org.testar.oracles.Oracle;
  '
  'public class <className> {
  '   <for (Assert a <- desugarUnless(oracle).top.asserts) {>
  '   <compileAssert(a)>
  '   <}>  
  '}"
  when /^<className:.*>\.testar$/ := oracle.src.file,
    str pkg := getPackage(oracle);


str makeClassName(String s) {
  str name = escape("<s>"[1..-1], (":": " ", "-": " ", "?": " ", "\'": " ", "!": " ", ".": " ", "(": " ", ")": " "));
  name = squeeze(name, " ");
  return intercalate("", [ capitalize(w) | str w <- split(" ", name) ]) + "$<s.src.offset>";
}

str compileAssert(Assert a)
  = "public static class <makeClassName(a.message)> implements Oracle {
    '  /*
    '   <a>
    '  */
    '
    '  @Override
    '  public void initialize() { }
    '
    '  @Override
    '  public String getMessage() {
    '    return <a.message>;
    '  }
    '
    '  @Override
    '  public Verdict getVerdict(State state) {
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
  = "for (Widget $it: getWidgets(\"<elt>\", state)) {
    '  <pred2java(p)>
    '}";


str widgetVar((Widget)`it`) = "$it";

// NB: also length in the name. Because of nesting
// different widgets can start at the same offset.
default str widgetVar(Widget w) = "widget$<w.src.offset>$<w.src.length>";


str condVar(Cond c) = "cond$<c.src.offset>";

str widget2java((Widget)`it`) = ""; // no decl when in for loop

str widget2java(w:(Widget)`<Name elementType> <String selector>`) = 
  "Widget <widgetVar(w)> = getWidget(\"<w.elementType>\", <w.selector>, state);
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
    'if (!<condVar(c)>) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), <offender(w)>)); }";
    // ^^^^^ note that as soon as verdict becomes fail, it stays fail.

// note how w is used as condvar for the when clause 
// even though it is on the right hand side.
str pred2java((Predicate)`<Widget w> <Cond c0> when it <Cond c>`)
  = "<widget2java(w)>
    'boolean <condVar(c)> = <cond2java(c, widgetVar(w))>;
    'if (<condVar(c)>) {
    '  boolean <condVar(c0)> = <cond2java(c0, widgetVar(w))>;
    '  if (!<condVar(c0)>) { verdict = verdict.join(new Verdict(Verdict.Severity.FAIL, getMessage(), <offender(w)>)); }
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



str cond2java((Cond)`contains <String string>`, str w)
  = "evaluateContains(<w>, <string>)";

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
    start[Oracle] ast = parse(#start[Oracle], x);
    set[Message] msgs = check(ast, m);
    if (Message m <- msgs, m is error) {
      println("errors for <x>; skipping");
      iprintln(msgs);
    }
    else compileToFile(ast);
  }
  return true;
}