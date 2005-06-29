#:tabSize=4:indentSize=4:noTabs=false:
#:folding=explicit:collapseFolds=1:

import treeview, sourceview

useFixture(default)

# Tests editing a CDATA Section node
def test():
	window('jsXe - Untitled-1')
	treeview.expand('/Document Root/default_element')
	treeview.removeNode('/Document Root/default_element/default_node')
	treeview.assertTree([ ['Document Root', 'default_element' ] ])

	treeview.addCDATANode('/Document Root/default_element')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'New CDATA Section' ] ])

	treeview.choose('/Document Root/default_element/New CDATA Section')
	treeview.setValue('CDATA Test 123 ]')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'CDATA Test 123 ]' ] ])

	click('View')
	click('Source View')
	sourceview.assertText("""<?xml version="1.0" encoding="UTF-8"?>
<default_element><![CDATA[CDATA Test 123 ]]]></default_element>""")
	close()
    
