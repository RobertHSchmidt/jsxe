#:tabSize=4:indentSize=4:noTabs=false:
#:folding=explicit:collapseFolds=1:

import treeview

useFixture(default)

# Tests adding and removing and editing attributes
# in the attributes table
def test():
	window('jsXe - Untitled-1')
	click('TreeViewTree', '/Document Root/default_element')
	treeview.addAttribute('test1', '123')
	treeview.assertAttributes([ [ 'test1', '123' ] ])

	treeview.addAttribute('test2', '124')
	treeview.assertAttributes([ [ 'test1', '123' ], [ 'test2', '124' ] ])

	treeview.removeAttribute(0)
	treeview.assertAttributes([ [ 'test2', '124' ] ])

	treeview.setAttribute('blah', '1234', 0)
	treeview.assertAttributes([ [ 'blah', '1234' ] ])
	close()
