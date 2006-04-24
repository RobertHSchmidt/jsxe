#:tabSize=4:indentSize=4:noTabs=false:
#:folding=explicit:collapseFolds=1:

import jsxe

useFixture(recent)

#script to test the recent files menu
def test():
	jsxe.setStartingFiles([['penguins.xml', 'multi-test.xml', 'entity-test.xml']])
	jsxe.selectFile('penguins.xml')
	jsxe.closeFileByName('penguins.xml')
	jsxe.closeFileByName('multi-test.xml')
	jsxe.closeFileByName('entity-test.xml')
	
	jsxe.openRecentFile('penguins.xml')
	jsxe.openRecentFile('multi-test.xml')
	jsxe.openRecentFile('entity-test.xml')
	
	close();
