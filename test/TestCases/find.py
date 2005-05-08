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
	click('Find')
	click('Find')
	click('Cancel')
	close()
