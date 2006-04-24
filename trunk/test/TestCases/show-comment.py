#:tabSize=4:indentSize=4:noTabs=false:
#:folding=explicit:collapseFolds=1:

import treeview, jsxe

useFixture(default)

# Tests the showing/hiding comment nodes feature
def test():
	jsxe.setStartingFiles([['Untitled-1']])
	treeview.expand('/Document Root/default_element')
	treeview.removeNode('/Document Root/default_element/default_node')
	treeview.assertTree([ [ 'Document Root', 'default_element' ] ])
	
	treeview.addElementNode('/Document Root/default_element')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'new_element' ] ])

	treeview.renameNode('/Document Root/default_element/new_element', 'element1')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1' ] ])

	treeview.addTextNode('/Document Root/default_element/element1')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'New Text Node' ] ])

	treeview.addCommentNode('/Document Root/default_element/element1')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment' ] ])

	treeview.addElementNode('/Document Root/default_element')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment', 'new_element' ] ])

	treeview.renameNode('/Document Root/default_element/new_element', 'element2')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment', 'element2' ] ])

	treeview.addCDATANode('/Document Root/default_element/element2')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment', 'element2', 'New CDATA Section' ] ])

	treeview.addCommentNode('/Document Root/default_element')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment', 'element2', 'New CDATA Section', 'New Comment' ] ])

	treeview.choose('/Document Root/default_element/New Comment')
	treeview.setValue('Comment Node')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment', 'element2', 'New CDATA Section', 'Comment Node' ] ])

	treeview.addElementNode('/Document Root/default_element')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment', 'element2', 'New CDATA Section', 'Comment Node', 'new_element' ] ])

	treeview.renameNode('/Document Root/default_element/new_element', 'element3')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment', 'element2', 'New CDATA Section', 'Comment Node', 'element3' ] ])

	treeview.addElementNode('/Document Root/default_element/element3')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment', 'element2', 'New CDATA Section', 'Comment Node', 'element3', 'new_element' ] ])

	treeview.renameNode('/Document Root/default_element/element3/new_element', 'element4')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment', 'element2', 'New CDATA Section', 'Comment Node', 'element3', 'element4' ] ])

	treeview.addCDATANode('/Document Root/default_element/element3/element4')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment', 'element2', 'New CDATA Section', 'Comment Node', 'element3', 'element4', 'New CDATA Section' ] ])

	treeview.addCommentNode('/Document Root/default_element/element3/element4')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment', 'element2', 'New CDATA Section', 'Comment Node', 'element3', 'element4', 'New CDATA Section', 'New Comment' ] ])

	treeview.addPINode('/Document Root/default_element/element3/element4')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment', 'element2', 'New CDATA Section', 'Comment Node', 'element3', 'element4', 'New CDATA Section', 'New Comment', 'Instruction' ] ])

	#doubleclick('JScrollPane$ScrollBar.')
	#doubleclick('JScrollPane$ScrollBar.')
	click('Tools')
	click('Options...')
	window('Global Options')
	click('JTree', '//jsxe/Tree View Options')
	select('Show comment nodes', 'false')
	click('OK')
	close()
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New CDATA Section', 'element3', 'element4', 'New CDATA Section', 'Instruction' ] ])

	click('Tools')
	click('Options...')
	window('Global Options')
	click('JTree', '//jsxe/Tree View Options')
	select('Show comment nodes', 'true')
	click('OK')
	close()
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment', 'element2', 'New CDATA Section', 'Comment Node', 'element3', 'element4', 'New CDATA Section', 'New Comment', 'Instruction' ] ])

	close()
