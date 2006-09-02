<?php 
define( 'ROOT', 'true' );
include("functions.php");
?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="<?php echo get_locale() ?>" lang="<?php echo get_locale() ?>">
  <head>
    <title>jsXe: <?php echo T_("Screenshots"); ?></title>
    <?php include("meta.php") ?>
  </head>
  <body>
    <div id="title"><h1>jsXe: The Java Simple XML Editor</h1></div>
    
    <div id="sidebar">
      <?php include("sidebar.php") ?>
    </div>
    
    <div id="mainBody">
      
      <?php if (isset($_GET['id'])) {
          echo '<center><img alt="screenshot" src="http://sourceforge.net/dbimage.php?id=';
          print_r($_GET['id']);
          echo '"/></center>';
        } else {
      ?>
      
        <h2><?php echo T_("Screenshots"); ?></h2>
        
        <table border="0" cellpadding="0" cellspacing="15" width="100%">
          <tr>
            
            <td>
              
              <?php
              $params = array();
              $params['id'] = '86758';
              echo create_link('screenshots.php', '<img alt="screenshot" border="0" src="http://sourceforge.net/dbimage.php?id=86757"/>', $params);
              ?>
            </td>
            <td>
              <?php echo T_("jsXe with Japanese locale."); ?>
              <br/>
              
              (jsXe 0.5 pre3, Ubuntu Linux)
            </td>
          </tr>
          <tr>
            <td>
              <?php
              $params = array();
              $params['id'] = '59765';
              echo create_link('screenshots.php', '<img alt="screenshot" border="0" src="https://sourceforge.net/dbimage.php?id=59764"/>', $params);
              ?>
            </td>
            <td>
              <?php echo T_("A screenshot showing the context dialog including cut/paste."); ?>
              <br/>
              
              (jsXe 0.4 pre3, Windows XP)
            </td>
          </tr>
          
          <tr>
            <td>
              <?php
              $params = array();
              $params['id'] = '34496';
              echo create_link('screenshots.php', '<img alt="screenshot" border="0" src="https://sourceforge.net/dbimage.php?id=34495"/>', $params);
              ?>
            </td>
            <td>
              <?php echo T_("A screenshot showing validation features. jsXe allows you to add nodes defined by DTD/Schema."); ?>
              <br/>
              
              (jsXe 0.4, Windows XP)
            </td>
          </tr>
          <tr>
            <td>
              <?php
              $params = array();
              $params['id'] = '34498';
              echo create_link('screenshots.php', '<img alt="screenshot" border="0" src="https://sourceforge.net/dbimage.php?id=34497"/>', $params);
              ?>
            </td>
            <td>
              <?php echo T_("A screenshot showing the syntax highlighted source view."); ?>
              <br/>
              
              (jsXe 0.4 pre2, Windows XP)
            </td>
          </tr>
          <tr>
            <td>
              <?php
              $params = array();
              $params['id'] = '34500';
              echo create_link('screenshots.php', '<img alt="screenshot" border="0" src="https://sourceforge.net/dbimage.php?id=34499"/>', $params);
              ?>
            </td>
            <td>
              <?php echo T_("A screenshot of jsXe&apos;s options panel."); ?>
              <br/>
              
              (jsXe 0.4 pre1, Windows XP)
            </td>
          </tr>
          <tr>
            <td>
              <?php
              $params = array();
              $params['id'] = '34502';
              echo create_link('screenshots.php', '<img alt="screenshot" border="0" src="https://sourceforge.net/dbimage.php?id=34501"/>', $params);
              ?>
            </td>
            <td>
              <?php echo T_("A screenshot of the edit node dialog. This dialog can be used to edit nodes defined in DTD/Schema."); ?>
              <br/>
              
              (jsXe 0.4 pre1, Windows XP)
            </td>
          </tr>
        </table>
      <?php } ?>
      
      <?php include("footer.php") ?>
    </div>
  </body>
</html>