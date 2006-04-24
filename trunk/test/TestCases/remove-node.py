#:tabSize=4:indentSize=4:noTabs=false:
#:folding=explicit:collapseFolds=1:

import treeview, jsxe

useFixture(default)

# Tests removing nodes from the tree.
def test():
	jsxe.setStartingFiles([['Untitled-1']])
	
	treeview.expand('/Document Root/default_element')
	treeview.addElementNode('/Document Root/default_element')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'default_node', 'new_element' ] ])

	treeview.addCDATANode('/Document Root/default_element')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'default_node', 'new_element', 'New CDATA Section' ] ])

	treeview.addPINode('/Document Root/default_element')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'default_node', 'new_element', 'New CDATA Section', 'Instruction' ] ])

	
	treeview.addCommentNode('/Document Root/default_element')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'default_node', 'new_element', 'New CDATA Section', 'Instruction', 'New Comment' ] ])

	treeview.addTextNode('/Document Root/default_element/new_element')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'default_node', 'new_element', 'New Text Node', 'New CDATA Section', 'Instruction', 'New Comment' ] ])

	treeview.removeNode('/Document Root/default_element/New CDATA Section')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'default_node', 'new_element', 'New Text Node', 'Instruction', 'New Comment' ] ])

	treeview.removeNode('/Document Root/default_element/new_element')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'default_node', 'Instruction', 'New Comment' ] ])

	treeview.removeNode('/Document Root/default_element/Instruction')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'default_node', 'New Comment' ] ])
	close()
