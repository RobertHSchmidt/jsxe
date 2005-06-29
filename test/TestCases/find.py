#:tabSize=4:indentSize=4:noTabs=false:
#:folding=explicit:collapseFolds=1:

import sourceview

useFixture(default)

# Tests the sourceview's Find dialog
def test():
	window('jsXe - Untitled-1')
	click('View')
	click('Source View')
	click('Edit')
	click('Find...')

	window('Search and Replace')
	select('FindComboBox', 'default')
	click('Find')
	window('jsXe - Untitled-1')
	sourceview.assertSelectedText('default')
	window('Search and Replace')
	click('Find')
	window('jsXe - Untitled-1')
	sourceview.assertSelectedText('default')
	window('Search and Replace')
	click('Find')
	window('jsXe - Untitled-1')
	sourceview.assertSelectedText('default')
	window('Search and Replace')
	click('Cancel')
	close()
