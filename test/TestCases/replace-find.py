useFixture(default)

# Tests the find dialog's find and replace feature
def test():
	window('jsXe - Untitled-1')
	doubleclick('DefaultViewTree', '/Document Root/default_element')
	rightclick('DefaultViewTree', '/Document Root/default_element/default_node')
	click('Remove Node')
	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Element')
	click('Add Element Node')
	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Element')
	click('Add Element Node')
	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Element')
	click('Add Element Node')

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
	assertText('SourceTextArea', """<?xml version="1.0" encoding="UTF-8"?>
<default_element>
	<new_element/>
	<new_element/>
	<new_element/>
</default_element>
""")


	window('Search and Replace')
	click('Replace&Find')

	window('jsXe - Untitled-1')
	assertText('SourceTextArea', """<?xml version="1.0" encoding="UTF-8"?>
<default_element>
	<element1/>
	<new_element/>
	<new_element/>
</default_element>
""")

	window('Search and Replace')
	click('Replace&Find')

	window('jsXe - Untitled-1')
	assertText('SourceTextArea', """<?xml version="1.0" encoding="UTF-8"?>
<default_element>
	<element1/>
	<element1/>
	<new_element/>
</default_element>
""")

	window('Search and Replace')
	click('Replace&Find')

	window('jsXe - Untitled-1')
	assertText('SourceTextArea', """<?xml version="1.0" encoding="UTF-8"?>
<default_element>
	<element1/>
	<element1/>
	<element1/>
</default_element>
""")

	window('Search and Replace')
	click('Replace&Find') # should do nothing

	window('jsXe - Untitled-1')
	assertText('SourceTextArea', """<?xml version="1.0" encoding="UTF-8"?>
<default_element>
	<element1/>
	<element1/>
	<element1/>
</default_element>
""")

	click('Cancel')
	close()
	close()
