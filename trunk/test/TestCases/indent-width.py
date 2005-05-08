useFixture(default)

# Tests the soft-tabs and indent width options
def test():
	window('jsXe - Untitled-1')
	click('View')
	click('Source View')
	click('Tools')
	click('Options...')

	window('Global Options')
	doubleclick('JTree', '//jsxe/XML Document Options')
	select('JComboBox2', '4')
	select('Soft tabs #{emulated with spaces#}', 'true')
	click('OK')
	close()

	select('SourceTextArea', '')
	keystroke('Tab')
	assertText('SourceTextArea', '    ')

	click('Tools')
	click('Options...')

	window('Global Options')
	doubleclick('JTree', '//jsxe/XML Document Options')
	select('JComboBox2', '8')
	select('Soft tabs #{emulated with spaces#}', 'true')
	click('OK')
	close()

	select('SourceTextArea', '')
	keystroke('Tab')
	assertText('SourceTextArea', '        ')
	close()
