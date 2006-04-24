#:tabSize=4:indentSize=4:noTabs=false:
#:folding=explicit:collapseFolds=1:

import treeview, sourceview, jsxe

useFixture(default)

# Tests simple non-formatted serialization
def test():
	jsxe.setStartingFiles([['Untitled-1']])
	
	treeview.expand('/Document Root/default_element')
	treeview.removeNode('/Document Root/default_element/default_node')
	treeview.assertTree([ [ 'Document Root', 'default_element' ] ])

	treeview.addElementNode('/Document Root/default_element')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'new_element' ] ])

	treeview.renameNode('/Document Root/default_element/new_element', 'element1')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1' ] ])

	treeview.addElementNode('/Document Root/default_element/element1')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'new_element' ] ])

	treeview.renameNode('/Document Root/default_element/element1/new_element', 'element2')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'element2' ] ])

	treeview.addElementNode('/Document Root/default_element')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'element2', 'new_element' ] ])

	treeview.renameNode('/Document Root/default_element/new_element', 'element3')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'element2', 'element3' ] ])

	treeview.addTextNode('/Document Root/default_element/element3')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'element2', 'element3', 'New Text Node' ] ])

	treeview.addElementNode('/Document Root/default_element')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'element2', 'element3', 'New Text Node', 'new_element' ] ])

	treeview.renameNode('/Document Root/default_element/new_element', 'element4')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'element2', 'element3', 'New Text Node', 'element4'] ])

	treeview.addCDATANode('/Document Root/default_element/element4')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'element2', 'element3', 'New Text Node', 'element4', 'New CDATA Section'] ])

	treeview.addElementNode('/Document Root/default_element')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'element2', 'element3', 'New Text Node', 'element4', 'New CDATA Section', 'new_element'] ])

	treeview.renameNode('/Document Root/default_element/new_element', 'element5')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'element2', 'element3', 'New Text Node', 'element4', 'New CDATA Section', 'element5'] ])

	treeview.addPINode('/Document Root/default_element/element5')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'element2', 'element3', 'New Text Node', 'element4', 'New CDATA Section', 'element5', 'Instruction'] ])

	treeview.addElementNode('/Document Root/default_element')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'element2', 'element3', 'New Text Node', 'element4', 'New CDATA Section', 'element5', 'Instruction', 'new_element'] ])

	#doubleclick('JScrollPane$ScrollBar.')
	#doubleclick('JScrollPane$ScrollBar.')

	treeview.renameNode('/Document Root/default_element/new_element', 'element6')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'element2', 'element3', 'New Text Node', 'element4', 'New CDATA Section', 'element5', 'Instruction', 'element6'] ])

	treeview.addCommentNode('/Document Root/default_element/element6')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'element2', 'element3', 'New Text Node', 'element4', 'New CDATA Section', 'element5', 'Instruction', 'element6', 'New Comment'] ])

	#doubleclick('JScrollPane$ScrollBar.')

	jsxe.setView('Source View')
	sourceview.assertText('''<?xml version="1.0" encoding="UTF-8"?>
<default_element><element1><element2/></element1><element3>New Text Node</element3><element4><![CDATA[New CDATA Section]]></element4><element5><?Instruction New Processing Instruction?></element5><element6><!--New Comment--></element6></default_element>''')
	

	#close()
