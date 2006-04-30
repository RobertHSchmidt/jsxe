<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <title>jsXe: Screenshots</title>
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
      
      <?php if (isset($_GET['id'])) {
          echo '<img alt="screenshot" src="http://sourceforge.net/dbimage.php?id=';
          print_r($_GET['id']);
          echo '"/>';
        } else {
      ?>
      
        <h2>Screenshots</h2>
        
        <table border="0" cellpadding="0" cellspacing="15" width="100%">
          <tr>
            <td>
              <a href="screenshots.php?id=34495">
                <img alt="screenshot1" border="0" src="https://sourceforge.net/dbimage.php?id=34495"/>
              </a>
            </td>
            <td>
              A screenshot showing validation features. jsXe allows you to add nodes defined by DTD/Schema
              <br/>
  
              (jsXe 0.4 pre1 running on Windows XP)
            </td>
          </tr>
          <tr>
            <td>
              <a href="screenshots.php?id=34498">
                <img alt="screenshot2" border="0" src="https://sourceforge.net/dbimage.php?id=34497"/>
              </a>
            </td>
            <td>
              A screenshot showing the syntax highlighted source view.
              <br/>
  
              (jsXe 0.4 pre2 running on Windows XP)
            </td>
          </tr>
          <tr>
            <td>
              <a href="screenshots.php?id=34500">
                <img alt="screenshot3" border="0" src="https://sourceforge.net/dbimage.php?id=34499"/>
              </a>
            </td>
            <td>
              A screenshot of jsXe&apos;s options panel.
              <br/>
  
              (jsXe 0.4 pre1 running on Windows XP)
            </td>
          </tr>
          <tr>
            <td>
              <a href="screenshots.php?id=34502">
                <img alt="screenshot4" border="0" src="https://sourceforge.net/dbimage.php?id=34501"/>
              </a>
            </td>
            <td>
              A screenshot of the edit node dialog. This dialog can be used to edit nodes defined in DTD/Schema
              <br/>
  
              (jsXe 0.4 pre1 running on Windows XP)
            </td>
          </tr>
          <tr>
            <td>
              <a href="screenshots.php?id=34504">
                <img alt="screenshot5" border="0" src="https://sourceforge.net/dbimage.php?id=34503"/>
              </a>
            </td>
            <td>
              An early look at the schema view running as a plugin.
              <br/>
  
              (jsXe 0.3pre16 running on Linux)
            </td>
          </tr>
        </table>
        <?php } ?>
      <div id="images">
        <a href="http://sourceforge.net/"><img alt="SourceForge Logo" src="http://sourceforge.net/sflogo.php?group_id=58584"/></a>
        <a href="http://validator.w3.org/check/referer"><img alt="Valid XHTML 1.0 Transitional" src="http://www.w3.org/Icons/valid-xhtml10"/></a>
        <a href="http://jigsaw.w3.org/css-validator/validator?uri=http%3A%2F%2Fjsxe.sourceforge.net%2Fcss%2Fmain.css&amp;usermedium=all"><img alt="Valid CSS" src="http://jigsaw.w3.org/css-validator/images/vcss"/></a>
        <a href="http://www.jedit.org"><img alt="Made with jEdit" src="images/made-with-jedit-6.png"/></a>
      </div>
    </div>
  </body>
</html>