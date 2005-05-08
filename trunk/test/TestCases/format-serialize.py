useFixture(default)

# tests the format-pretty-print feature
def test():
	window('jsXe - Untitled-1')
	doubleclick('DefaultViewTree', '/Document Root/default_element')
	rightclick('DefaultViewTree', '/Document Root/default_element/default_node')
	click('Remove Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element' ] ])
	
	click('DefaultViewTree', '/Document Root/default_element')
	assertContent('AttributesTable', [ [ '', ''  ] ])
	select('AttributesTable', 'test', 'Attribute,0')
	select('AttributesTable', 'junk', 'Value,0')
	assertContent('AttributesTable', [ [ 'test', 'junk' ], [ '', '' ] ])
	select('AttributesTable', 'test2', 'Attribute,1')
	select('AttributesTable', 'test Attribute', 'Value,1')
	assertContent('AttributesTable', [ [ 'test', 'junk' ], [ 'test2', 'test Attribute' ], [ '', '' ] ])
	
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
	
	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Element')
	click('Add Element Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'new_element' ] ])
	
	rightclick('DefaultViewTree', '/Document Root/default_element/new_element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'element2')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2' ] ])
	
	rightclick('DefaultViewTree', '/Document Root/default_element/element2')
	click('Add')
	click('Add Text Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Element')
	click('Add Element Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'new_element' ] ])
	
	rightclick('DefaultViewTree', '/Document Root/default_element/new_element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'element3')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3' ] ])
	
	rightclick('DefaultViewTree', '/Document Root/default_element/element3')
	click('Add')
	click('Add CDATA Section')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'New CDATA Section' ] ])

	click('DefaultViewTree', '/Document Root/default_element/element3/New CDATA Section')
	select('JEditorPane', 'Test CDATA')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'Test CDATA' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Add Processing Instruction')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'Test CDATA', 'Instruction' ] ])
	
	click('DefaultViewTree', '/Document Root/default_element/Instruction')
	select('JEditorPane', 'Test Instruction')
	
	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Add Comment')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'Test CDATA', 'Instruction', 'New Comment' ] ])

	click('DefaultViewTree', '/Document Root/default_element/New Comment')
	select('JEditorPane', 'TEST COMMENT')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'Test CDATA', 'Instruction', 'TEST COMMENT' ] ])
	
	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Element')
	click('Add Element Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'Test CDATA', 'Instruction', 'TEST COMMENT', 'new_element' ] ])
	
	rightclick('DefaultViewTree', '/Document Root/default_element/new_element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'element4')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'Test CDATA', 'Instruction', 'TEST COMMENT', 'element4' ] ])
	
	rightclick('DefaultViewTree', '/Document Root/default_element/element4')
	click('Add')
	click('Element')
	click('Add Element Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'Test CDATA', 'Instruction', 'TEST COMMENT', 'element4', 'new_element' ] ])
	
	rightclick('DefaultViewTree', '/Document Root/default_element/element4/new_element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'element5')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'Test CDATA', 'Instruction', 'TEST COMMENT', 'element4', 'element5' ] ])
	
	click('Tools')
	click('Options...')
	window('Global Options')
	click('JTree', '//jsxe/XML Document Options')
	select('Format XML output', 'true')
	click('OK')
	close()

	click('View')
	click('Source View')
	assertText('SourceTextArea',"""<?xml version="1.0" encoding="UTF-8"?>
<default_element test="junk" test2="test Attribute">
	<element1>New Text Node</element1>
	<element2>New Text Node</element2>
	<element3>
		<![CDATA[Test CDATA]]>
	</element3>
	<?Instruction Test Instruction?>
	<!--TEST COMMENT-->
	<element4>
		<element5/>
	</element4>
</default_element>""")
	
	close()
