useFixture(default)

# Tests removing nodes from the tree.
def test():
	window('jsXe - Untitled-1')
	doubleclick('DefaultViewTree', '/Document Root/default_element')
	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Element')
	click('Add Element Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'default_node', 'new_element' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Add CDATA Section')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'default_node', 'new_element', 'New CDATA Section' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Add Processing Instruction')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'default_node', 'new_element', 'New CDATA Section', 'Instruction' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Add Comment')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'default_node', 'new_element', 'New CDATA Section', 'Instruction', 'New Comment' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/new_element')
	click('Add')
	click('Add Text Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'default_node', 'new_element', 'New Text Node', 'New CDATA Section', 'Instruction', 'New Comment' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/New CDATA Section')
	click('Remove Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'default_node', 'new_element', 'New Text Node', 'Instruction', 'New Comment' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/new_element')
	click('Remove Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'default_node', 'Instruction', 'New Comment' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/Instruction')
	click('Remove Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'default_node', 'New Comment' ] ])
	close()
