module lang::testar::Model

extend lang::std::Layout;
extend lang::std::Id;


start syntax Model = Record* records;

syntax Record = Id name "{" Field* fields "}";

syntax Field = Id name ":" Type ;

syntax Type 
    = "bool" | "str" | "int" | "color"
    | "list" "[" Type "]" 
    | Id \ "bool" \ "int" \ "str" \ "color";
