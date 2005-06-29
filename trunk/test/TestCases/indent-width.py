#:tabSize=4:indentSize=4:noTabs=false:
#:folding=explicit:collapseFolds=1:

import sourceview, combobox

useFixture(default)

# Tests the soft-tabs and indent width options
def test():
	window('jsXe - Untitled-1')
	click('View')
	click('Source View')
	click('Tools')
	click('Options...')

	window('Global Options')
	click('JTree', '//jsxe/XML Document Options')
	combobox.setValue('IndentComboBox', '3')
	select('Soft tabs #{emulated with spaces#}', 'true')
	click('OK')
	close()

	sourceview.setText('')
	click('TextAreaPainter')
	keystroke('Tab')
	sourceview.assertText('   ')

	click('Tools')
	click('Options...')

	window('Global Options')
	click('JTree', '//jsxe/XML Document Options')
	combobox.setValue('IndentComboBox','8')
	click('OK')
	close()

	sourceview.setText('')
	click('TextAreaPainter')
	keystroke('Tab')
	sourceview.assertText('        ')
	close()
