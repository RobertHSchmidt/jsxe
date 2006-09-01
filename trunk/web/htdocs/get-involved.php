<?php 
define( 'ROOT', 'true' );
include("functions.php");
?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="<?php echo get_locale() ?>" lang="<?php echo get_locale() ?>">
  <head>
    <title>jsXe: Getting Involved</title>
    <?php include("meta.php") ?>
  </head>
  <body>
    <div id="title"><h1>jsXe: The Java Simple XML Editor</h1></div>
    
    <div id="sidebar">
      <?php include("sidebar.php") ?>
    </div>

    <div id="mainBody">
      
      <?php get_content('get-involved') ?>
      
      <?php include("footer.php") ?>
    </div>
  </body>
</html>