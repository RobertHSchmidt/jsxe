useFixture(default)

def test():
	#Tests the soft-tabs and indent width options
	window('jsXe - Untitled-1')
	click('View')
	click('Source View')
	click('Tools')
	click('Options...')

	window('Global Options')
	click('JTree', '//jsxe/Source View Options')
	select('Soft tabs #{emulated with spaces#}', 'true')
	doubleclick('JTree', '//jsxe/XML Document Options')
	select('JComboBox2', '4')
	click('OK')
	close()

	select('SourceView$SourceViewTextPane', '')
	keystroke('Tab')
	assertText('SourceView$SourceViewTextPane', '    ')

	click('Tools')
	click('Options...')

	window('Global Options')
	click('JTree', '//jsxe/Source View Options')
	select('Soft tabs #{emulated with spaces#}', 'true')
	doubleclick('JTree', '//jsxe/XML Document Options')
	select('JComboBox2', '8')
	click('OK')
	close()

	select('SourceView$SourceViewTextPane', '')
	keystroke('Tab')
	assertText('SourceView$SourceViewTextPane', '        ')
	close()
