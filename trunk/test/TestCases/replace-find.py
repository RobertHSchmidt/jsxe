#:tabSize=4:indentSize=4:noTabs=false:
#:folding=explicit:collapseFolds=1:

import treeview, sourceview

useFixture(default)

# Tests the find dialog's find and replace feature
def test():
	window('jsXe - Untitled-1')
    
	treeview.expand('/Document Root/default_element')
	treeview.removeNode('/Document Root/default_element/default_node')
	
	treeview.addElementNode('/Document Root/default_element')
	treeview.addElementNode('/Document Root/default_element')
	treeview.addElementNode('/Document Root/default_element')
    
	click('Tools')
	click('Options...')
	window('Global Options')
	click('JTree', '//jsxe/XML Document Options')
	select('Format XML output', 'true')
	click('OK')
	close()

	click('View')
	click('Source View')
	click('Edit')
	click('Find...')

	window('Search and Replace')
	select('FindComboBox', 'new_element')
	select('ReplaceComboBox', 'element1')
	# for some reason we need this to for combo box to update selected content
	click('ReplaceComboBox') 

	# sleep(30);

	click('Replace&Find') #this should do nothing

	window('jsXe - Untitled-1')
	sourceview.assertText("""<?xml version="1.0" encoding="UTF-8"?>
<default_element>
	<new_element/>
	<new_element/>
	<new_element/>
</default_element>""")


	window('Search and Replace')
	click('Replace&Find')

	window('jsXe - Untitled-1')
	sourceview.assertText("""<?xml version="1.0" encoding="UTF-8"?>
<default_element>
	<element1/>
	<new_element/>
	<new_element/>
</default_element>""")

	window('Search and Replace')
	click('Replace&Find')

	window('jsXe - Untitled-1')
	sourceview.assertText("""<?xml version="1.0" encoding="UTF-8"?>
<default_element>
	<element1/>
	<element1/>
	<new_element/>
</default_element>""")

	window('Search and Replace')
	click('Replace&Find')

	window('jsXe - Untitled-1')
	sourceview.assertText("""<?xml version="1.0" encoding="UTF-8"?>
<default_element>
	<element1/>
	<element1/>
	<element1/>
</default_element>""")

	window('Search and Replace')
	click('Replace&Find') # should do nothing

	window('jsXe - Untitled-1')
	sourceview.assertText("""<?xml version="1.0" encoding="UTF-8"?>
<default_element>
	<element1/>
	<element1/>
	<element1/>
</default_element>""")

	click('Cancel')
	close()
	close()
