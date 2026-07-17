module lang::testar::Check

import lang::testar::Oracle;
import lang::testar::Model;
import lang::testar::util::Locale;

import ParseTree;
import Message;
import List;
import String;
import IO;
import util::FileSystem;

syntax Type 
  = "*unknown*"
  | assoc Type "&" Type // intersection
  ;

// map from variable to type + the model
alias TEnv = tuple[map[str, Type] env, start[Model] model];

str getPackage(start[Oracle] oracle) {
  if ((Oracle)`package <{Id "."}+ ids>; <Import* _> <Decl* _>` := oracle.top) {
    return "<ids>";
  }
  return "";
}

loc resolveImport((Import)`import <{Id "."}+ ids>;`, start[Oracle] importer) {
  loc myLoc = importer.top.src;
  //println("desugaring imports: <myLoc>");
  str myPkg = getPackage(importer);
  str myDir = replaceAll(myPkg, ".", "/");
  //println("myDir: <myDir> (<myPkg>)");
  str myRoot = "";
  if (/^<root:.*>\/<myDir>/ := myLoc.path) {
    myRoot = root;
  }
  //println("myRoot: <myRoot>");
  str path = myRoot + "/" + intercalate("/", [ "<x>" | Id x <- ids ]) + ".testar";
  //println("path = <path>");
  loc l = importer.top.src[path=path].top;
  return l;
}


set[Message] check(start[Oracle] oracle, start[Model] model) 
  = check(oracle.top, model) + checkRegexps(oracle)
  + checkNestedContexts(oracle);

set[Message] checkNestedContexts(start[Oracle] oracle)
  = { error("cannot nest contexts", d.src) |
      /Decl ctx := oracle, ctx is context, /Decl d := ctx.decls, d is context || d is flatContext };


tuple[lrel[str, String], set[Message]] collectRegexps(start[Oracle] oracle, set[loc] seen = {}) {
  set[Message] msgs = {};
  lrel[str, String] res = [ <"<x>", re> | /(Decl)`pattern <Id x> = <String re>` := oracle ];
  seen += {oracle.src.top};

  for (Import imp <- oracle.top.imports) {
    loc l = resolveImport(imp, oracle);
    if (l in seen) {
      msgs += {error("cyclic import", imp.src)};
      continue; // skip it
    }
    if (!exists(l)) {
      msgs += {error("cannot find import", imp.src)};
      continue;
    }
    try {
      start[Oracle] imported = parseOracle(l);
      <res2, msgs2> = collectRegexps(imported, seen = seen);
      res += res2;
      msgs += msgs2;
    }
    catch ParseError(value x): {
      msgs += {error("cannot parse import: <x>", imp.src)};
    }
  }
  return <res, msgs>;
}

set[Message] checkRegexps(start[Oracle] oracle) {
  <regexps, msgs> = collectRegexps(oracle);
  msgs += { error("undefined regexp", x.src) | /(Cond)`matches <Id x>` := oracle, "<x>" notin regexps<0> };

  msgs += { *checkJavaRegex(re) | <_, String re> <- regexps };

  set[str] seen = {};
  for (<str x, String re> <- regexps) {
    if (x in seen) {
      msgs += {error("duplicate pattern definition", re.src)};
    }
    seen += {x};
  }
  
  return msgs;
}

set[Message] check(Oracle oracle, start[Model] model) 
  = { *check(d, model) | Decl d <- oracle.decls };

set[Message] check((Decl)`<Assert a>`, start[Model] model) = check(a, model);

set[Message] check((Decl)`context <String+ _> {<Decl* ds>}`, start[Model] model) 
  = { *check(d, model) | Decl d <- ds };

default set[Message] check(Decl _, start[Model] _) = {};

/*
 * Typing widgets
 */


Type typeOf((Widget)`it`, TEnv env) = env.env["it"]
  when "it" in env.env;

Type typeOf((Widget)`<Id t> <String _>`, TEnv env) = (Type)`<Id t>`;

Type typeOf((Widget)`<Widget w>.<Name fld>`, TEnv env) = t2
  when 
    Type t := typeOf(w, env),
    Type t2 := lookup(t, fld, env.model);

default Type typeOf(Widget _, TEnv _) = (Type)`*unknown*`;

/*
 * Looking up types and their properties in the UI model
 */


Type lookup((Type)`list[<Type _>]`, (Name)`length`, start[Model] _) = (Type)`int`;

Type lookup((Type)`list[<Type _>]`, (Name)`empty`, start[Model] _) = (Type)`bool`;

Type lookup((Type)`<Id t>`, (Name)`<Id fld>`, start[Model] m) = t2
  when 
    (Record)`<Id x> {<Field* flds>}` <- m.top.records, t := x,
    (Field)`<Id fld2>: <Type t2>` <- flds, fld := fld2;

default Type lookup(Type _, Name _, start[Model] _) = (Type)`*unknown*`;

// looking up in the intersection of multiple types
// this is needed for `assert for all a, b, c...`: the looked up property
// must be present in all of a, b, c etc. and have the same type.
// if they're both unknown, it will be unknown
// if they're different, we go to the default case (above)
// if they're the same we return ft1 (which can be unknown)
Type lookup((Type)`<Type t1> & <Type t2>`, Name fld, start[Model] m) = ft1
  when
    Type ft1 := lookup(t1, fld, m),
    Type ft2 := lookup(t2, fld, m),
    ft1 := ft2;

/*
 * Checking asserts
 */


bool isEmpty(String s) = (String)`""` := s;

set[Message] checkMessage(String msg) 
  = { warning("empty message", msg.src) | isEmpty(msg) }
  + { error("message should start with letter", msg.src) | !isEmpty(msg), /^[a-zA-Z]/ !:= "<msg>"[1..-1] };

set[Message] checkElement((Name)`<Id e>`, start[Model] model) 
  = { builtinErrMsg("element type", elts, e.src) | list[str] elts := getElements(model), "<e>" notin elts };

list[str] getElements(start[Model] m) = [ "<e>" | (Record)`<Id e> { <Field* _> }` <- m.top.records ];

Type isect((ForAll)`for all <Id t>`) = (Type)`<Id t>`;

Type isect((ForAll)`for all <Id t>, <{Name ","}+ ts>`) = (Type)`<Id t> & <Type t2>`
  when 
    Type t2 := isect((ForAll)`for all <{Name ","}+ ts>`);

set[Message] check((Assert)`assert for all <{Name ","}+ ts> <Predicate p> <String msg>.`, start[Model] model) 
  = check(p, <("it": t), model>) + checkMessage(msg)
  + { *checkElement(x, model) | Name x <- ts }
  + { warning("widget overrides \'it\'", w.src) | Widget w := leftMostWidget(p), (Widget)`it` !:= w }
  when 
    Type t := isect((ForAll)`for all <{Name ","}+ ts>`);

set[Message] check((Assert)`assert <Predicate p> <String msg>.`, start[Model] model) 
  = check(p, <(), model>) + checkMessage(msg);


Widget leftMostWidget((Predicate)`<Widget w> <Cond _>`) = leftMostWidget(w);

Widget leftMostWidget((Predicate)`<Predicate lhs> when <Predicate rhs>`) = leftMostWidget(lhs);

Widget leftMostWidget((Predicate)`<Predicate lhs> unless <Predicate rhs>`) = leftMostWidget(lhs);

Widget leftMostWidget((Widget)`<Widget w>.<Name fld>`) = leftMostWidget(w);

default Widget leftMostWidget(Widget w) = w;




/* 
 * Checking widgets
 */

set[Message] check(w:(Widget)`<Name e> <String x>`, TEnv env)
  = checkElement(e, env.model)
   + { warning("empty element identifier", x.src) | (String)`""` := x };

set[Message] check(w:(Widget)`it`, TEnv env)
  = { error("unbound `it` reference", w.src) | "it" notin env.env };

set[Message] check((Widget)`<Widget w>.<Name fld>`, TEnv env) 
  = check(w, env)
  + { error("undefined field", fld.src) | (Type)`*unknown*` := lookup(typeOf(w, env), fld, env.model) };

/*
 * Checking predicates
 */

set[Message] check(p:(Predicate)`<Widget w> <Cond cond>`, TEnv env)
  = check(w, env) + check(cond, typeOf(w, env), env);
 
set[Message] check(p:(Predicate)`<Predicate lhs> when <Predicate rhs>`, TEnv env)
  = check(lhs, env) + check(rhs, bindIt(lhs, env));

set[Message] check(p:(Predicate)`<Predicate lhs> unless <Predicate rhs>`, TEnv env)
  = check(lhs, env) + check(rhs, bindIt(lhs, env));


TEnv bindIt((Predicate)`it <Cond _>`, TEnv env) = env;

TEnv bindIt((Predicate)`<Id e> <String _> <Cond _>`, TEnv env) = env[env=("it": (Type)`<Id e>`)];

TEnv bindIt((Predicate)`<Widget w>.<Name _> <Cond c>`, TEnv env) 
  = bindIt((Predicate)`<Widget w> <Cond c>`, env); 



/*
 * Checking conditions
 */

// helper functions
Message builtinErrMsg(str key, list[str] names, loc l) = error("invalid <key>; must be one of:\n<toL(names)>", l);

str toL(list[str] names) = intercalate("\n", [ "- <x>" | str x <- names ]);


set[Message] check((Cond)`has nonempty <Name a>`, Type t, TEnv env)
  = { error("undefined property for type <t>", a.src) | (Type)`*unknown*` := lookup(t, a, env.model) };

set[Message] check((Cond)`is <Name s>`, Type t, TEnv env) 
  = { error("undefined property for type <t>", s.src) | (Type)`*unknown*` := lookup(t, s, env.model) }
  + { error("property <s> is not boolean", s.src) | (Type)`bool` !:= lookup(t, s, env.model) };

set[Message] check(c:(Cond)`are <Name s>`, Type t, TEnv env) 
  = { error("expected list, not <t>", c.src) | (Type)`list[<Type _>]` !:= t }
  + { error("undefined property for type <elt>", s.src)
    | (Type)`list[<Type elt>]` := t, (Type)`*unknown*` := lookup(elt, s, env.model) }
  + { error("property <s> is not boolean", s.src) | 
        (Type)`list[<Type elt>]` := lookup(t, s, env.model), 
        (Type)`bool` !:= lookup(elt, s, env.model) };
    

bool isStrOrElement(Type t) = (Type)`str` := t || isElement(t);

bool isElement((Type)`<Id _>`) = true;

bool isElement((Type)`<Type t1> & <Type t2>`) = isElement(t1) && isElement(t2);

default bool isElement(Type _) = false;


// matching, contains and is-one-of also work on elements (their contents?).
set[Message] check(c:(Cond)`matches <RegExp r>`, Type t, TEnv env) 
  = { error("expected string or element, not <t>", c.src) | !isStrOrElement(t) }
  + { warning("deprecated regular expression syntax;\nuse the Java notation within \"\"", r.src)};

set[Message] checkJavaRegex(String re) {
  try {
    str jre = "<re>"[1..-1];
    jre = unescapeJava(jre);
    rexpMatch("", jre); // we try to compile the regexp to check it conforms to java
    return {};
  }
  catch Java("PatternSyntaxException", str msg): {
    return {error(msg, re.src)};
  }  
}

set[Message] check(c:(Cond)`matches <String re>`, Type t, TEnv env) 
  = { error("expected string or element, not <t>", c.src) | !isStrOrElement(t) }
  + checkJavaRegex(re);

set[Message] check(c:(Cond)`matches <Id _>`, Type t, TEnv env) = {}; 

set[Message] check(c:(Cond)`contains <String _>`, Type t, TEnv env) 
  = { error("expected string or element, not <t>", c.src) | !isStrOrElement(t) };

set[Message] check(c:(Cond)`starts with <String _>`, Type t, TEnv env) = {};

set[Message] check(c:(Cond)`ends with <String _>`, Type t, TEnv env) = {};

set[Message] check(c:(Cond)`spell checks`, Type t, TEnv env) = {};

set[Message] check(c:(Cond)`spell checks in <Id locale>`, Type t, TEnv env) 
  = {error("unknown locale", locale.src) | "<locale>" notin LANGUAGE_BY_LOCALE };


set[Message] check(c:(Cond)`is one of [<{String ","}* ss>]`, Type t, TEnv env) 
  = { warning("empty list of strings", c.src) | [] == [ s | String s <- ss] }
  + { error("expected string, not <t>", c.src) | !isStrOrElement(t) };


set[Message] check(c:(Cond)`is equal to <Expr e>`, Type t, TEnv env) 
  = { error("expected <t2>, not <t>", c.src) | Type t2 := typeOf(e, env), t2 !:= t };


set[Message] check((Cond)`<Cond lhs> and <Cond rhs>`, Type t, TEnv env)
  = check(lhs, t, env) + check(rhs, t, env);
  
set[Message] check((Cond)`<Cond lhs> or <Cond rhs>`, Type t, TEnv env)
  = check(lhs, t, env) + check(rhs, t, env);
  
set[Message] check((Cond)`not <Cond c>`, Type t, TEnv env) = check(c, t, env);
  
set[Message] check((Cond)`(<Cond c>)`, Type t, TEnv env) = check(c, t, env);

default set[Message] check(Cond c, Type t, TEnv env)
  = {warning("missing case in type checker (type = <t>)!", c.src)};


/*
 * Type of expressions
 */


Type typeOf((Expr)`<String  _>`, TEnv env) = (Type)`str`;

Type typeOf((Expr)`<Integer _>`, TEnv env) = (Type)`int`;

Type typeOf((Expr)`<Widget w>`, TEnv env) = typeOf(w, env);

default Type typeOf(Expr _, TEnv _) = (Type)`*unknown*`;






test bool smokeTestChecker() {
  start[Model] m = parse(#start[Model], |project://testar-oracle/src/main/rascal/lang/testar/testar.model|);
  for (loc x <- files(|project://testar-oracle/examples_for_testing/|), x.extension == "testar") {
    try {
      start[Oracle] ast = parseOracle(x);
      set[Message] msgs = check(ast, m);
      if (msgs != {}) {
        println("errors/warnings for <x>:");
        iprintln(msgs);
      }
    }
    catch value x: {
      println("thrown: <x>");
    }
  }
  return true;
}