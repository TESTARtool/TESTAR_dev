module lang::testar::Oracle

extend lang::std::Layout;
extend lang::std::Id;

import String;
import ParseTree;

start syntax Oracle 
  = Package? pkgOpt Assert* asserts;

syntax Package = "package" {Id "."}+ elts ";" ;

syntax Assert 
  = "assert" ForAll? Predicate pred String message ".";

syntax ForAll = "for" "all" {Name ","}+ elt;

syntax Predicate
  = Widget widget Cond cond
  // | "any" Name Predicate
  // | "all" Name Predicate
  | non-assoc (
      Predicate "when" Predicate
    | Predicate "unless" Predicate
  );

syntax Widget 
  = Name elementType String selector
  | "it"
  | Widget "." Name field
  ;

syntax Expr
  = String
  | Integer
  | Widget
  ;

lexical Integer = [0-9]+ !>>[0-9];

syntax Cond
  = "is" Name status
  | "are" Name status
  | "has" "nonempty" Name attr
  | "contains" String string
  | "matches" RegExp regexp // deprecated
  | "matches" String javaRegexp
  | "is" "one" "of" "[" {String ","}* strings "]"
  | "is" "equal" "to" Expr
  | bracket "(" Cond ")"
  | "not" Cond cond 
  > left Cond lhs "and" Cond rhs
  > left Cond lhs "or" Cond rhs
  ;

lexical Name = Id \ Reserved;

keyword Reserved = "is" | "has" | "contains" | "matches" | "one" | "of"
  | "not" | "and" | "or" | "when" | "unless" | "assert" | "for" | "all" | "it";


lexical String = @category="string" "\"" StrChar* "\"";

lexical StrChar= ![\"\\\n] | [\\][tbnrfs\'\"\\];

lexical RegExp = @category="regexp" "/" RegExpChar* "/";

lexical RegExpChar = ![/\n\\] | [\\][/] | [\\] !>> [/] ;


str unescapeJava(str s) {
  return escape(s, (
      "\\t": "\t",
      "\\b": "\b",
      "\\n": "\n",
      "\\r": "\r",
      "\\f": "\f",
      "\\s": " ",
      "\\\'": "\'",
      "\\\"": "\"",
      "\\\\": "\\"
    ));
}

str toStr(String s) = unescapeJava("<s>"[1..-1]);


start[Oracle] parseOracle(loc l) = parse(#start[Oracle], l);