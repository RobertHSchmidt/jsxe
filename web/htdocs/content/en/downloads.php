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
  <li>The latest stable version of jsXe is <?php echo get_stable_download_link(); ?></li>
  <li>The latest development version of jsXe is <?php echo get_devel_download_link(); ?>
    <ul>
      <li>Windows Installer: <?php echo get_devel_windows_download_link(); ?></li>
      <li>Java Installer: <?php echo get_devel_jar_download_link(); ?></li>
    </ul>
  </li>
</ul>

<h3>Install</h3>

<ul>
    <li><b>0.4 beta</b>
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
             &apos;Start in&apos; directory can be anywhere.</p>
      </li>
      </ul>
    </li>
    <li><b>0.5 pre2</b>
      <ul>
        <li><b>Windows</b>
          
        <p>Just run jsXe using the jsXe.exe in the install directory. The
           exe will detect your version of Java and run jsXe using that
           version.</p>
       </li>
      
        <li><b>Unix/Linux</b>
          
          <p>To run jsXe you should just run the java interpreter on the
             jsXe.jar file. There is a shell script included in the
             source tree for this purpose.</p>
          
          <p>./bin/jsXe.sh</p>
          
          <p>The shell script should be run in with the jsXe root directory of
             as the working directory. You can also edit the JSXEDIR variable in
             the shell script to be the directory where you installed jsXe.</p>
        </li>
      </ul>
    </li>
</ul>