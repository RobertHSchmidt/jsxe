useFixture(default)

# Tests editing a processing instruction
def test():
	window('jsXe - Untitled-1')
	doubleclick('DefaultViewTree', '/Document Root/default_element')
	rightclick('DefaultViewTree', '/Document Root/default_element/default_node')
	click('Remove Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element' ] ])

	
	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Add Processing Instruction')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'Instruction' ] ])

	
	click('DefaultViewTree', '/Document Root/default_element/Instruction')
	rightclick('DefaultViewTree', '/Document Root/default_element/Instruction')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'SQLSELECT')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'SQLSELECT' ] ])

	
	select('JEditorPane', 'SELECT * FROM BLAH')
	click('View')
	click('Source View')
	
	assertText('SourceTextArea', """<?xml version="1.0" encoding="UTF-8"?>
<default_element><?SQLSELECT SELECT * FROM BLAH?></default_element>""")
	close()
