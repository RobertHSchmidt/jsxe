#:tabSize=4:indentSize=4:noTabs=false:
#:folding=explicit:collapseFolds=1:

import treeview, sourceview,jsxe

useFixture(default)

# tests the format-pretty-print feature
def test():
	jsxe.setStartingFiles([['Untitled-1']])
	
	treeview.expand('/Document Root/default_element')
	treeview.removeNode('/Document Root/default_element/default_node')
	treeview.assertTree([ [ 'Document Root', 'default_element' ] ])
	
	treeview.choose('/Document Root/default_element')
	treeview.assertAttributes([ ])
	treeview.addAttribute('test', 'junk')
	treeview.assertAttributes([ [ 'test', 'junk' ] ])
	
	treeview.addAttribute('test2', 'test Attribute')
	treeview.assertAttributes([ [ 'test', 'junk' ], [ 'test2', 'test Attribute' ] ])
	
	treeview.addElementNode('/Document Root/default_element')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'new_element' ] ])

	treeview.renameNode('/Document Root/default_element/new_element', 'element1')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1' ] ])
	
	treeview.addTextNode('/Document Root/default_element/element1')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'New Text Node' ] ])
	
	treeview.addElementNode('/Document Root/default_element')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'new_element' ] ])
	
	treeview.renameNode('/Document Root/default_element/new_element', 'element2')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2' ] ])
	
	treeview.addTextNode('/Document Root/default_element/element2')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node' ] ])

	treeview.addElementNode('/Document Root/default_element')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'new_element' ] ])
	
	treeview.renameNode('/Document Root/default_element/new_element', 'element3')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3' ] ])
	
	treeview.addCDATANode('/Document Root/default_element/element3')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'New CDATA Section' ] ])

	treeview.choose('/Document Root/default_element/element3/New CDATA Section')
	treeview.setValue('Test CDATA')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'Test CDATA' ] ])

	treeview.addPINode('/Document Root/default_element')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'Test CDATA', 'Instruction' ] ])
	
	treeview.choose('/Document Root/default_element/Instruction')
	treeview.setValue('Test Instruction')
	
	treeview.addCommentNode('/Document Root/default_element')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'Test CDATA', 'Instruction', 'New Comment' ] ])

	treeview.choose('/Document Root/default_element/New Comment')
	treeview.setValue('TEST COMMENT')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'Test CDATA', 'Instruction', 'TEST COMMENT' ] ])
	
	treeview.addElementNode('/Document Root/default_element')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'Test CDATA', 'Instruction', 'TEST COMMENT', 'new_element' ] ])
	
	treeview.renameNode('/Document Root/default_element/new_element', 'element4')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'Test CDATA', 'Instruction', 'TEST COMMENT', 'element4' ] ])
	
	treeview.addElementNode('/Document Root/default_element/element4')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'Test CDATA', 'Instruction', 'TEST COMMENT', 'element4', 'new_element' ] ])
	
	treeview.renameNode('/Document Root/default_element/element4/new_element', 'element5')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'Test CDATA', 'Instruction', 'TEST COMMENT', 'element4', 'element5' ] ])
	
	click('Tools')
	click('Options...')
	window('Global Options')
	click('JTree', '//jsxe/XML Document Options')
	select('Format XML output', 'true')
	select('Soft tabs #{emulated with spaces#}', 'false')
	click('OK')
	close()

	jsxe.setView('Source View')
	
	sourceview.assertText("""<?xml version="1.0" encoding="UTF-8"?>
<default_element test="junk" test2="test Attribute">
	<element1>New Text Node</element1>
	<element2>New Text Node</element2>
	<element3><![CDATA[Test CDATA]]></element3>
	<?Instruction Test Instruction?>
	<!--TEST COMMENT-->
	<element4>
		<element5/>
	</element4>
</default_element>""")
	
	close()
