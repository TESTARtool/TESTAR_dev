<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:s="http://testar.org/state">

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="*">
        <abstract-state>
            <gui>
                <path>
                    <xsl:apply-templates select="ancestor-or-self::*" mode="path"/>
                </path>
            </gui>
        </abstract-state>
    </xsl:template>

    <xsl:template match="*" mode="path">
        <xsl:copy>
            <xsl:attribute name="i">
                <xsl:value-of select="count(preceding-sibling::*)"/>
            </xsl:attribute>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>