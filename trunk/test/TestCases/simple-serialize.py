useFixture(default)

# Tests simple non-formatted serialization
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
	click('Element')
	click('Add Element Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'new_element' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/element1/new_element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'element2')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'element2' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Element')
	click('Add Element Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'element2', 'new_element' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/new_element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'element3')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'element2', 'element3' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/element3')
	click('Add')
	click('Add Text Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'element2', 'element3', 'New Text Node' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Element')
	click('Add Element Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'element2', 'element3', 'New Text Node', 'new_element' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/new_element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'element4')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'element2', 'element3', 'New Text Node', 'element4'] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/element4')
	click('Add')
	click('Add CDATA Section')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'element2', 'element3', 'New Text Node', 'element4', 'New CDATA Section'] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Element')
	click('Add Element Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'element2', 'element3', 'New Text Node', 'element4', 'New CDATA Section', 'new_element'] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/new_element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'element5')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'element2', 'element3', 'New Text Node', 'element4', 'New CDATA Section', 'element5'] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/element5')
	click('Add')
	click('Add Processing Instruction')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'element2', 'element3', 'New Text Node', 'element4', 'New CDATA Section', 'element5', 'Instruction'] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Element')
	click('Add Element Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'element2', 'element3', 'New Text Node', 'element4', 'New CDATA Section', 'element5', 'Instruction', 'new_element'] ])

	#doubleclick('JScrollPane$ScrollBar.')
	#doubleclick('JScrollPane$ScrollBar.')

	rightclick('DefaultViewTree', '/Document Root/default_element/new_element')
	click('Add')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'element6')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'element2', 'element3', 'New Text Node', 'element4', 'New CDATA Section', 'element5', 'Instruction', 'element6'] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/element6')
	click('Add')
	click('Add Comment')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'element2', 'element3', 'New Text Node', 'element4', 'New CDATA Section', 'element5', 'Instruction', 'element6', 'New Comment'] ])

	#doubleclick('JScrollPane$ScrollBar.')

	click('View')
	click('Source View')
	assertText('SourceTextArea', '''
<?xml version="1.0" encoding="UTF-8"?>
<default_element><element1><element2/></element1><element3>New Text Node</element3><element4><![CDATA[New CDATA Section]]></element4><element5><?Instruction New Processing Instruction?></element5><element6><!--New Comment--></element6></default_element>''')
	

	#close()
