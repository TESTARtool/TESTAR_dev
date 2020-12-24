<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:s="http://testar.org/state">
    
    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="body">
        <abstract-state>
            <xsl:apply-templates select="@*|node()"/>
        </abstract-state>
    </xsl:template>
    
    <xsl:template match="@*|node()">
        <xsl:apply-templates select="@*|node()"/>
    </xsl:template>

    <xsl:template match="ing-flow">
        <ing-flow-non-gui>
            <xsl:apply-templates select="s:customState/*"  mode="non-gui"/>
        </ing-flow-non-gui>
        <xsl:apply-templates select="@*|node()"/>
    </xsl:template>

    <xsl:template match="form">
        <gui-form>
            <ing-path>
                <xsl:apply-templates select="ancestor::*" mode="path"/>
            </ing-path>
            <elements>
                <xsl:apply-templates select="@*|node()" mode="gui"/>
            </elements>
        </gui-form>
    </xsl:template>

    <xsl:template match="*[starts-with(name(), 'ing')]" mode="path">
        <xsl:copy>
            <xsl:attribute name="i">
                <xsl:value-of select="count(preceding-sibling::*)"/>
            </xsl:attribute>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="div" mode="path"/>

    <xsl:template match="s:node" mode="non-gui">
        <node>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="*" mode="non-gui"/>
        </node>
    </xsl:template>
    
    <xsl:template match="@*|node()" mode="gui">
        <xsl:apply-templates select="@*|node()" mode="gui"/>
    </xsl:template>

    <xsl:template match="input[@type = 'radio']" mode="gui">
        <xsl:copy>
            <xsl:copy-of select="@name"/>
            <xsl:apply-templates select="@*|node()" mode="gui"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="input[@type = 'text']" mode="gui">
        <xsl:copy>
            <xsl:copy-of select="@name"/>
            <xsl:apply-templates select="@*|node()" mode="gui"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>