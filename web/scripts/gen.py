#!/usr/bin/env python
# gen.py
#:tabSize=4:indentSize=4:noTabs=true:
#:folding=explicit:collapseFolds=1:
#
# This script is a tool used to generate the webpage for jsXe.
# Wow is this crappy. Fix soon.
#
# This file written by ian Lewis (iml001@bridgewater.edu)
# Copyright (C) 2002 ian Lewis
#
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
# Optionally, you may find a copy of the GNU General Public License
# from http://www.fsf.org/copyleft/gpl.txt

import string
import sys

def getWords(line):
    counter = 0
    globalcounter = 0
    words = []
    length = len(line)
    line = string.strip(line)
    while (globalcounter < length and line != ""):
        while (counter < len(line) and line[counter] != ";"):
            counter += 1
        words += [string.strip(line[:counter])]
        counter += 1
        line = line[counter:]
        line = string.strip(line)
        globalcounter += counter
        counter = 0
    return words

def notCommented(line):
    line = string.strip(line)
    return (line[0] != "#")

def main():

    # {{{ initialize the list variables
    pages  = []
    names  = {}
    titles = {}
    f = open("pages", "r")
    allLines = f.readlines()
    f.close()
    print "Generating Page List..."
    onLine = 1
    for eachLine in allLines:
        if string.strip(eachLine) != "":
            if notCommented(eachLine):
                words = getWords(eachLine)
                if (len(words) == 3):
                    pages += [words[0]]
                    names[words[0]] = words[1]
                    titles[words[0]] = words[2]
                else:
                    print "ERROR: Invalid syntax in pages file."
                    print "Line " + str(onLine)
                    sys.exit(0)
            onLine += 1
    #}}}

    # {{{ generate the navigation bar
    print "Generating Navigation Bar..."
    navbar = ""
    navheader = open("navheader.src", "r")
    navbar += navheader.read()
    navheader.close()
    # {{{ generate each entry
    for page in pages:
        navbar += "<div>\n" + "  <a href=""" + "\"" + page + ".html\">" + names[page] + "</a>\n" + "</div>\n"
    # }}}
    navfooter = open("navfooter.src", "r")
    navbar += navfooter.read()
    navfooter.close()
    # }}}

    # {{{ Create the pages
    for page in pages:
        print "Creating " + names[page] + " Page..."
        title = titles[page]
        try:
            source = open(page + ".src", "r")
        except IOError:
            print "ERROR: Could not find " + page + ".src file."
            sys.exit(0)
        content = source.read()
        source.close()
        
        # {{{ special case for screenshots page
        # this seems to specific to this webpage but this _whole thing_
        # is specific to jsXe's webpage
        if (page == "screenshots"):
            screens  = ("ss1", "ss2", "ss3", "ss4")
            descriptions = ("jsXe displaying an XML document on penguins.", """
                             jsXe displaying a test document.""","""jsXe
                             displaying the same document, showing comment
                             and whitespace only nodes.""",
                            """jsXe's options panel.""")
            counter = 0
            # {{{ generate the thumbs
            for screen in screens:
                content += """
    <tr>
      <td>
        <a href=\"images/""" + screen + """.jpg\">
          <img src=\"images/""" + screen + """small.jpg\" alt="Screenshot""" + str(counter + 1) + """\" border="0">
        </a>
      </td>
      <td>""" + descriptions[counter] + """</td>
    </tr>"""
                counter += 1
            # }}}
            # {{{ add closing tags
            content += """
  </table>
</div>
<!-- }}} -->"""
        # }}}

        header = """<!-- {{{ header -->
<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">
<!-- :indentSize=2:tabSize=2:noTabs=true: -->

<html>
  <head>
    <title>jsXe: """ + title + """</title>
    <meta http-equiv=\"Content-type\" content=\"text/html; charset=iso-8859-1\">
    <!-- <base href=\"http://jsxe.sourceforge.net/\"/> -->
    <link rev=\"made\" href=\"mailto:iml001@bridgewater.edu\">
    <meta name=\"author\" content=\"ian Lewis\">
    <meta name=\"keywords\" content=\"jsXe, Java, XML, Editor, Sourceforge, GNU\">
    <meta name=\"description\" content=\"Information and links about jsXe\">
  </head>
<body>
<!-- }}} -->\n"""
        body = """<!-- {{{ body -->
<table width=\"100%\" cellpadding=\"6\" cellspacing=\"0\" border=\"0\">
  <tr>
    <td width=\"10%\">&nbsp;</td>
    <td width=\"90%\">
      <div align=\"center\"><h2>jsXe: """ + title + """</h2></div>
      <hr>
    </td>
  </tr>
<tr>

""" + navbar + """
<td width=\"90%\" valign=\"top\">

""" + content + """
<hr>
</td>
</tr>
</table>
<!-- }}} -->\n"""
        footer = """<!-- {{{ footer -->
</body>
</html>
<!-- }}} -->"""

        # {{{ Write the current file
        f = open("../htdocs/" + page + ".html", "w")
        f.write(header)
        f.write(body)
        f.write(footer)
        f.close()
        # }}}

    # }}}

if __name__=='__main__':
    main()
