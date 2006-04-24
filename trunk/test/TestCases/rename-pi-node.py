#:tabSize=4:indentSize=4:noTabs=false:
#:folding=explicit:collapseFolds=1:

import treeview, sourceview, jsxe

useFixture(default)

# Tests editing a processing instruction
def test():
	jsxe.setStartingFiles([['Untitled-1']])
	treeview.expand('/Document Root/default_element')
	treeview.removeNode('/Document Root/default_element/default_node')
	treeview.assertTree([ [ 'Document Root', 'default_element' ] ])

	
	treeview.addPINode('/Document Root/default_element')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'Instruction' ] ])

	
	treeview.renameNode('/Document Root/default_element/Instruction', 'SQLSELECT')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'SQLSELECT' ] ])

	treeview.choose('/Document Root/default_element/SQLSELECT')
	treeview.setValue('SELECT * FROM BLAH')
	
	jsxe.setView('Source View')
	
	sourceview.assertText("""<?xml version="1.0" encoding="UTF-8"?>
<default_element><?SQLSELECT SELECT * FROM BLAH?></default_element>""")
	close()
