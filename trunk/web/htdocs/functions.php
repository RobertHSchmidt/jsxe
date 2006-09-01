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
?>