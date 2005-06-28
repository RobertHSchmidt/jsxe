#:tabSize=4:indentSize=4:noTabs=false:
#:folding=explicit:collapseFolds=1:

useFixture(default)

# tests the format-pretty-print feature
def test():
    
	window('jsXe - Untitled-1')
	doubleclick('TreeViewTree', '/Document Root/default_element')
	rightclick('TreeViewTree', '/Document Root/default_element/default_node')
	click('Remove Node')
	assertContent('TreeViewTree', [ [ 'Document Root', 'default_element' ] ])
	
	click('TreeViewTree', '/Document Root/default_element')
	assertContent('TreeViewAttributesTable', [ [ '', ''  ] ])
	select('TreeViewAttributesTable', 'test', 'Attribute,0')
	select('TreeViewAttributesTable', 'junk', 'Value,0')
	assertContent('TreeViewAttributesTable', [ [ 'test', 'junk' ], [ '', '' ] ])
	select('TreeViewAttributesTable', 'test2', 'Attribute,1')
	select('TreeViewAttributesTable', 'test Attribute', 'Value,1')
	assertContent('TreeViewAttributesTable', [ [ 'test', 'junk' ], [ 'test2', 'test Attribute' ], [ '', '' ] ])
	
	rightclick('TreeViewTree', '/Document Root/default_element')
	click('Add')
	click('Element')
	click('Add Element Node')
	assertContent('TreeViewTree', [ [ 'Document Root', 'default_element', 'new_element' ] ])

	rightclick('TreeViewTree', '/Document Root/default_element/new_element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'element1')
	keystroke('Enter')
	assertContent('TreeViewTree', [ [ 'Document Root', 'default_element', 'element1' ] ])
	
	rightclick('TreeViewTree', '/Document Root/default_element/element1')
	click('Add')
	click('Text')
	assertContent('TreeViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node' ] ])
	
	rightclick('TreeViewTree', '/Document Root/default_element')
	click('Add')
	click('Element')
	click('Add Element Node')
	assertContent('TreeViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'new_element' ] ])
	
	rightclick('TreeViewTree', '/Document Root/default_element/new_element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'element2')
	keystroke('Enter')
	assertContent('TreeViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2' ] ])
	
	rightclick('TreeViewTree', '/Document Root/default_element/element2')
	click('Add')
	click('Text')
	assertContent('TreeViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node' ] ])

	rightclick('TreeViewTree', '/Document Root/default_element')
	click('Add')
	click('Element')
	click('Add Element Node')
	assertContent('TreeViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'new_element' ] ])
	
	rightclick('TreeViewTree', '/Document Root/default_element/new_element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'element3')
	keystroke('Enter')
	assertContent('TreeViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3' ] ])
	
	rightclick('TreeViewTree', '/Document Root/default_element/element3')
	click('Add')
	click('CDATA Section')
	assertContent('TreeViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'New CDATA Section' ] ])

	click('TreeViewTree', '/Document Root/default_element/element3/New CDATA Section')
	select('TreeViewTextArea', 'Test CDATA')
	assertContent('TreeViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'Test CDATA' ] ])

	rightclick('TreeViewTree', '/Document Root/default_element')
	click('Add')
	click('Processing Instruction')
	assertContent('TreeViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'Test CDATA', 'Instruction' ] ])
	
	click('TreeViewTree', '/Document Root/default_element/Instruction')
	select('TreeViewTextArea', 'Test Instruction')
	
	rightclick('TreeViewTree', '/Document Root/default_element')
	click('Add')
	click('Comment')
	assertContent('TreeViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'Test CDATA', 'Instruction', 'New Comment' ] ])

	click('TreeViewTree', '/Document Root/default_element/New Comment')
	select('TreeViewTextArea', 'TEST COMMENT')
	assertContent('TreeViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'Test CDATA', 'Instruction', 'TEST COMMENT' ] ])
	
	rightclick('TreeViewTree', '/Document Root/default_element')
	click('Add')
	click('Element')
	click('Add Element Node')
	assertContent('TreeViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'Test CDATA', 'Instruction', 'TEST COMMENT', 'new_element' ] ])
	
	rightclick('TreeViewTree', '/Document Root/default_element/new_element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'element4')
	keystroke('Enter')
	assertContent('TreeViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'Test CDATA', 'Instruction', 'TEST COMMENT', 'element4' ] ])
	
	rightclick('TreeViewTree', '/Document Root/default_element/element4')
	click('Add')
	click('Element')
	click('Add Element Node')
	assertContent('TreeViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'Test CDATA', 'Instruction', 'TEST COMMENT', 'element4', 'new_element' ] ])
	
	rightclick('TreeViewTree', '/Document Root/default_element/element4/new_element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'element5')
	keystroke('Enter')
	assertContent('TreeViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'Test CDATA', 'Instruction', 'TEST COMMENT', 'element4', 'element5' ] ])
	
	click('Tools')
	click('Options...')
	window('Global Options')
	click('JTree', '//jsxe/XML Document Options')
	select('Format XML output', 'true')
	click('OK')
	close()

	click('View')
	click('Source View')
	
	textarea = getComponent('SourceTextArea')
	
	expected = """<?xml version="1.0" encoding="UTF-8"?>
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
</default_element>"""
	
	if not (textarea.getText() == expected):
		fail('Text in sourceview does not match '+expected)
	
	close()
