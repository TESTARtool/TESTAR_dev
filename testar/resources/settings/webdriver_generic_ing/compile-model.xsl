<?xml version="1.0"?>

<!-- should be version=3.1, but most editors cannot parse 3.1, the saxon backend doesn't complain though -->
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:s="http://testar.org/state">

    <xsl:output indent="yes"/>

    <xsl:param name="json-file"/>

    <xsl:template match="/">
        <xsl:variable name="p" select='map{ "method" : "json" }'/>
        <xsl:variable name="s" select="json-doc($json-file)"/>
        <xsl:variable name="d" select="serialize($s, $p)"/>
        <xsl:variable name="x" select="json-to-xml($d)"/>

        <!-- stage 1 -->
        <xsl:variable name="model">
            <xsl:apply-templates select="$x//*:map[@key = 'screenStructure']" mode="structure"/>
        </xsl:variable>

        <!-- stage 2 -->
        <input-rules>
            <xsl:apply-templates select="$model/*"/>
        </input-rules>
    </xsl:template>

    <xsl:template match="@* | node()">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>

    <!-- stage 2: date rule -->
    <xsl:template match="field[@type = 'date']">
        <xsl:call-template name="form-rule-1">
            <xsl:with-param name="regex">
                (01|13|22|31)/(02|07|10|12)/(1977|1984|1991|2000|2014|2021)
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>


    <!-- stage 2: alphabetical rule -->
    <xsl:template match="validator[@type = 'alphabetical']">
        <xsl:call-template name="form-rule-1">
            <xsl:with-param name="regex">
                [a-zA-Z ]{5,15}
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <!-- stage 2: alphanumeric rule -->
    <xsl:template match="validator[@type = 'alphanumeric']">
        <xsl:call-template name="form-rule-1">
            <xsl:with-param name="regex">
                [0-9a-zA-Z ]{5,15}
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <!-- stage 2: numeric rule -->
    <xsl:template match="validator[@type = 'numeric']">
        <xsl:call-template name="form-rule-1">
            <xsl:with-param name="regex">
                [0-9]{5,15}
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <!-- stage 2: telephone rule -->
    <xsl:template match="validator[@type = 'telephone']">
        <xsl:call-template name="form-rule-1">
            <xsl:with-param name="regex">
                [+]?[0-9]{10}
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <!-- stage 2: iban (nl) rule -->
    <xsl:template match="validator[@type = 'iban-nl']">
        <xsl:call-template name="form-rule-1">
            <xsl:with-param name="regex">
                (NL20 INGB 0001 2345 67)|([A-Z0-9]{4} [0-9]{4} [0-9]{3}([A-Z0-9] [A-Z0-9]{0,4} [A-Z0-9]{0,4}
                [A-Z0-9]{0,4} [A-Z0-9]{0,3}))
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <!-- stage 2: email rule -->
    <xsl:template match="validator[@type = 'email']">
        <xsl:call-template name="form-rule-1">
            <xsl:with-param name="regex">
                ([a-z0-9._%+-]{4,15}@[a-z0-9.-]{5,10}\.[a-z]{2,4})|(bla@company\.com)|(sut@bla\.nl)
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <!-- stage 2: enterprise rule -->
    <xsl:template match="validator[@type = 'enterprise']">
        <xsl:call-template name="form-rule-1">
            <xsl:with-param name="regex">
                E(0415580365)|(0[0-9]{9})
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>


    <!-- stage 2: generate form-rule (entry-point) -->
    <xsl:template name="form-rule-1">
        <xsl:param name="regex"/>
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
            <xsl:call-template name="form-rule-2">
                <xsl:with-param name="regex" select="$regex"/>
            </xsl:call-template>
        </xsl:copy>
    </xsl:template>

    <!-- stage 2: generate form-rule (xquery and xpath) -->
    <xsl:template name="form-rule-2">
        <xsl:param name="regex"/>

        <xsl:variable name="form" select="ancestor-or-self::section/@name"/>
        <xsl:variable name="name" select="ancestor-or-self::field/@name"/>

        <xsl:variable name="xquery">
            .[contains(lower-case(@name), '<xsl:value-of select="$name"/>')]/ancestor::*[contains(lower-case(@name),
            '<xsl:value-of select="$form"/>')]
        </xsl:variable>

        <input-rule xquery="{normalize-space($xquery)}" regexp="{normalize-space($regex)}"/>
    </xsl:template>

    <!-- stage 1: transform screen structure -->
    <xsl:template match="*:map[@key = 'screenStructure']" mode="structure">
        <model>
            <xsl:apply-templates select="*:array/*" mode="section"/>
        </model>
    </xsl:template>

    <!-- stage 1: transform section -->
    <xsl:template match="*:map" mode="section">
        <xsl:variable name="name" select="s:tname(*:map[@key='title']/*:string[@key='EN'], 'NONAME')"/>
        <section name="{$name}">
            <xsl:apply-templates select="*:array[@key='fields']/*" mode="field"/>
        </section>
    </xsl:template>

    <!-- stage 1: transform field -->
    <xsl:template match="*:map" mode="field">
        <xsl:variable name="type" select="*:string[@key='type']"/>
        <xsl:variable name="name" select="s:tname(*:string[@key='name'], *:string[@key='EN'])"/>

        <field name="{$name}" type="{$type}">
            <xsl:apply-templates select="*:array[@key='validators']/*" mode="validator"/>
        </field>
    </xsl:template>

    <!-- stage 1: transform validator -->
    <xsl:template match="*:map" mode="validator">
        <xsl:variable name="type" select="*:string[@key = 'type']"/>
        <validator type="{$type}"/>
    </xsl:template>

    <!-- generate name, based on two inputs (1 and 2, 1 take precedence) -->
    <xsl:function name="s:tname">
        <xsl:param name="input"/>
        <xsl:param name="input2"/>

        <xsl:variable name="i" select="if (string-length($input) > 0) then $input else $input2"/>

        <!-- encode spaces, lowercase etc -->
        <xsl:value-of select="translate(lower-case($i), ' ', '-')"/>
    </xsl:function>
</xsl:stylesheet>                                                               