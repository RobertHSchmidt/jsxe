#:tabSize=4:indentSize=4:noTabs=false:
#:folding=explicit:collapseFolds=1:

import treeview

useFixture(default)

#Tests opening multiple files at once and closing.
def test():
	# rename default_element to differentiate files
	window('jsXe - Untitled-1')
	
	treeview.assertTree([ [ 'Document Root', 'default_element', 'default_node' ] ])
	treeview.renameNode('/Document Root/default_element', 'untitled-1')
	treeview.assertTree([ [ 'Document Root', 'untitled-1', 'default_node' ] ])

	click('File')
	click('New')
	close()

	window('jsXe - Untitled-2')
	assertContent('JTabbedPane', [ [ 'Untitled-1', 'Untitled-2' ] ])

	treeview.assertTree([ [ 'Document Root', 'default_element', 'default_node' ] ])
	treeview.renameNode('/Document Root/default_element', 'untitled-2')
	treeview.assertTree([ [ 'Document Root', 'untitled-2', 'default_node' ] ])

	click('File')
	click('New')
	close()

	window('jsXe - Untitled-3')
	assertContent('JTabbedPane', [ [ 'Untitled-1', 'Untitled-2', 'Untitled-3' ] ])

	treeview.assertTree([ [ 'Document Root', 'default_element', 'default_node' ] ])
	treeview.renameNode('/Document Root/default_element', 'untitled-3')
	treeview.assertTree([ [ 'Document Root', 'untitled-3', 'default_node' ] ])

	click('File')
	click('New')
	close()

	window('jsXe - Untitled-4')
	assertContent('JTabbedPane', [ [ 'Untitled-1', 'Untitled-2', 'Untitled-3', 'Untitled-4' ] ])

	
	treeview.assertTree([ [ 'Document Root', 'default_element', 'default_node' ] ])
	treeview.renameNode('/Document Root/default_element', 'untitled-4')
	treeview.assertTree([ [ 'Document Root', 'untitled-4', 'default_node' ] ])

	select('JTabbedPane', 'Untitled-2')
	close()

	window('jsXe - Untitled-2')
	click('File')
	click('Close')
	window('Unsaved Changes')
	click('No')
	close()

	window('jsXe - Untitled-3')
	assertContent('JTabbedPane', [ [ 'Untitled-1', 'Untitled-3', 'Untitled-4' ] ])

	click('File')
	click('New')
	close()

	window('jsXe - Untitled-5')
	assertContent('JTabbedPane', [ [ 'Untitled-1', 'Untitled-3', 'Untitled-4', 'Untitled-5' ] ])

	treeview.assertTree([ [ 'Document Root', 'default_element', 'default_node' ] ])
	treeview.renameNode('/Document Root/default_element', 'untitled-5')
	treeview.assertTree([ [ 'Document Root', 'untitled-5', 'default_node' ] ])

	click('File')
	click('New')
	close()

	window('jsXe - Untitled-6')
	assertContent('JTabbedPane', [ [ 'Untitled-1', 'Untitled-3', 'Untitled-4', 'Untitled-5', 'Untitled-6' ] ])

	treeview.assertTree([ [ 'Document Root', 'default_element', 'default_node' ] ])
	treeview.renameNode('/Document Root/default_element', 'untitled-6')
	treeview.assertTree([ [ 'Document Root', 'untitled-6', 'default_node' ] ])

	click('File')
	click('New')
	close()

	window('jsXe - Untitled-7')
	assertContent('JTabbedPane', [ [ 'Untitled-1', 'Untitled-3', 'Untitled-4', 'Untitled-5', 'Untitled-6', 'Untitled-7' ] ])

	treeview.assertTree([ [ 'Document Root', 'default_element', 'default_node' ] ])
	treeview.renameNode('/Document Root/default_element', 'untitled-7')
	treeview.assertTree([ [ 'Document Root', 'untitled-7', 'default_node' ] ])

	select('JTabbedPane', 'Untitled-5')
	close()

	window('jsXe - Untitled-5')
	assertContent('JTabbedPane', [ [ 'Untitled-1', 'Untitled-3', 'Untitled-4', 'Untitled-5', 'Untitled-6', 'Untitled-7' ] ])

	click('File')
	click('Close')
	window('Unsaved Changes')
	click('No')
	close()

	window('jsXe - Untitled-6')
	assertContent('JTabbedPane', [ [ 'Untitled-1', 'Untitled-3', 'Untitled-4', 'Untitled-6', 'Untitled-7' ] ])

	click('File')
	click('New')
	close()

	window('jsXe - Untitled-8')
	assertContent('JTabbedPane', [ [ 'Untitled-1', 'Untitled-3', 'Untitled-4', 'Untitled-6', 'Untitled-7', 'Untitled-8' ] ])

	treeview.assertTree([ [ 'Document Root', 'default_element', 'default_node' ] ])
	treeview.renameNode('/Document Root/default_element', 'untitled-8')
	treeview.assertTree([ [ 'Document Root', 'untitled-8', 'default_node' ] ])
	
	select('JTabbedPane', 'Untitled-7')
	close()

	window('jsXe - Untitled-7')
	click('File')
	click('Close')
	window('Unsaved Changes')
	click('No')
	close()

	window('jsXe - Untitled-8')
	assertContent('JTabbedPane', [ [ 'Untitled-1', 'Untitled-3', 'Untitled-4', 'Untitled-6', 'Untitled-8' ] ])
	click('File')
	click('Close')
	window('Unsaved Changes')
	click('No')
	close()

	# Tests

	window('jsXe - Untitled-6')
	assertContent('JTabbedPane', [ [ 'Untitled-1', 'Untitled-3', 'Untitled-4', 'Untitled-6' ] ])
	
	select('JTabbedPane', 'Untitled-1')
	close()
	
	window('jsXe - Untitled-1')
	treeview.assertTree([ [ 'Document Root', 'untitled-1', 'default_node' ] ])

	select('JTabbedPane', 'Untitled-3')
	close()

	window('jsXe - Untitled-3')
	treeview.assertTree([ [ 'Document Root', 'untitled-3', 'default_node' ] ])

	select('JTabbedPane', 'Untitled-4')
	close()

	window('jsXe - Untitled-4')
	treeview.assertTree([ [ 'Document Root', 'untitled-4', 'default_node' ] ])

	select('JTabbedPane', 'Untitled-6')
	close()

	window('jsXe - Untitled-6')
	treeview.assertTree([ [ 'Document Root', 'untitled-6', 'default_node' ] ])

	close()
