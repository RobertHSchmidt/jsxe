#:tabSize=4:indentSize=4:noTabs=false:
#:folding=explicit:collapseFolds=1:

import treeview, jsxe

useFixture(default)

#Tests opening multiple files at once and closing.
def test():
	# rename default_element to differentiate files
	jsxe.setStartingFiles([['Untitled-1']])
	
	treeview.assertTree([ [ 'Document Root', 'default_element', 'default_node' ] ])
	treeview.renameNode('/Document Root/default_element', 'untitled-1')
	treeview.assertTree([ [ 'Document Root', 'untitled-1', 'default_node' ] ])

	jsxe.newFile()

	treeview.assertTree([ [ 'Document Root', 'default_element', 'default_node' ] ])
	treeview.renameNode('/Document Root/default_element', 'untitled-2')
	treeview.assertTree([ [ 'Document Root', 'untitled-2', 'default_node' ] ])

	jsxe.newFile()
	
	treeview.assertTree([ [ 'Document Root', 'default_element', 'default_node' ] ])
	treeview.renameNode('/Document Root/default_element', 'untitled-3')
	treeview.assertTree([ [ 'Document Root', 'untitled-3', 'default_node' ] ])

	jsxe.newFile()

	treeview.assertTree([ [ 'Document Root', 'default_element', 'default_node' ] ])
	treeview.renameNode('/Document Root/default_element', 'untitled-4')
	treeview.assertTree([ [ 'Document Root', 'untitled-4', 'default_node' ] ])

	jsxe.closeDirtyFileByName('Untitled-2')

	jsxe.newFile()

	treeview.assertTree([ [ 'Document Root', 'default_element', 'default_node' ] ])
	treeview.renameNode('/Document Root/default_element', 'untitled-5')
	treeview.assertTree([ [ 'Document Root', 'untitled-5', 'default_node' ] ])

	jsxe.newFile()

	treeview.assertTree([ [ 'Document Root', 'default_element', 'default_node' ] ])
	treeview.renameNode('/Document Root/default_element', 'untitled-6')
	treeview.assertTree([ [ 'Document Root', 'untitled-6', 'default_node' ] ])

	jsxe.newFile()

	treeview.assertTree([ [ 'Document Root', 'default_element', 'default_node' ] ])
	treeview.renameNode('/Document Root/default_element', 'untitled-7')
	treeview.assertTree([ [ 'Document Root', 'untitled-7', 'default_node' ] ])

	jsxe.selectFile('Untitled-5')
	
	jsxe.closeDirtyFile()

	jsxe.newFile()

	treeview.assertTree([ [ 'Document Root', 'default_element', 'default_node' ] ])
	treeview.renameNode('/Document Root/default_element', 'untitled-8')
	treeview.assertTree([ [ 'Document Root', 'untitled-8', 'default_node' ] ])
	
	jsxe.closeDirtyFileByName('Untitled-7')

	jsxe.closeDirtyFile()

	# Tests
	
	jsxe.selectFile('Untitled-1')
	
	treeview.assertTree([ [ 'Document Root', 'untitled-1', 'default_node' ] ])

	jsxe.selectFile('Untitled-3')

	treeview.assertTree([ [ 'Document Root', 'untitled-3', 'default_node' ] ])

	jsxe.selectFile('Untitled-4')
	
	treeview.assertTree([ [ 'Document Root', 'untitled-4', 'default_node' ] ])

	jsxe.selectFile('Untitled-6')
	
	treeview.assertTree([ [ 'Document Root', 'untitled-6', 'default_node' ] ])

	close()
