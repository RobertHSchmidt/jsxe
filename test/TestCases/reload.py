useFixture(default)

# Tests reloading an untitled file
def test():
	window('jsXe - Untitled-1')
	doubleclick('DefaultViewTree', '/Document Root/default_element')
	rightclick('DefaultViewTree', '/Document Root/default_element/default_node')
	click('Remove Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Element')
	click('Add Element Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'new_element' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Add Text Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'new_element', 'New Text Node' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Add CDATA Section')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'new_element', 'New Text Node', 'New CDATA Section' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Add Processing Instruction')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'new_element', 'New Text Node', 'New CDATA Section', 'Instruction' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Add Comment')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'new_element', 'New Text Node', 'New CDATA Section', 'Instruction', 'New Comment' ] ])

	select('AttributesTable', 'TEST', 'Attribute,0')
	select('AttributesTable', 'TEST2', 'Value,0')
	assertContent('AttributesTable', [ [ 'TEST', 'TEST2' ], [ '', '' ] ])
	click('File')
	click('Reload')

	window('Document Modified')
	click('Yes')
	close()

	click('DefaultViewTree', '/Document Root/default_element')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'default_node' ] ])
	assertContent('AttributesTable', [ [ '', '' ] ])
	close()
