#:tabSize=4:indentSize=4:noTabs=false:
#:folding=explicit:collapseFolds=1:

from marathon.playback import *

fileSelector = 'JTabbedPane'

def newFile():
	click('File')
	click('New')
	close()

def closeFile():
	click('File')
	click('Close')
	close()

def closeDirtyFile():
	closeFile()
	window('Unsaved Changes')
	click('No')
	close()

def closeDirtyFileByName(name):
	selectFile(name)
	closeDirtyFile()

def selectFile(name):
	select(fileSelector, name)
	close()
	
def assertFiles(content):
	assertContent(fileSelector, content)

def setView(view):
	click('View')
	click(view)