<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!--  xmlns="http://www.w3.org/1999/xhtml" -->
<xsl:output method="xml" omit-xml-declaration="yes" media-type="text/html"/>

<xsl:template match="*[local-name()!='property']">
<ul class="{local-name()}">
  <table>
  <xsl:for-each select="property">
  <tr>
    <th><xsl:value-of select="@name"/></th>
    <td><xsl:value-of select="."/></td>
  </tr>
  </xsl:for-each>
  </table>
  <xsl:apply-templates select="*[local-name()!='property']"/>
</ul>
</xsl:template>

</xsl:stylesheet>
