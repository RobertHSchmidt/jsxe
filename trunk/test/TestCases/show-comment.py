useFixture(default)

# Tests the showing/hiding comment nodes feature
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

	rightclick('DefaultViewTree', '/Document Root/default_element/new_element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'element1')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/element1')
	click('Add')
	click('Add Text Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/element1')
	click('Add')
	click('Add Comment')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Element')
	click('Add Element Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment', 'new_element' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/new_element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'element2')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment', 'element2' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/element2')
	click('Add')
	click('Add CDATA Section')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment', 'element2', 'New CDATA Section' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Add Comment')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment', 'element2', 'New CDATA Section', 'New Comment' ] ])

	click('DefaultViewTree', '/Document Root/default_element/New Comment')
	select('JEditorPane', 'Comment Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment', 'element2', 'New CDATA Section', 'Comment Node' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Element')
	click('Add Element Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment', 'element2', 'New CDATA Section', 'Comment Node', 'new_element' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/new_element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'element3')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment', 'element2', 'New CDATA Section', 'Comment Node', 'element3' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/element3')
	click('Add')
	click('Element')
	click('Add Element Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment', 'element2', 'New CDATA Section', 'Comment Node', 'element3', 'new_element' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/element3/new_element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'element4')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment', 'element2', 'New CDATA Section', 'Comment Node', 'element3', 'element4' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/element3/element4')
	click('Add')
	click('Add CDATA Section')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment', 'element2', 'New CDATA Section', 'Comment Node', 'element3', 'element4', 'New CDATA Section' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/element3/element4')
	click('Add')
	click('Add Comment')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment', 'element2', 'New CDATA Section', 'Comment Node', 'element3', 'element4', 'New CDATA Section', 'New Comment' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/element3/element4')
	click('Add')
	click('Add Processing Instruction')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment', 'element2', 'New CDATA Section', 'Comment Node', 'element3', 'element4', 'New CDATA Section', 'New Comment', 'Instruction' ] ])

	#doubleclick('JScrollPane$ScrollBar.')
	#doubleclick('JScrollPane$ScrollBar.')
	click('Tools')
	click('Options...')
	window('Global Options')
	click('JTree', '//jsxe/Tree View Options')
	select('Show comment nodes', 'false')
	click('OK')
	close()
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New CDATA Section', 'element3', 'element4', 'New CDATA Section', 'Instruction' ] ])

	click('Tools')
	click('Options...')
	window('Global Options')
	click('JTree', '//jsxe/Tree View Options')
	select('Show comment nodes', 'true')
	click('OK')
	close()
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment', 'element2', 'New CDATA Section', 'Comment Node', 'element3', 'element4', 'New CDATA Section', 'New Comment', 'Instruction' ] ])

	close()

#-----------------------------
