useFixture(default)

def test():
	#Tests the soft-tabs and indent width options
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

	select('SourceView$SourceViewTextPane', '')
	keystroke('Tab')
	assertText('SourceView$SourceViewTextPane', '    ')

	click('Tools')
	click('Options...')

	window('Global Options')
	doubleclick('JTree', '//jsxe/XML Document Options')
	select('JComboBox2', '8')
	select('Soft tabs #{emulated with spaces#}', 'true')
	click('OK')
	close()

	select('SourceView$SourceViewTextPane', '')
	keystroke('Tab')
	assertText('SourceView$SourceViewTextPane', '        ')
	close()
