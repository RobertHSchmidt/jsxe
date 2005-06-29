#:tabSize=4:indentSize=4:noTabs=false:
#:folding=explicit:collapseFolds=1:

#a hack since the select() function doesn't seem to save the value
#works in real life though.
def setValue(name, item):
	from marathon.playback import *
	combobox = getComponent(name)
	combobox.setSelectedItem(item)
