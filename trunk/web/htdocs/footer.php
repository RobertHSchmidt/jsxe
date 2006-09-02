<?php if( !defined('ROOT') ) die( 'Please, do not access this page directly.' ); ?>

<div id="images">
  <a href="http://validator.w3.org/check/referer"><?php echo get_image('xhtml.png', 'Valid XHTML 1.0 Transitional', 'This site is valid XHTML.' ); ?></a>
  <a href="http://jigsaw.w3.org/css-validator/validator?uri=http%3A%2F%2Fjsxe.sourceforge.net%2Fcss%2Fmain.css&amp;usermedium=all"><?php echo get_image('css.png', 'Valid CSS', 'This site is valid CSS.'); ?></a>
  <?php echo get_image('anybrowser.png', 'Any Browser!', 'This site is usable in any browser'); ?>
</div>
