<?php if( !defined('ROOT') ) die( 'Please, do not access this page directly.' ); ?>

<ul>
  <li><?php echo create_link("index.php", "News") ?></li>
  <li><?php echo create_link("features.php", "Features") ?></li>
  <li><?php echo create_link("screenshots.php", "Screenshots") ?></li>
  <li><?php echo create_link("downloads.php", "Downloads") ?></li>
  <li><?php echo create_link("get-involved.php", "Get Involved") ?></li>
</ul>

<ul>
  <li><a href="api/"><?php T_("API Docs") ?></a></li>
  <li><a href="http://www.sourceforge.net/projects/jsxe/"><?php echo T_("SourceForge Project")?></a></li>
</ul>

<a href="http://sourceforge.net/"><img alt="SourceForge Logo" src="http://sourceforge.net/sflogo.php?group_id=58584"/></a>
<a href="http://sourceforge.jp/"><img alt="SourceForge.jp" src="http://sourceforge.jp/sflogo.php?group_id=2456"/></a>

<div id="languages"/>
  <ul>
    <li><?php echo get_image('us.png');?> <?php echo create_language_link('en-us', 'English'); ?></li>
    <li><?php echo get_image('jp.png');?> <?php echo create_language_link('ja-jp', '日本語'); ?></li>
    <li><?php echo get_image('ru.png');?> <?php echo create_language_link('ru-ru', 'Русский Язык'); ?></li>
  </ul>
</div>