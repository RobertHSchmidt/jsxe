<?php
/*
:tabSize=4:indentSize=4:noTabs=true:
:folding=explicit:collapseFolds=1:
*/

if( !defined('ROOT') ) die( 'Please, do not access this page directly.' );

global $default_locale, $locales;
$default_locale = "en-us";

$dir = dirname(__FILE__).'/content/';
$locales = directory_list($dir);

//{{{ get_locale()

function get_locale() {
    if (!defined('LOCALE')) {
        if (isset($_GET['lang'])) {
            //IML: Support the lang attribute
            $locale = match_locale($_GET['lang']);
            if (!empty($locale)) {
                define('LANG', $locale);
                return $locale;
            }
        }
        $locale = locale_from_httpaccept();
        define( 'LOCALE', $locale );
        return LOCALE;
    }
    return LOCALE;
}//}}}

//{{{ match_locale()

function match_locale($locale) {
    
    global $locales;
    
   // echo 'match_locale('.$locale.')';
   // echo '<br/>';
        
    
    // look for exact match. (lang and country)
    foreach ($locales as $key => $value) {
       // echo 'searching '.$key;
       // echo '<br/>';
        if ($key == $locale) {
           // echo 'found '.$key;
           // echo '<br/>';
            return $locale;
        }
    }
    
    // look for just lang
    foreach ($locales as $key => $value) {
        $pos = strpos( $locale, '-');
        if ($pos !== false) {
            $locale = substr($locale, 0, $pos);
        }
       // echo 'searching '.$key;
       // echo '<br/>';
        if ($key == $locale) {
           // echo 'found '.$key;
           // echo '<br/>';
            return $locale;
        }
    }
   // echo '<br/>';
    //no matches at all.
    return '';
}//}}}

//{{{ get_image()

function get_image($image, $alt='', $title='') {
    $location = 'http://jsxe.sourceforge.net/images/'.$image;
   // $location = 'http://www.ianlewis.org/jsxetest/images/'.$image;
    return '<img src="'.$location.'" alt="'.T_($alt).'" title="'.T_($title).'"/>';
}//}}}

//{{{ create_link()

function create_link( $href, $contents, $params = array() ) {
    global $default_locale;
    
    $link = '<a href="'.$href;
    
    if (isset($_GET['lang']) and empty($params['lang'])) {
        $params['lang'] = $_GET['lang'];
    }
   // if (isset($_GET['id'])) {
   //     $params['id'] = $_GET['id'];
   // }
    
    if (count($params) > 0) {
        $link = $link.'?';
        foreach ($params as $key => $value) {
            if (!empty($key) and !empty($value)) {
                $link = $link.$key.'='.$value.'&';
            }
        }
        //remove the last '&'
        $link = substr($link, 0, -1);
    }
    
    $link = $link.'">'.T_($contents).'</a>';
    return $link;
}//}}}

//{{{ create_language_link()

function create_language_link($lang, $text) {
    $current_file = $_SERVER['PHP_SELF'];
    $params = array();
    $params['lang'] = $lang;
    return create_link($current_file, $text, $params);
}//}}}

//{{{ get_content()

function get_content( $name ) {
   // $file = 'content/'.get_locale().'/'.$name.'.html';
   // if (!file_exists($file)) {
   //     $file = 'content/en/'.$name.'.html';
   // }
   // $fh = fopen($file, 'r');
   // $data = fread($fh, filesize($file));
   // fclose($fh);
   // echo $data;
    $file = 'content/'.get_locale().'/'.$name.'.html';
    if (!file_exists($file)) {
        $file = 'content/en/'.$name.'.html';
    }
    include($file);
}//}}}

//{{{ T_()

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
}//}}}

//{{{ locale_from_httpaccept()
/**
 * Detect language from HTTP_ACCEPT_LANGUAGE
 *
 * First matched full locale code in HTTP_ACCEPT_LANGUAGE will win
 * Otherwise, first locale in table matching a lang code will win
 *
 * {@internal locale_from_httpaccept(-)}}
 *
 * @return locale made out of HTTP_ACCEPT_LANGUAGE or $default_locale, if no match
 */
function locale_from_httpaccept() {
    
    global $locales, $default_locale;
    if ( isset($_SERVER['HTTP_ACCEPT_LANGUAGE']) ) {
        
        $accept = strtolower( $_SERVER['HTTP_ACCEPT_LANGUAGE'] );
       // pre_dump($accept, 'http_accept_language');
       // echo $accept;
       // $accept = 'en;q=0.3,ru,ja;q=0.8,ar-sa;q=0.5';
        
        $accept_locales = array();
        
        $tok = strtok($accept, ",");
        
        while ($tok !== false) {
          // echo "Word=$tok<br/>";
           
           $priority = 1;
           $locale = $tok;
           
           $pos = strpos( $tok, ';');
           if ($pos !== false) {
               $locale = substr($locale, 0, $pos);
               
               //check priority
               $priority_string = substr($tok, $pos+1);
              // echo $priority_string;
              // echo '<br/>';
               if (substr($priority_string, 0,2) == 'q=') {
                   $priority = substr($priority_string, 2);
               }
           }
           
          // echo 'locale='.$locale;
          // echo '<br/>';
          // echo 'priority='.$priority;
          // echo '<br/>';
           $accept_locales[$locale] = $priority;
           
           $tok = strtok(",");
        }
       // echo '<br/>';
        $accept_locales = array_sort($accept_locales, 'desc');
        foreach ( $accept_locales as $locale => $priority ) {
           // echo $priority.' => '.$locale.'<br/>';
            $selected_locale = match_locale($locale);
            if (!empty($selected_locale)) {
                return $selected_locale;
            }
        }
    }
    return $default_locale;
}//}}}

//{{{ array_sort()

function array_sort($array, $type='asc'){
   $result=array();
   foreach($array as $var => $val){
       $set=false;
       foreach($result as $var2 => $val2){
           if($set==false){
               if($val>$val2 && $type=='desc' || $val<$val2 && $type=='asc'){
                   $temp=array();
                   foreach($result as $var3 => $val3){
                       if($var3==$var2) $set=true;
                       if($set){
                           $temp[$var3]=$val3;
                           unset($result[$var3]);
                       }
                   }
                   $result[$var]=$val;   
                   foreach($temp as $var3 => $val3){
                       $result[$var3]=$val3;
                   }
               }
           }
       }
       if(!$set){
           $result[$var]=$val;
       }
   }
   return $result;
}//}}}

//{{{ directory_list()

function directory_list($dir) {
   $d = dir($dir);
   while (false !== ($entry = $d->read())) {
       if($entry != '.' && $entry != '..' && is_dir($dir.$entry))
           $arDir[$entry] = $dir.$entry.'/';
   }
   $d->close();
   return $arDir;
}//}}}

//{{{ redirect()

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
}//}}}

//{{{ get_devel_download_link()

function get_devel_download_link() {
    return '<a href="https://sourceforge.net/project/showfiles.php?group_id=58584&amp;package_id=54488">'.get_devel_version().'</a>';
}//}}}

//{{{ get_stable_download_link()

function get_stable_download_link() {
    return '<a href="https://sourceforge.net/project/showfiles.php?group_id=58584&amp;package_id=120827">'.get_stable_version().'</a>';
}//}}}

//{{{ get_devel_version()

function get_devel_version() {
    return "0.4 beta";
}//}}}

//{{{ get_stable_version()

function get_stable_version() {
    return "0.4 beta";
}//}}}

?>