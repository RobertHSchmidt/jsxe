useFixture(default)

def test():
	# Tests simple non-formatted serialization
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
	click('Element Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New_Element' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/element1/New_Element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'element2')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'element2' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Element Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'element2', 'New_Element' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/New_Element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'element3')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'element2', 'element3' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/element3')
	click('Add')
	click('Text Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'element2', 'element3', 'New Text Node' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Element Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'element2', 'element3', 'New Text Node', 'New_Element' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/New_Element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'element4')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'element2', 'element3', 'New Text Node', 'element4'] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/element4')
	click('Add')
	click('CDATA Section')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'element2', 'element3', 'New Text Node', 'element4', 'New CDATA Node'] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Element Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'element2', 'element3', 'New Text Node', 'element4', 'New CDATA Node', 'New_Element'] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/New_Element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'element5')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'element2', 'element3', 'New Text Node', 'element4', 'New CDATA Node', 'element5'] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/element5')
	click('Add')
	click('Processing Instruction')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'element2', 'element3', 'New Text Node', 'element4', 'New CDATA Node', 'element5', 'Instruction'] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Element Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'element2', 'element3', 'New Text Node', 'element4', 'New CDATA Node', 'element5', 'Instruction', 'New_Element'] ])

	#doubleclick('JScrollPane$ScrollBar.')
	#doubleclick('JScrollPane$ScrollBar.')

	rightclick('DefaultViewTree', '/Document Root/default_element/New_Element')
	click('Add')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'element6')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'element2', 'element3', 'New Text Node', 'element4', 'New CDATA Node', 'element5', 'Instruction', 'element6'] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/element6')
	click('Add')
	click('Comment')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'element2', 'element3', 'New Text Node', 'element4', 'New CDATA Node', 'element5', 'Instruction', 'element6', 'New Comment Node'] ])

	#doubleclick('JScrollPane$ScrollBar.')

	click('View')
	click('Source View')
	assertText('SourceView$SourceViewTextPane', '''
<?xml version="1.0" encoding="UTF-8"?>
<default_element><element1><element2/></element1><element3>New Text Node</element3><element4><![CDATA[New CDATA Node]]></element4><element5><?Instruction New Processing Instruction?></element5><element6><!--New Comment Node--></element6></default_element>''')
	

	#close()
