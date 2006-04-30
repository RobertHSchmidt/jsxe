<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <title>jsXe: Features</title>
    <meta content="General" name="rating"/>
    <meta content="Index, follow" name="robots"/>
    <meta content="text/html; charset=UTF-8" http-equiv="Content-type"/>
    <link href="mailto:IanLewis@member.fsf.org" rev="made"/>
    <meta content="Ian Lewis" name="author"/>
    <meta content="jsXe, Java, XML, Editor, Sourceforge, GNU" name="keywords"/>
    <meta content="Information and links about jsXe" name="description"/>
    <link rel="stylesheet" type="text/css" media="all" href="css/main.css" />
  </head>
  <body>
    <div id="title"><h1>jsXe: The Java Simple XML Editor</h1></div>
    
    <div id="sidebar">
      <?php include("sidebar.php") ?>
    </div>
    <div id="mainBody">
      <h2>Features</h2>
      
      <p>These are features currently implemented in the stable version of jsXe</p>
      
      <h3>General</h3>
        
      <ul>
        <li>Written in Java, so it runs on Mac OS X, OS/2, Unix, VMS and Windows.</li>
        <li>XML documents are edited using views. jsXe allows for
            multiple views.</li>
        <li>Plugin interface that allows the addition of views
            without re-compiling</li>
        <li>Relatively good serialization of XML documents.
            Supports the option of formatting serialized XML
            documents or not formatting. Supports preserving
            whitespace in text.</li>
        <li>Allows opening and editing of multiple XML documents in
            one open window.</li>
      </ul>
        
      <h3>Tree View</h3>
        
      <ul>
        <li>Adding, deleting, and editing of Nodes in an XML document using context menus.</li>
        <li>Adding, deleting, and editing of attributes of an element node.</li>
        <li>Drag and Drop of XML nodes within the Tree view and to text editors.</li>
      </ul>
      
      <h3>Source View</h3>
        
      <ul>
        <li>Text editing of the full XML source of any XML document</li>
        <li>Cut, copy, and paste and other limited features.</li>
      </ul>
      
      
      <h2>Planned Features</h2>
      
      <p>These are features that have been added to the development version of jsXe or are planned for the future</p>
          
      <ul>
        <li>A schema designer view that uses <a href="http://www.jgraph.com/">JGraph</a></li>
        <li>Incremental parsing. Parsing the document and validation
            will be done automatically without having to
            continuously hit a validate/parse button. (version 0.4 beta)</li>
        <li>Support for transforming XML documents using XSLT
            stylesheets.</li>
        <li>Support for validation of an XML document using DTDs and
            other forms of Schema. This information will be used to
            allow users to add and edit nodes based on definitions
            within the DTD/Schema. (version 0.4 beta)</li>
        <li>Support for Unlimited Undo</li>
        <li>Syntax highlighted source view (version 0.4 beta)</li>
        <li>Additional features for editing source of XML
            documents such as closing tag completion and marking of
            matching opening/closing tags (version 0.4 beta)</li>
        <li>Internationalization. jsXe will have support to easily
            create translations of menus and messages into your native
            language. (version 0.4 beta)</li>
      </ul>
      <div id="images">
        <a href="http://sourceforge.net/"><img alt="SourceForge Logo" src="http://sourceforge.net/sflogo.php?group_id=58584"/></a>
        <a href="http://validator.w3.org/check/referer"><img alt="Valid XHTML 1.0 Transitional" src="http://www.w3.org/Icons/valid-xhtml10"/></a>
        <a href="http://jigsaw.w3.org/css-validator/validator?uri=http%3A%2F%2Fjsxe.sourceforge.net%2Fcss%2Fmain.css&amp;usermedium=all"><img alt="Valid CSS" src="http://jigsaw.w3.org/css-validator/images/vcss"/></a>
        <a href="http://www.jedit.org"><img alt="Made with jEdit" src="images/made-with-jedit-6.png"/></a>
      </div>
    </div>
  </body>
</html>