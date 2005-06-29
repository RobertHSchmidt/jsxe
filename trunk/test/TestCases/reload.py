useFixture(default)

# Tests reloading an untitled file
def test():
	window('jsXe - Untitled-1')
	doubleclick('TreeViewTree', '/Document Root/default_element')
	rightclick('TreeViewTree', '/Document Root/default_element/default_node')
	click('Remove Node')
	assertContent('TreeViewTree', [ [ 'Document Root', 'default_element' ] ])

	rightclick('TreeViewTree', '/Document Root/default_element')
	click('Add')
	click('Element')
	click('Add Element Node')
	assertContent('TreeViewTree', [ [ 'Document Root', 'default_element', 'new_element' ] ])

	rightclick('TreeViewTree', '/Document Root/default_element')
	click('Add')
	click('Text')
	assertContent('TreeViewTree', [ [ 'Document Root', 'default_element', 'new_element', 'New Text Node' ] ])

	rightclick('TreeViewTree', '/Document Root/default_element')
	click('Add')
	click('CDATA Section')
	assertContent('TreeViewTree', [ [ 'Document Root', 'default_element', 'new_element', 'New Text Node', 'New CDATA Section' ] ])

	rightclick('TreeViewTree', '/Document Root/default_element')
	click('Add')
	click('Processing Instruction')
	assertContent('TreeViewTree', [ [ 'Document Root', 'default_element', 'new_element', 'New Text Node', 'New CDATA Section', 'Instruction' ] ])

	rightclick('TreeViewTree', '/Document Root/default_element')
	click('Add')
	click('Comment')
	assertContent('TreeViewTree', [ [ 'Document Root', 'default_element', 'new_element', 'New Text Node', 'New CDATA Section', 'Instruction', 'New Comment' ] ])

	select('TreeViewAttributesTable', 'TEST', 'Attribute,0')
	select('TreeViewAttributesTable', 'TEST2', 'Value,0')
	assertContent('TreeViewAttributesTable', [ [ 'TEST', 'TEST2' ], [ '', '' ] ])
	click('File')
	click('Reload')

	window('Document Modified')
	click('Yes')
	close()

	click('TreeViewTree', '/Document Root/default_element')
	assertContent('TreeViewTree', [ [ 'Document Root', 'default_element', 'default_node' ] ])
	assertContent('TreeViewAttributesTable', [ [ '', '' ] ])
	close()
