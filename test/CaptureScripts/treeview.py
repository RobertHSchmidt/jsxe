#:tabSize=4:indentSize=4:noTabs=false:
#:folding=explicit:collapseFolds=1:

from marathon.playback import *

treeName  		= 'TreeViewTree'
tableName 		= 'TreeViewAttributesTable'
textName  		= 'TreeViewTextArea'
cellEditorName 	= 'DefaultTreeCellEditor$DefaultTextField'

def expand(location):
	doubleclick(treeName, location)

def choose(location):
	click(treeName, location)

def addElementNode(location):
	rightclick(treeName, location)
	click('Add')
	click('Element')
	click('Add Element Node')

def addCDATANode(location):
	rightclick(treeName, location)
	click('Add')
	click('CDATA Section')

def addTextNode(location):
	rightclick(treeName, location)
	click('Add')
	click('Text')

def addPINode(location):
	rightclick(treeName, location)
	click('Add')
	click('Processing Instruction')

def addCommentNode(location):
	rightclick(treeName, location)
	click('Add')
	click('Comment')

def renameNode(location, name):
	rightclick(treeName, location)
	click('Rename Node')
	select(cellEditorName, name)
	keystroke('Enter')

def removeNode(location):
	rightclick(treeName, location)
	click('Remove Node')

def addAttribute(name, value):
	size = len(getContent(tableName))
	rightclick(tableName, 'Attribute,0')
	click('Add Attribute')
	select(tableName, name, 'Attribute,'+str(size-1))
	select(tableName, value, 'Value,'+str(size-1))

def removeAttribute(index):
	rightclick(tableName, 'Attribute,'+str(index))
	click('Remove Attribute')

def setAttribute(name, value, index):
	select(tableName, name, 'Attribute,'+str(index))
	select(tableName, value, 'Value,'+str(index))

def setValue(value):
	select(textName, value)

def assertTree(content):
	assertContent(treeName, content)
	
def assertAttributes(content):
	content = content + [ [ '', '' ] ]
	assertContent(tableName, content)

def assertValue(content):
	assertText(textName, content)