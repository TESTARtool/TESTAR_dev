package nl.ou.testar.temporal.proposition;

public  enum InferrableExpression {

    value_eq("number"),
    value_lt("number"),
    textmatch("text"),
    heigth_lt("shape"),
    width_lt("shape"),
    textlength_eq("text"),
    textlength_lt("text"),
    is_blank("boolean"),
    exists("boolean"),
    //is_deadstate_("boolean"), //not used anymore, deadstate requires special handling
    ;

    public final String typ;

    InferrableExpression(String typ) {
        this.typ=typ;
    }
}

