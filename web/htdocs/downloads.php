<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <title>jsXe: Downloads</title>
    <?php include("meta.php") ?>
  </head>
  <body>
    <div id="title"><h1>jsXe: The Java Simple XML Editor</h1></div>
    
    <div id="sidebar">
      <?php include("sidebar.php") ?>
    </div>
    
    <div id="mainBody">
      <h2>Downloads</h2>
      
      <h3>Requirements</h3>
      
      <ul>
        <li>jsXe requires Java 2 version 1.4.2</li>
        <li>jsXe currently requires a specific version of Xerces-J to
            run. Xerces-J 2.8.0 is required since some features are
            implemented using the DOM level 3 interfaces that were in 2.6.0
            but were changed in later versions. Xerces-J 2.8.0 included in
            the normal binary and source releases of jsXe for convenience.
            You can also get Xerces-J 2.8.0
            <a href="http://archive.apache.org/dist/xml/xerces-j/">here</a>.</li>
      </ul>
      
      <h3>Download</h3>
      
      <ul>
        <li>The latest stable version of jsXe is <a href="https://sourceforge.net/project/showfiles.php?group_id=58584&amp;package_id=120827">0.4 beta</a></li>
        <li>The latest development version of jsXe is <a href="https://sourceforge.net/project/showfiles.php?group_id=58584&amp;package_id=54488">0.5 pre1</a></li>
      </ul>
      
      <h3>Install</h3>
      
      <ul>
        <li><p>To install and run jsXe simply unzip jsXe into the directory
            of your choosing and run the included batch program or shell
            script. You will need to make sure that the java runtime is in
            your path. In the root of the jsXe install directory type the
            following at a command prompt.</p>
            <p>./bin/jsXe.sh</p>
            <p>or</p>
            <p>./bin/jsXe.bat</p>
            <p>The batch program should be run in with the jsXe root
               directory of as the working directory. So you can create a
               shortcut to run jsXe by specifying the batch file as the program
               to run and jsXe&apos;s root directory as the working directory.</p>
               
            <p>Ex.<br/>
            Target: &quot;C:\Program Files\jsXe\bin\jsXe.bat&quot;<br/>
            Start in: &quot;C:\Program Files\jsXe\&quot;</p>
            
            <p>You can also edit the JSXEDIR variable in the batch program
               to be the directory where you installed jsXe and then the 
               &apos;Start in&apos; directory can be anywhere.</p></li>
      </ul>
      
      <?php include("footer.php") ?>
    </div>
  </body>
</html>