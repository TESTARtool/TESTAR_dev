package nl.ou.testar.temporal.proposition;

/**
 * Used by the Proposition Manager to make filters on widgets.
 */
public  enum InferrableExpression {

    value_eq("number"),
    value_lt("number"),
    textmatch("text"),
    heigth_lt("shape"),
    width_lt("shape"),
    anchorx_lt("shape"),
    anchory_lt("shape"),
    textlength_eq("text"),
    textlength_lt("text"),
    is_blank("boolean"),
    exists("boolean"),
    //is_deadstate_("boolean"), //not used anymore, deadstate requires special handling
    ;

    public final String typ;

    /**
     * @param typ the type of the expression. Can be one of number,text,shape,boolean
     */
    InferrableExpression(String typ) {
        this.typ=typ;
    }
}

