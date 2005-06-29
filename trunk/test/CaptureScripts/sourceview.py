#:tabSize=4:indentSize=4:noTabs=false:
#:folding=explicit:collapseFolds=1:

from marathon.playback import *

textAreaName = 'SourceTextArea'

#checks the sourceview text against the given text
def assertText(expected):
	textarea = getComponent(textAreaName)
	if not (textarea.getText() == expected):
		fail('Text in sourceview: "'+textarea.getText()+'" does not match "'+expected+'"')

def assertSelectedText(expected):
	textarea = getComponent(textAreaName)
	if not (textarea.getSelectedText() == expected):
		fail('Selected text: "'+textarea.getSelectedText()+'" does not match "'+expected+'"')

# sets the sourceview's text
def setText(text):
	textarea = getComponent(textAreaName)
	textarea.setText(text)
