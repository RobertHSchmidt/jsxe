useFixture(default)

def test():
	# Tests reloading an untitled file
	window('jsXe - Untitled-1')
	doubleclick('DefaultViewTree', '/Document Root/default_element')
	rightclick('DefaultViewTree', '/Document Root/default_element/default_node')
	click('Remove Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Element Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'New_Element' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Text Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'New_Element', 'New Text Node' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('CDATA Section')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'New_Element', 'New Text Node', 'New CDATA Node' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Processing Instruction')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'New_Element', 'New Text Node', 'New CDATA Node', 'Instruction' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Comment')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'New_Element', 'New Text Node', 'New CDATA Node', 'Instruction', 'New Comment Node' ] ])

	select('JTable', 'TEST', 'Attribute,0')
	select('JTable', 'TEST2', 'Value,0')
	assertContent('JTable', [ [ 'TEST', 'TEST2' ], [ '', '' ] ])
	click('File')
	click('Reload')

	window('Document Modified')
	click('Yes')
	close()

	click('DefaultViewTree', '/Document Root/default_element')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'default_node' ] ])
	assertContent('JTable', [ [ '', '' ] ])
	close()
