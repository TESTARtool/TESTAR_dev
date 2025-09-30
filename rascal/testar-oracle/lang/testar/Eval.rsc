module lang::testar::Eval

import lang::testar::Oracle;
import lang::testar::Check; // for leftMostWidget
import String;
import ParseTree;


alias Report = list[Verdict];

data Verdict(loc \assert=|tmp://|, str msg="")
    = SUCCESS()
    | WARN()
    | FAIL(Value offender);


data Value
    = prim(value val)
    | null()
    | lst(list[Value] elts)
    | obj(str \type, str id, map[str, Value] props, str inner="");


alias Env = map[str, Value];

Report eval(start[Oracle] oracle, Value ui) = eval(oracle.top, ui);

Report eval(Oracle oracle, Value ui) 
    = [ *eval(a, ui) | Assert a <- oracle.asserts ];

/*
 * Asserts evaluate to reports
 */

Report eval(a:(Assert)`assert <Predicate p> <String msg>.`, Value ui)
    = [ eval(p, ui, ())[\assert=a.src][msg=toStr(msg)] ];

Report eval(a:(Assert)`assert for all <{Name ","}+ elts> <Predicate p> <String msg>.`, Value ui)
    = [ eval(p, ui, ("it": v))[\assert=a.src][msg=toStr(msg)] 
            | /Value v := ui, v has \type, v.\type in types ]
    when 
        set[str] types := { "<e>" | Name e <- elts };

/*
 * Predicates evaluate to verdicts
 */


Verdict eval(p:(Predicate)`<Widget w> <Cond c>`, Value ui, Env env)
    = x == null() ? WARN(msg="could not evaluate <w>") :
        (eval(c, x, ui, env) ? SUCCESS() : FAIL(eval(leftMostWidget(p), ui, env)))
    when    
        Value x := eval(w, ui, env);


Verdict eval((Predicate)`<Predicate lhs> when <Predicate rhs>`, Value ui, Env env)
    = v is FAIL ? SUCCESS() : (v is SUCCESS ? eval(lhs, ui, env) : v)
    when 
        Verdict v := eval(rhs, ui, bindLeftMostWidget(lhs, ui, env));

Verdict eval((Predicate)`<Predicate lhs> unless <Widget w> <Cond c>`, Value ui, Env env)
    = eval((Predicate)`<Predicate lhs> when <Widget w> not(<Cond c>)`, ui, env); 

Env bindLeftMostWidget(Predicate lhs, Value ui, Env env) 
    = (Widget)`it` := w ? env : env + ("it": eval(w, ui, env))
    when
        Widget w := leftMostWidget(lhs);

/*
 * Widgets evaluate to objects
 */

Value lookup(obj(str _, str _, map[str, Value] props), Name fld) 
    = "<fld>" in props ? props["<fld>"] : null();

Value eval((Widget)`<Name e> <String s>`, Value ui, Env env) = x
    when
        /x:obj(str t, str id, map[str, Value] _) := ui,
        t == "<e>", id == toStr(s);

Value eval((Widget)`<Widget w>.<Name fld>`, Value ui, Env env) = lookup(v, fld)
    when
        Value v := eval(w, ui, env), v != null(); // should always be obj()

Value eval((Widget)`it`, Value ui, Env env) = env["it"];

default Value eval(Widget _, Value _, Env _) = null();

/*
 * Conditions evaluate to booleans
 */

bool eval((Cond)`is <Name status>`, Value w, Value ui, Env env) 
    = prim(true) := lookup(w, status);

bool eval((Cond)`are <Name status>`, lst(list[Value] vs), Value ui, Env env) 
    = all(Value v <- vs, prim(true) := lookup(v, status));

bool eval((Cond)`has nonempty <Name x>`, Value w, Value ui, Env env)
    = v != null() && prim("") !:= v
    when 
        Value v := lookup(w, x);

bool eval((Cond)`matches <String re>`, Value w, Value ui, Env env)
    = rexpMatch(textOf(w), toStr(re));


bool eval((Cond)`contains <String x>`, Value w, Value ui, Env env)
    = contains(textOf(w), toStr(x));

bool eval((Cond)`is one of [<{String ","}* ss>]`, Value w, Value ui, Env env)
    = textOf(w) in [ toStr(s) | String s <- ss ];

bool eval((Cond)`is equal to <Expr e>`, Value w, Value ui, Env env)
    = w == eval(e, ui, env);


bool eval((Cond)`(<Cond c>)`, Value w, Value ui, Env env) = eval(c, w, ui, env);

bool eval((Cond)`not <Cond c>`, Value w, Value ui, Env env) = !eval(c, w, ui, env);

bool eval((Cond)`<Cond lhs> and <Cond rhs>`, Value w, Value ui, Env env) 
    = eval(lhs, w, ui, env) && eval(rhs, w, ui, env);

bool eval((Cond)`<Cond lhs> or <Cond rhs>`, Value w, Value ui, Env env) 
    = eval(lhs, w, ui, env) || eval(rhs, w, ui, env);

default bool eval(Cond c, Value w, Value ui, Env env) = false;

str textOf(prim(str x)) = x;

str textOf(x:obj(str _, str _, map[str, Value] _)) = x.inner;

/*
 * Expressions evaluate to values
 */

Value eval((Expr)`<String s>`, Value _, Env _) = prim(toStr(s));

Value eval((Expr)`<Integer n>`, Value _, Env _) = prim(toInt("<n>"));

Value eval((Expr)`<Widget w>`, Value ui, Env env) = eval(w, ui, env);

default Value eval(Expr e) {
    throw "unsupported expression <e>";
}

/*
 * Unit tests
 */

test bool testSimpleSuccess()
    = [SUCCESS()] := 
        eval((Assert)`assert button "x" has nonempty label 
                     ' "button x cannot have empty label".`
        , obj("button", "x", ("label":prim("Hello"))));


test bool testSimpleFail()
    = [FAIL(ui)] :=
        eval((Assert)`assert button "x" has nonempty label 
                     ' "button x cannot have empty label".`, ui)
    when 
        Value ui := obj("button", "x", ());


test bool testForAllFailOne()
    = [FAIL(obj("button", "x", _)), SUCCESS()] :=
        eval((Assert)`assert for all button it has nonempty label 
                     ' "buttons cannot have empty label".`, ui)
    when 
        Value ui := obj("panel", "", ("kids":
            lst([
                    obj("button", "x", ()), 
                    obj("button", "y", ("label": prim("Hello")))
            ])));
            
test bool testMatchesFails() 
    = [FAIL(ui)] :=
        eval((Assert)`assert button "x" matches "^[a-z]+$"
                     '  "button matches".`, ui)
    when   
        Value ui := obj("button", "x", (), inner="Hello");

test bool testMatchesSuccess() 
    = [SUCCESS()] :=
        eval((Assert)`assert button "x" matches "^[A-Za-z]+$"
                     '  "button matches".`, ui)
    when   
        Value ui := obj("button", "x", (), inner="Hello");

test bool testContainsFails() 
    = [FAIL(ui)] :=
        eval((Assert)`assert button "x" contains "Heaven"
                     '  "button".`, ui)
    when   
        Value ui := obj("button", "x", (), inner="Hello");

test bool testContainsSuccess() 
    = [SUCCESS()] :=
        eval((Assert)`assert button "x" contains "Hell"
                     '  "button".`, ui)
    when   
        Value ui := obj("button", "x", (), inner="Hello");



test bool testWhenClause() 
    = [SUCCESS()] :=
        eval((Assert)`assert button "x" contains "Hell"
                     '  when it has nonempty label
                     '  "button".`, ui)
    when   
        Value ui := obj("button", "x", (), inner="Hello");

test bool testWhenClauseFail() 
    = [FAIL(ui)] :=
        eval((Assert)`assert button "x" contains "Heaven"
                     '  when it has nonempty label
                     '  "button".`, ui)
    when   
        Value ui := obj("button", "x", ("label": prim("nonempty")), inner="Hello");


test bool testAndFail() 
    = [FAIL(ui)] :=
        eval((Assert)`assert button "x" contains "Heaven"
                     '  and has nonempty label
                     '  "button".`, ui)
    when   
        Value ui := obj("button", "x", ("label": prim("nonempty")), inner="Hello");

test bool testAndSuccess() 
    = [SUCCESS()] :=
        eval((Assert)`assert button "x" contains "Hell"
                     '  and has nonempty label
                     '  "button".`, ui)
    when   
        Value ui := obj("button", "x", ("label": prim("nonempty")), inner="Hello");


test bool testOrFail() 
    = [FAIL(ui)] :=
        eval((Assert)`assert button "x" contains "Heaven"
                     '  or has nonempty label
                     '  "button".`, ui)
    when   
        Value ui := obj("button", "x", (), inner="Hello");

test bool testOrSuccess() 
    = [SUCCESS()] :=
        eval((Assert)`assert button "x" contains "Heaven"
                     '  or has nonempty label
                     '  "button".`, ui)
    when   
        Value ui := obj("button", "x", ("label": prim("nonempty")), inner="Hello");


test bool testAreSuccess()
    = [SUCCESS()] :=
        eval((Assert)`assert for all panel it.kids are enabled 
                     ' "enabled".`, ui)
    when 
        Value ui := obj("panel", "", ("kids":
            lst([
                    obj("button", "x", ("enabled": prim(true))), 
                    obj("button", "y", ("enabled": prim(true)))
            ])));

test bool testAreFail()
    = [FAIL(ui)] :=
        eval((Assert)`assert for all panel it.kids are enabled 
                     ' "enabled".`, ui)
    when 
        Value kids := lst([
                    obj("button", "x", ("enabled": prim(false))), 
                    obj("button", "y", ("enabled": prim(true)))
            ]),
        Value ui := obj("panel", "", ("kids": kids));
        
test bool testIsEqualToSuccess() 
    = [SUCCESS()] :=
        eval((Assert)`assert for all button
                     '  it.placeholder is equal to it.tooltip
                     '  "placeholder and tooltip must be equal".`, ui)
    when
        Value ui := obj("button", "x", 
            ("placeholder": prim("yes"),
             "tooltip": prim("yes")));

test bool testIsEqualToFail() 
    = [FAIL(ui)] :=
        eval((Assert)`assert for all button
                     '  it.placeholder is equal to it.tooltip
                     '  "placeholder and tooltip must be equal".`, ui)
    when
        Value ui := obj("button", "x", 
            ("placeholder": prim("yes"),
             "tooltip": prim("no")));