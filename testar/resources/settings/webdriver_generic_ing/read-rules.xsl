<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output indent="yes"/>

    <xsl:param name="json-file"/>

    <xsl:template match="/">
        <xsl:variable name="p" select='map{ "method" : "json" }'/>
        <xsl:variable name="s" select="json-doc($json-file)"/>
        <xsl:variable name="d" select="serialize($s, $p)"/>
        <xsl:variable name="x" select="json-to-xml($d)"/>

        <root>
            <xsl:apply-templates select="$x/*:map/*:array[@key = 'ignore-filter']/*" mode="ignore-filter"/>
            <xsl:apply-templates select="$x/*:map/*:array[@key = 'generic-input']/*" mode="generic-input"/>
            <xsl:apply-templates select="$x/*:map/*:array[@key = 'action-priority']/*" mode="action-priority"/>
            <xsl:apply-templates select="$x/*:map/*:array[@key = 'oracle']/*" mode="oracle"/>

        </root>
    </xsl:template>

    <xsl:template match="*:map" mode="ignore-filter">
        <ignore-filter
                doc="{*:array[@key = 'doc']/*:string}"
                xquery="{*:array[@key = 'xquery']/*:string}"
        />
    </xsl:template>

    <xsl:template match="*:map" mode="generic-input">
        <generic-input
                doc="{*:array[@key = 'doc']/*:string}"
                xquery="{*:array[@key = 'xquery']/*:string}"
                regexp="{*:array[@key = 'regexp']/*:string}"
                priority="{*:array[@key = 'priority']/*:string}"
        />
    </xsl:template>

    <xsl:template match="*:map" mode="action-priority">
        <action-priority
                doc="{*:array[@key = 'doc']/*:string}"
                xquery="{*:array[@key = 'xquery']/*:string}"
                priority="{*:array[@key = 'priority']/*:string}"
        />
    </xsl:template>

    <xsl:template match="*:map" mode="oracle">
        <oracle
                xquery="{*:array[@key = 'xquery']/*:string}"
                doc="{*:array[@key = 'doc']/*:string}"
                severity="{*:array[@key = 'severity']/*:string}"
        />
    </xsl:template>
</xsl:stylesheet>