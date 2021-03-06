#:tabSize=4:indentSize=4:noTabs=false:
#:folding=explicit:collapseFolds=1:

import treeview, sourceview, jsxe

useFixture(default)

# Tests editing a text node
def test():
	jsxe.setStartingFiles([['Untitled-1']])
	
	treeview.expand('/Document Root/default_element')
	treeview.choose('/Document Root/default_element/default_node')
	treeview.setValue('This is a test')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'This is a test' ] ])
	
	jsxe.setView('Source View')
	
	sourceview.assertText("""<?xml version="1.0" encoding="UTF-8"?>
<default_element>This is a test</default_element>""")
	close()
