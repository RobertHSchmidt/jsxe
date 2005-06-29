#:tabSize=4:indentSize=4:noTabs=false:
#:folding=explicit:collapseFolds=1:

import treeview, sourceview

useFixture(default)

# Tests splitting CDATA nodes
def test():
	window('jsXe - Untitled-1')
	
	treeview.expand('/Document Root/default_element')
	treeview.removeNode('/Document Root/default_element/default_node')
	treeview.assertTree([ [ 'Document Root', 'default_element' ] ])

	treeview.addCDATANode('/Document Root/default_element')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'New CDATA Section' ] ])

	treeview.choose('/Document Root/default_element/New CDATA Section')
	treeview.setValue('CDATA1 ]]> CDATA2')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'CDATA1 ]]> CDATA2' ] ])

	click('View')
	click('Source View')
	sourceview.assertText("""<?xml version="1.0" encoding="UTF-8"?>
<default_element><![CDATA[CDATA1 ]]]]><![CDATA[> CDATA2]]></default_element>""")
	
	click('View')
	click('Tree View')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'CDATA1 ]]', '> CDATA2' ] ])

	close()
