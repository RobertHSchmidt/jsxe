#:tabSize=4:indentSize=4:noTabs=false:
#:folding=explicit:collapseFolds=1:

import treeview, sourceview

useFixture(default)

# Tests the find dialog's find ignoring case feature
def test():
	window('jsXe - Untitled-1')
	
	treeview.expand('/Document Root/default_element')
	treeview.removeNode('/Document Root/default_element/default_node')
	
	treeview.addElementNode('/Document Root/default_element')
	treeview.renameNode('/Document Root/default_element/new_element', 'Default')
	
	treeview.addElementNode('/Document Root/default_element')
	
	treeview.renameNode('/Document Root/default_element/new_element', 'default')
	
	click('View')
	click('Source View')
	click('Edit')
	click('Find...')

	window('Search and Replace')
	select('FindComboBox', 'Default')
	select('ReplaceComboBox', 'test1')
	
	# for some reason we need this for combo box to update selected content
	click('ReplaceComboBox')
	select('Ignore Case', 'true')
	click('Find')
	select('FindComboBox', 'default')
	click('FindComboBox')
	select('Ignore Case', 'false')
	click('Replace&Find')

	window('jsXe - Untitled-1')
	sourceview.assertText("""<?xml version="1.0" encoding="UTF-8"?>
<test1_element><Default/><default/></default_element>""")

	window('Search and Replace')
	select('FindComboBox', 'Default')
	select('ReplaceComboBox', 'test2')
	click('ReplaceComboBox')
	select('Ignore Case', 'true')
	click('Replace&Find')

	window('jsXe - Untitled-1')
	sourceview.assertText("""<?xml version="1.0" encoding="UTF-8"?>
<test1_element><Default/><test2/></default_element>""")

	window('Search and Replace')
	select('ReplaceComboBox', 'test3')
	click('ReplaceComboBox')

	click('Replace&Find')

	window('jsXe - Untitled-1')
	
	sourceview.assertText("""<?xml version="1.0" encoding="UTF-8"?>
<test1_element><Default/><test2/></test3_element>""")
	
	click('Cancel')
	close()
	close()
