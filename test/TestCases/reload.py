#:tabSize=4:indentSize=4:noTabs=false:
#:folding=explicit:collapseFolds=1:

import treeview

useFixture(default)

# Tests reloading an untitled file
def test():
	window('jsXe - Untitled-1')
	treeview.expand('/Document Root/default_element')
	treeview.removeNode('/Document Root/default_element/default_node')
	treeview.assertTree([ [ 'Document Root', 'default_element' ] ])

	treeview.addElementNode('/Document Root/default_element')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'new_element' ] ])

	treeview.addTextNode('/Document Root/default_element')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'new_element', 'New Text Node' ] ])

	treeview.addCDATANode('/Document Root/default_element')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'new_element', 'New Text Node', 'New CDATA Section' ] ])

	treeview.addPINode('/Document Root/default_element')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'new_element', 'New Text Node', 'New CDATA Section', 'Instruction' ] ])

	treeview.addCommentNode('/Document Root/default_element')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'new_element', 'New Text Node', 'New CDATA Section', 'Instruction', 'New Comment' ] ])

	treeview.addAttribute('TEST', 'TEST2')
	treeview.assertAttributes([ [ 'TEST', 'TEST2' ] ])
	
	click('File')
	click('Reload')

	window('Document Modified')
	click('Yes')
	close()

	treeview.choose('/Document Root/default_element')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'default_node' ] ])
	treeview.assertAttributes([ ])
	close()
