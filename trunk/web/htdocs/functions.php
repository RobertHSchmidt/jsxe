<?php
if( !defined('ROOT') ) die( 'Please, do not access this page directly.' );

function get_locale() {
    $req_locale="en";
    if (isset($_GET['lang'])) {
        //IML: Support the lang attribute
        $req_locale = $_GET['lang'];
    }
    return $req_locale;
}

function create_link( $href, $contents ) {
    $link = '<a href="'.$href;
    if (isset($_GET['lang'])) {
        $locale = $_GET['lang'];
        $link = $link.'?lang='.$locale;
    }
    $link = $link.'">'.T_($contents).'</a>';
    return $link;
}

function get_content( $name ) {
    $file = 'content/'.get_locale().'/'.$name.'.html';
    if (!file_exists($file)) {
        $file = 'content/en/'.$name.'.html';
    }
    $fh = fopen($file, 'r');
    $data = fread($fh, filesize($file));
    fclose($fh);
    echo $data;
}

function get_news() {
    
    $file = 'content/'.get_locale().'/news-header.html';
    if (!file_exists($file)) {
        $file = 'content/en/news-header.html';
    }
    $fh = fopen($file, 'r');
    $header = fread($fh, filesize($file));
    fclose($fh);
    
    $file = 'content/'.get_locale().'/news.html';
    if (!file_exists($file)) {
        $file = 'content/en/news.html';
    }
    $fh = fopen($file, 'r');
    $data = fread($fh, filesize($file));
    fclose($fh);
    
    echo $header;
    echo $data;
}

function T_( $string, $lang = '' ) {
    
    global $trans;
    
    if (empty($lang)) {
        $lang = get_locale();
    }
    
   // echo $lang;
    
    if (empty($lang))
        return $string;  // don't translate if we have no locale
    
    if ( !isset($trans[ $lang ] ) ) {
        // Translations for current locale have not yet been loaded:
       // echo 'LOADING', dirname(__FILE__).'/content/'.$lang.'/_global.php';
        @include_once dirname(__FILE__).'/content/'.$lang.'/_global.php';
        if ( !isset($trans[ $lang ] ) ) {
            // Still not loaded... file doesn't exist, memorize that no translation are available
            // echo 'file not found!';
            $trans[ $lang ] = array();
        }
    }
    
    $search = str_replace( array("\n", "\r", "\t"), array('\n', '', '\t'), $string );
    
    if (isset($trans[ $lang ][ $search ] ) ) { // If the string has been translated:
        return $trans[ $lang ][ $search ];
    }

   // echo "Not found: ".$string;

    // Return the English string:
    return $string;
}

// func: redirect($to,$code=307)
// spec: http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
function redirect($to,$code=301)
{
  $location = null;
  $sn = $_SERVER['SCRIPT_NAME'];
  $cp = dirname($sn);
  if (substr($to,0,4)=='http') $location = $to; // Absolute URL
  else
  {
    $schema = $_SERVER['SERVER_PORT']=='443'?'https':'http';
    $host = strlen($_SERVER['HTTP_HOST'])?$_SERVER['HTTP_HOST']:$_SERVER['SERVER_NAME'];
    if (substr($to,0,1)=='/') $location = "$schema://$host$to";
    elseif (substr($to,0,1)=='.') // Relative Path
    {
      $location = "$schema://$host";
      $pu = parse_url($to);
      $cd = dirname($_SERVER['SCRIPT_FILENAME']).'/';
      $np = realpath($cd.$pu['path']);
      $np = str_replace($_SERVER['DOCUMENT_ROOT'],'',$np);
      $location.= $np;
      if ((isset($pu['query'])) && (strlen($pu['query'])>0)) $location.= '?'.$pu['query'];
    }
  }

  $hs = headers_sent();
  if ($hs==false)
  {
    if ($code==301) header("301 Moved Permanently HTTP/1.1"); // Convert to GET
    elseif ($code==302) header("302 Found HTTP/1.1"); // Conform re-POST
    elseif ($code==303) header("303 See Other HTTP/1.1"); // dont cache, always use GET
    elseif ($code==304) header("304 Not Modified HTTP/1.1"); // use cache
    elseif ($code==305) header("305 Use Proxy HTTP/1.1");
    elseif ($code==306) header("306 Not Used HTTP/1.1");
    elseif ($code==307) header("307 Temorary Redirect HTTP/1.1");
    else trigger_error("Unhandled redirect() HTTP Code: $code",E_USER_ERROR);
    header("Location: $location");
    header('Cache-Control: no-store, no-cache, must-revalidate, post-check=0, pre-check=0');
  }
  elseif (($hs==true) || ($code==302) || ($code==303))
  {
    // todo: draw some javascript to redirect
    $cover_div_style = 'background-color: #ccc; height: 100%; left: 0px; position: absolute; top: 0px; width: 100%;'; 
    echo "<div style='$cover_div_style'>\n";
    $link_div_style = 'background-color: #fff; border: 2px solid #f00; left: 0px; margin: 5px; padding: 3px; ';
    $link_div_style.= 'position: absolute; text-align: center; top: 0px; width: 95%; z-index: 99;';
    echo "<div style='$link_div_style'>\n";
    echo "<p>Please See: <a href='$to'>".htmlspecialchars($location)."</a></p>\n";
    echo "</div>\n</div>\n";
  }
  exit(0);
}
?>