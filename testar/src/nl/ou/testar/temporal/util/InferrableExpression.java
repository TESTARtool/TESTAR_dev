package nl.ou.testar.temporal.util;

public  enum InferrableExpression {

    value_eq_("number"),
    value_lt_("number"),
    textmatch_("text"),
    pathmatch_("path"),
    rolematch_("role"),
    heigth_lt_("shape"),
    width_lt_("shape"),
    textlength_eq_("text"),
    textlength_lt_("text"),
    is_blank_("boolean"),
    exists_("boolean"),
    is_deadstate_("boolean"),
    relpos_upleft_("boolean"),  //relative position in parent window
    relpos_upright_("boolean"),
    relpos_downleft_("boolean"),
    relpos_downright_("boolean"),;

    public final String typ;

    private InferrableExpression(String typ) {
        this.typ=typ;
    }
};

