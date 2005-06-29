#:tabSize=4:indentSize=4:noTabs=false:
#:folding=explicit:collapseFolds=1:

import treeview, sourceview

useFixture(default)

# Tests editing an element.
def test():
	window('jsXe - Untitled-1')
	
	treeview.renameNode('/Document Root/default_element', 'renamed_element')
	treeview.assertTree([ [ 'Document Root', 'renamed_element', 'default_node' ] ])
	
	click('View')
	click('Source View')
	sourceview.assertText("""<?xml version="1.0" encoding="UTF-8"?>
<renamed_element>default_node</renamed_element>""")
	close()

