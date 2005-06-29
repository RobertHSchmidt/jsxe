#:tabSize=4:indentSize=4:noTabs=false:
#:folding=explicit:collapseFolds=1:

import treeview, sourceview

useFixture(default)

# Tests editing a comment node
def test():
	window('jsXe - Untitled-1')
	treeview.expand('/Document Root/default_element')
	treeview.removeNode('/Document Root/default_element/default_node')
	treeview.assertTree([ [ 'Document Root', 'default_element' ] ])
	
	treeview.addCommentNode('/Document Root/default_element')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'New Comment' ] ])
	
	treeview.choose('/Document Root/default_element/New Comment')
	treeview.setValue('Comment test 123 ]]> [[> <[!CDATA')
	treeview.assertTree([ [ 'Document Root', 'default_element', 'Comment test 123 ]]> [[> <[!CDATA' ] ])
	
	click('View')
	click('Source View')
	sourceview.assertText("""<?xml version="1.0" encoding="UTF-8"?>
<default_element><!--Comment test 123 ]]> [[> <[!CDATA--></default_element>""")
	
	close()
