#:tabSize=4:indentSize=4:noTabs=false:
#:folding=explicit:collapseFolds=1:

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
	setcombobox('3')
	select('Soft tabs #{emulated with spaces#}', 'true')
	click('OK')
	close()

	settext('')
	click('TextAreaPainter')
	keystroke('Tab')
	checktext('    ')

	click('Tools')
	click('Options...')

	window('Global Options')
	click('JTree', '//jsxe/XML Document Options')
	setcombobox('8')
	click('OK')
	close()

	settext('')
	click('TextAreaPainter')
	keystroke('Tab')
	checktext('        ')
	close()

def checktext(expected):
	textarea = getComponent('SourceTextArea')
	
	if not (textarea.getText() == expected):
		fail('Text in sourceview "'+textarea.getText()+'" does not match "'+expected+'"')

def settext(text):
	textarea = getComponent('SourceTextArea')
	
	textarea.setText(text)

#a hack since the select() function doesn't seem to save the value
#works in real life though.
def setcombobox(item):
	combobox = getComponent('IndentComboBox')
	combobox.setSelectedItem(item)
