useFixture(default)

def test():
	#Tests opening multiple files at once and closing.

	# rename default_element to differentiate files
	window('jsXe - Untitled-1')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'default_node' ] ])
	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'untitled-1')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'untitled-1', 'default_node' ] ])

	click('File')
	click('New')
	close()

	window('jsXe - Untitled-2')
	assertContent('JTabbedPane', [ [ 'Untitled-1', 'Untitled-2' ] ])

	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'default_node' ] ])
	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'untitled-2')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'untitled-2', 'default_node' ] ])

	click('File')
	click('New')
	close()

	window('jsXe - Untitled-3')
	assertContent('JTabbedPane', [ [ 'Untitled-1', 'Untitled-2', 'Untitled-3' ] ])

	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'default_node' ] ])
	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'untitled-3')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'untitled-3', 'default_node' ] ])

	click('File')
	click('New')
	close()

	window('jsXe - Untitled-4')
	assertContent('JTabbedPane', [ [ 'Untitled-1', 'Untitled-2', 'Untitled-3', 'Untitled-4' ] ])

	
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'default_node' ] ])
	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'untitled-4')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'untitled-4', 'default_node' ] ])

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

	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'default_node' ] ])
	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'untitled-5')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'untitled-5', 'default_node' ] ])

	click('File')
	click('New')
	close()

	window('jsXe - Untitled-6')
	assertContent('JTabbedPane', [ [ 'Untitled-1', 'Untitled-3', 'Untitled-4', 'Untitled-5', 'Untitled-6' ] ])

	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'default_node' ] ])
	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'untitled-6')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'untitled-6', 'default_node' ] ])

	click('File')
	click('New')
	close()

	window('jsXe - Untitled-7')
	assertContent('JTabbedPane', [ [ 'Untitled-1', 'Untitled-3', 'Untitled-4', 'Untitled-5', 'Untitled-6', 'Untitled-7' ] ])

	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'default_node' ] ])
	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'untitled-7')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'untitled-7', 'default_node' ] ])

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

	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'default_node' ] ])
	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'untitled-8')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'untitled-8', 'default_node' ] ])
	
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
	assertContent('DefaultViewTree', [ [ 'Document Root', 'untitled-1', 'default_node' ] ])

	select('JTabbedPane', 'Untitled-3')
	close()

	window('jsXe - Untitled-3')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'untitled-3', 'default_node' ] ])

	select('JTabbedPane', 'Untitled-4')
	close()

	window('jsXe - Untitled-4')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'untitled-4', 'default_node' ] ])

	select('JTabbedPane', 'Untitled-6')
	close()

	window('jsXe - Untitled-6')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'untitled-6', 'default_node' ] ])

	close()
