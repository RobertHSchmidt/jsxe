#:tabSize=4:indentSize=4:noTabs=false:
#:folding=explicit:collapseFolds=1:

import treeview, sourceview

useFixture(default)

# Tests editing a file from source
def test():
	window('jsXe - Untitled-1')
	click('View')
	click('Source View')
	sourceview.setText('''<?xml version="1.0" encoding="UTF-8"?>
<root>
	<text_element>Text</text_element>
	<cdata_element>
		<![CDATA[CDATA]]>
	</cdata_element>
	<pi_element>
		<?Instruction New Processing Instruction?>
	</pi_element>
	<comment_element>
		<!--comment-->
	</comment_element>
</root>''')
	click('View')
	click('Tree View')

	treeview.assertTree([ [ 'Document Root', 'root', 'text_element', 'Text', 'cdata_element', 'CDATA', 'pi_element', 'Instruction', 'comment_element', 'comment' ] ])
	close()
