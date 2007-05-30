<h3>システム条件</h3>

<ul>
<li>Java 2 バージョン 1.4.2は必要です。</li>
<li>Xerces-J 2.8.0は必要です。便利なので、jsXeのバイナリ・リリースと
    ソース・リリースはXerces-J 2.8.0が含めています。または、
    <a href="http://archive.apache.org/dist/xml/xerces-j/">こちら</a>に
    ダウンロードできもす。</li>
</ul>

<h3>ダウンロード</h3>

<ul>
  <li>jsXeの最新ヴァージョンは <?php echo get_stable_download_link(); ?></li>
  <li>jsXeの最新開発ヴァージョンは <?php echo get_devel_download_link(); ?></li>
</ul>

<h3>インストール</h3>

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