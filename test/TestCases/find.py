#:tabSize=4:indentSize=4:noTabs=false:
#:folding=explicit:collapseFolds=1:

import sourceview, jsxe

useFixture(default)

# Tests the sourceview's Find dialog
def test():
	jsxe.setStartingFiles([['Untitled-1']])
	jsxe.setView('Source View')
	click('Edit')
	click('Find...')

	window('Search and Replace')
	select('FindComboBox', 'default')
	click('Find')
	jsxe.jsxewindow('Untitled-1')
	sourceview.assertSelectedText('default')
	window('Search and Replace')
	click('Find')
	jsxe.jsxewindow('Untitled-1')
	sourceview.assertSelectedText('default')
	window('Search and Replace')
	click('Find')
	jsxe.jsxewindow('Untitled-1')
	sourceview.assertSelectedText('default')
	window('Search and Replace')
	click('Cancel')
	close()
