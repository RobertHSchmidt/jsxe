useFixture(default)

def test():
	# tests the format-pretty-print feature
	window('jsXe - Untitled-1')
	doubleclick('DefaultViewTree', '/Document Root/default_element')
	rightclick('DefaultViewTree', '/Document Root/default_element/default_node')
	click('Remove Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element' ] ])
	
	click('DefaultViewTree', '/Document Root/default_element')
	assertContent('JTable', [ [ '', ''  ] ])
	select('JTable', 'test', 'Attribute,0')
	select('JTable', 'junk', 'Value,0')
	assertContent('JTable', [ [ 'test', 'junk' ], [ '', '' ] ])
	select('JTable', 'test2', 'Attribute,1')
	select('JTable', 'test Attribute', 'Value,1')
	assertContent('JTable', [ [ 'test', 'junk' ], [ 'test2', 'test Attribute' ], [ '', '' ] ])
	
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
	
	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Element Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'New_Element' ] ])
	
	rightclick('DefaultViewTree', '/Document Root/default_element/New_Element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'element2')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2' ] ])
	
	rightclick('DefaultViewTree', '/Document Root/default_element/element2')
	click('Add')
	click('Text Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Element Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'New_Element' ] ])
	
	rightclick('DefaultViewTree', '/Document Root/default_element/New_Element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'element3')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3' ] ])
	
	rightclick('DefaultViewTree', '/Document Root/default_element/element3')
	click('Add')
	click('CDATA Section')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'New CDATA Node' ] ])

	click('DefaultViewTree', '/Document Root/default_element/element3/New CDATA Node')
	select('JEditorPane', 'Test CDATA')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'Test CDATA' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Processing Instruction')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'Test CDATA', 'Instruction' ] ])
	
	click('DefaultViewTree', '/Document Root/default_element/Instruction')
	select('JEditorPane', 'Test Instruction')
	
	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Comment')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'Test CDATA', 'Instruction', 'New Comment Node' ] ])

	click('DefaultViewTree', '/Document Root/default_element/New Comment Node')
	select('JEditorPane', 'TEST COMMENT')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'Test CDATA', 'Instruction', 'TEST COMMENT' ] ])
	
	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Element Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'Test CDATA', 'Instruction', 'TEST COMMENT', 'New_Element' ] ])
	
	rightclick('DefaultViewTree', '/Document Root/default_element/New_Element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'element4')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'Test CDATA', 'Instruction', 'TEST COMMENT', 'element4' ] ])
	
	rightclick('DefaultViewTree', '/Document Root/default_element/element4')
	click('Add')
	click('Element Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'Test CDATA', 'Instruction', 'TEST COMMENT', 'element4', 'New_Element' ] ])
	
	rightclick('DefaultViewTree', '/Document Root/default_element/element4/New_Element')
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
	assertText('SourceView$SourceViewTextPane',"""<?xml version="1.0" encoding="UTF-8"?>
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
