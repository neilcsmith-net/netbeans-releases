<?xml version="1.0"?>
<!--
                 Sun Public License Notice
 
 The contents of this file are subject to the Sun Public License
 Version 1.0 (the "License"). You may not use this file except in
 compliance with the License. A copy of the License is available at
 http://www.sun.com/
 
 The Original Code is NetBeans. The Initial Developer of the Original
 Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 Microsystems, Inc. All Rights Reserved.

-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:include href="library.xsl"/>

<xsl:template match="/">
	<xsl:call-template name="html-page">
		<xsl:with-param name="html-title">TestBag <xsl:value-of select="/TestBag/@name"/></xsl:with-param>
	</xsl:call-template>
</xsl:template>


<xsl:template match="TestBag">
	<H2>Test Bag: <xsl:value-of select="@name"/></H2>	
	<xsl:call-template name="summary-table">
			<xsl:with-param name="table-width">95%</xsl:with-param>
	</xsl:call-template>
	<UL>
	<xsl:if test="@testAttributes">
		<LI>Attributes: <xsl:value-of select="@testAttribs"/></LI>
	</xsl:if>
	<LI>Executor: <xsl:value-of select="@executor"/></LI>
	<LI>Module: <xsl:value-of select="@module"/></LI>
	<LI>Test Type: <xsl:value-of select="@testType"/></LI>
	<xsl:if test="string(@executor)='ide'">
		<LI><A HREF="../sys/ide">IDE User Directory</A></LI>
	</xsl:if>
	<xsl:if test="@unexpectedFailure">
		<LI><B><FONT color="#FF0000">!!!<xsl:value-of select="@unexpectedFailure"/>!!!</FONT></B></LI>
	</xsl:if>
	</UL>
	<HR/>
	<H2>Suites:</H2>
		<TABLE width="95%" cellspacing="2" cellpadding="5" border="0">	
			<xsl:call-template name="testsuite-summary-header"/>
			<xsl:apply-templates select="UnitTestSuite">
				<xsl:sort select="@name"/>
			</xsl:apply-templates>	
		</TABLE>
</xsl:template>

<xsl:template match="UnitTestSuite">
	<xsl:call-template name="testsuite-summary-row"/>
</xsl:template>


</xsl:stylesheet>