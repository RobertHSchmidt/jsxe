#:tabSize=4:indentSize=4:noTabs=false:
#:folding=explicit:collapseFolds=1:

from marathon.playback import *
from net.sourceforge.jsxe.gui import jsxeFileDialog

currentFile = ''
openFiles = [[ 'you must call setStartingFiles' ]]
untitledNums = []
fileSelector = 'TabbedView'
fileChooser = 'jsXeFileDialog'
title = 'jsXe - '

#TODO: create a setDirty to allow treeview and sourceview to set the current file dirty

def setStartingFiles(files):
	global openFiles
	global currentFile
	global untitledNums
	
	openFiles=files
	currentFile=openFiles[0][len(openFiles[0])-1]
	if currentFile=='Untitled-1':
		untitledNums.append(1)
	jsxewindow(currentFile)
	assertOpenFiles()

def newFile():
	global openFiles
	global currentFile
	
	num=max(untitledNums)+1
	untitledName='Untitled-'+str(num)
	click('File')
	click('New')
	close()
	jsxewindow(untitledName)
	addToOpenFiles(untitledName)
	assertOpenFiles()

#open a file from the current directory (normally test)
#not sure this works
def openFile(name):
	click('File')
	click('Open...')
	window('Open')
	click(fileChooser, name)
	click('Open')
	close()
	addToOpenFiles(name)
	assertOpenFiles()

def openRecentFile(name):
	global openFiles
	click('File')
	click('Recent Files')
	click(name)
	jsxewindow(name)
	
	addToOpenFiles(name)
	
	#hack (assume untitled file is clean)
	# I just opened a file with 1 Untitled file
	if len(openFiles[0])==2 and openFiles[0][0].find('Untitled-')!=-1:
		removeFromOpenFiles(openFiles[0][0])
	
	assertOpenFiles()

def closeFile():
	global currentFile
	global openFiles
	
	click('File')
	click('Close')
	close()
	removeFromOpenFiles(currentFile)
	jsxewindow(currentFile)
	assertOpenFiles()

def closeFileByName(name):
	global currentFile
	
	selectFile(name)
	_closeFile()
	removeFromOpenFiles(currentFile)
	jsxewindow(currentFile)
	assertOpenFiles()

def closeDirtyFile():
	global currentFile
	
	_closeFile()
	window('Unsaved Changes')
	click('No')
	close()
	removeFromOpenFiles(currentFile)
	jsxewindow(currentFile)
	assertOpenFiles()

def closeDirtyFileByName(name):
	selectFile(name)
	jsxewindow(name)
	closeDirtyFile()

def selectFile(name):
	global currentFile
	
	select(fileSelector, name)
	close()
	currentFile=name
	jsxewindow(currentFile)
	
def assertFiles(content):
	assertContent(fileSelector, content)

def setView(view):
	click('View')
	click(view)

#the following are utility methods do not call from a script
def jsxewindow(newFile):
	global currentFile
	currentFile=newFile
	window(title+currentFile)

def addToOpenFiles(name):
	global untitledNums
	global openFiles
	
	if name.find('Untitled-')!=-1:
		untitledNums.append(int(name[9:]))
	openFiles[0].append(name)

def removeFromOpenFiles(name):
	global currentFile
	global openFiles
	global untitledNums
	
	if name==currentFile:
		index = openFiles[0].index(name)+1
		if index>=len(openFiles[0]):
			index=index-2
		currentFile=openFiles[0][index]
	if name.find('Untitled-')!=-1:
		untitledNums.remove(int(name[9:]))
	openFiles[0].remove(name)
	if len(openFiles[0])==0:
		openFiles[0].append('Untitled-1')
		currentFile='Untitled-1'
		untitledNums.append(1)

def assertOpenFiles():
	global openFiles
	print openFiles
	assertFiles(openFiles)

def _closeFile():
	click('File')
	click('Close')
	close()