useFixture(default)

def test():
	window('jsXe - Untitled-1')
	doubleclick('DefaultViewTree', '/Document Root/default_element')
	rightclick('DefaultViewTree', '/Document Root/default_element/default_node')
	click('Remove Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element' ] ])
	
	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Element Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'New_Element' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/New_Element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'element1')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/element1')
	click('Add')
	click('Text Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/element1')
	click('Add')
	click('Comment')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment Node' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Element Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment Node', 'New_Element' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/New_Element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'element2')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment Node', 'element2' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/element2')
	click('Add')
	click('CDATA Section')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment Node', 'element2', 'New CDATA Node' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Comment')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment Node', 'element2', 'New CDATA Node', 'New Comment Node' ] ])

	click('DefaultViewTree', '/Document Root/default_element/New Comment Node')
	select('JEditorPane', 'Comment Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment Node', 'element2', 'New CDATA Node', 'Comment Node' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Element Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment Node', 'element2', 'New CDATA Node', 'Comment Node', 'New_Element' ] ])

	click('DefaultViewTree', '/Document Root/default_element/New_Element')
	rightclick('DefaultViewTree', '/Document Root/default_element/New_Element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'element3')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment Node', 'element2', 'New CDATA Node', 'Comment Node', 'element3' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/element3')
	click('Add')
	click('Element Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment Node', 'element2', 'New CDATA Node', 'Comment Node', 'element3', 'New_Element' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/element3/New_Element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'element4')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment Node', 'element2', 'New CDATA Node', 'Comment Node', 'element3', 'element4' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/element3/element4')
	click('Add')
	click('CDATA Section')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment Node', 'element2', 'New CDATA Node', 'Comment Node', 'element3', 'element4', 'New CDATA Node' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/element3/element4')
	click('Add')
	click('Comment')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment Node', 'element2', 'New CDATA Node', 'Comment Node', 'element3', 'element4', 'New CDATA Node', 'New Comment Node' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/element3/element4')
	click('Add')
	click('Processing Instruction')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New Comment Node', 'element2', 'New CDATA Node', 'Comment Node', 'element3', 'element4', 'New CDATA Node', 'New Comment Node', 'Instruction' ] ])

	#doubleclick('JScrollPane$ScrollBar.')
	#doubleclick('JScrollPane$ScrollBar.')
	click('Tools')
	click('Options...')

	window('Global Options')
	click('JTree', '//jsxe/Tree View Options')
	select('Show comment nodes', 'false')
	click('OK')
	close()
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New CDATA Node', 'element3', 'element4', 'New CDATA Node', 'Instruction' ] ])

	close()

#-----------------------------
