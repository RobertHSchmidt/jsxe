<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <title>jsXe: The Java Simple XML Editor</title>
    <?php include("meta.php") ?>
  </head>
  <body>
    <div id="title"><h1>jsXe: The Java Simple XML Editor</h1></div>
    
    <div id="sidebar">
      <?php include("sidebar.php") ?>
    </div>
    <div id="mainBody">
      
      <h2>Overview</h2>
      
      <p>jsXe is the <b>J</b>ava  <b>S</b>imple <b>X</b>ML <b>E</b>ditor.</p>
      <p>jsXe is a fast, intuitive, scalable, platform-independent XML editor.
         It is written in <a href="http://java.sun.com">Java</a> using the Swing
         toolkit, and <a href="http://xml.apache.org/xerces2-j/">Xerces-J</a>
         and is released under the terms of the 
         <a href="http://www.gnu.org/copyleft/gpl.html">GNU General Public License</a>. It aims to provide end users and developers with an
         intuitive way of creating XML documents that is simple enough to deal
         with any XML document but flexible to allow the addition of support for
         XML document formats through the use of plugins.</p>
      <p>jsXe is currently in the beta stage and many intended features
         are not currently implemented. If you are interesting in helping to
         make jsXe a great editor by contributing your suggestions and needs or
         if you are a developer and would like to contribute time and code check
         out <a href="get-involved.php">how to get involved</a></p>
         
      <h2>News</h2>
      
      <?php include("news.cache") ?>
      <?php 
        //include("http://sourceforge.net/export/projnews.php?group_id=58584&limit=5&flat=FLATSETTING&show_summaries=1") 
      ?>
      
      <?php include("footer.php") ?>
    </div>
  </body>
</html>