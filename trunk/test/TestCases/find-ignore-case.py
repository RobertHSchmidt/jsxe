useFixture(default)

# Tests the find dialog's find ignoring case feature
def test():
	window('jsXe - Untitled-1')
	doubleclick('DefaultViewTree', '/Document Root/default_element')
	rightclick('DefaultViewTree', '/Document Root/default_element/default_node')
	click('Remove Node')
	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Element')
	click('Add Element Node')
	rightclick('DefaultViewTree', '/Document Root/default_element/new_element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'Default')
	keystroke('Enter')
	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Element')
	click('Add Element Node')
	rightclick('DefaultViewTree', '/Document Root/default_element/new_element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'default')
	keystroke('Enter')
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
	assertText('SourceTextArea',"""<?xml version="1.0" encoding="UTF-8"?>
<test1_element><Default/><default/></default_element>""")

	window('Search and Replace')
	select('FindComboBox', 'Default')
	select('ReplaceComboBox', 'test2')
	click('ReplaceComboBox')
	select('Ignore Case', 'true')
	click('Replace&Find')

	window('jsXe - Untitled-1')
	assertText('SourceTextArea',"""<?xml version="1.0" encoding="UTF-8"?>
<test1_element><Default/><test2/></default_element>""")

	window('Search and Replace')
	select('ReplaceComboBox', 'test3')
	click('ReplaceComboBox')

	click('Replace&Find')

	window('jsXe - Untitled-1')
	assertText('SourceTextArea',"""<?xml version="1.0" encoding="UTF-8"?>
<test1_element><Default/><test2/></test3_element>""")

	click('Cancel')
	close()
	close()
