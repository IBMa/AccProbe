<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns:xalan="http://xml.apache.org/xslt"
    exclude-result-prefixes="xalan">
    
     <xsl:template match="packages">
     <xsl:text disable-output-escaping="yes">
&lt;?NLS TYPE="org.eclipse.help.toc"?&gt;
     </xsl:text>
     <toc label="API Reference" link_to="../org.eclipse.actf.validation.doc/toc_reference.xml#api">
     <xsl:for-each select="package">
     <xsl:sort select="text()"/>
        <xsl:if test="text() != ''">
            <topic label="{text()}" href="html/api/{translate(text(),'.','/')}/package-summary.html"/>
        </xsl:if>
     </xsl:for-each>
    
     </toc> 
    </xsl:template>
</xsl:stylesheet>
