useFixture(default)

def test():
	window('jsXe - Untitled-1')
	doubleclick('DefaultViewTree', '/Document Root/default_element')
	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Element Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'default_node', 'New_Element' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('CDATA Section')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'default_node', 'New_Element', 'New CDATA Node' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Processing Instruction')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'default_node', 'New_Element', 'New CDATA Node', 'Instruction' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Comment')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'default_node', 'New_Element', 'New CDATA Node', 'Instruction', 'New Comment Node' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/New_Element')
	click('Add')
	click('Text Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'default_node', 'New_Element', 'New Text Node', 'New CDATA Node', 'Instruction', 'New Comment Node' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/New CDATA Node')
	click('Remove Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'default_node', 'New_Element', 'New Text Node', 'Instruction', 'New Comment Node' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/New_Element')
	click('Remove Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'default_node', 'Instruction', 'New Comment Node' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/Instruction')
	click('Remove Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'default_node', 'New Comment Node' ] ])
	close()
