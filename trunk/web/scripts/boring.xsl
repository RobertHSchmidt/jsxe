<?xml version="1.0" encoding="UTF-8"?>
<!--
XSL stylesheet for generating jsXe's webpage.
:indentSize=2:tabSize=2:noTabs=true:
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="html" encoding="UTF-8"
    doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
    doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
    omit-xml-declaration="no"/>

<xsl:template match="page">


<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<!-- {{{ head -->
  <head>
    <title>jsXe: <xsl:value-of select="title"/></title>
    <!-- <base href="http://jsxe.sourceforge.net/"/> -->
    <link rev="made" href="mailto:iml001@bridgewater.edu">
    <meta name="author" content="Ian Lewis">
    <meta name="keywords" content="jsXe, Java, XML, Editor, Sourceforge, GNU">
    <meta name="description" content="Information and links about jsXe">
  </head>
<!-- }}} -->
<!-- {{{ body -->
<body>
  <table width="100%" cellpadding="6" cellspacing="0" border="0">
    <tr>
      <td width="10%">&nbsp;</td>
      <td width="90%">
        <div align="center"><h2>jsXe: <xsl:value-of select="title"/></h2></div>
        <hr/>
      </td>
    </tr>
  <tr>
    <td width="10%" valign="top">
      
      
      <div>
        <a href="index.html">Home</a>
      </div>
      <div>
        <a href="features.html">Features</a>
      </div>
<div>
  <a href="todo.html">To Do</a>
</div>
<div>
  <a href="screenshots.html">Screenshots</a>
</div>
<div>
  <a href="downloads.html">Downloads</a>
</div>
<div>
  <a href="links.html">Links</a>
</div>
<div>
  <a href="about.html">About</a>
</div>
<hr>
<div>
  <a href="http://sourceforge.net/news/?group_id=58584">News</a>
</div>
<div>
  <a href="http://www.sourceforge.net/projects/jsxe/">SourceForge project</a>
</div>
<hr>
<div align="center">
  <a href="http://www.jedit.org/">
    <img src="images/made-with-jedit-3.png" border="0" width="102" height="47" alt="Developed with jEdit">
  </a>
</div>
<div align="center">
  <a href="http://sourceforge.net">
    <img src="http://sourceforge.net/sflogo.php?group_id=58584" width="88" height="31" border="0" alt="SourceForge Logo">
  </a>
</div>
<div align="center">
  <a href="http://validator.w3.org/check/referer">
    <img src="images/vh401.png" width="88" height="31" border="0" alt="Valid HTML 4.01!">
  </a>
</div>
</td>
<!-- }}} -->

<td width="90%" valign="top">

<!-- {{{ content for home page -->
<h3>Introduction</h3>
<p>
jsXe is the Java Simple XML Editor. Although you may pronounce it any way you
wish i encourage you to pronounce it "jay sexy". It is written in java using the
Swing toolkit, JAXP, and DOM and is released under the terms of the <a
href="http://www.gnu.org/copyleft/gpl.html">GNU General Public License</a>. It
aims to provide end users and developers with an intuitive way of creating XML
documents. It is geared towards simpler XML documents but will (eventually) be
able to handle any XML document.
</p>
<h3>Development</h3>
<p>
jsXe is currently in a planning and pre-alpha stage. It is not ready for
extensive use and is considered a work in progress. If you require a free XML
editor and jsXe does not currently serve your needs, i recommend that you try <a
href="http://pollo.sourceforge.net">Pollo</a> or <a
href="http://jaxe.sourceforge.net">Jaxe</a> (i didn't steal the name, i
promise).
</p>
<!-- }}} -->

<hr>
</td>
</tr>
</table>
<!-- }}} -->
<!-- {{{ footer -->
</body>
</html>
<!-- }}} -->

</xsl:template>
