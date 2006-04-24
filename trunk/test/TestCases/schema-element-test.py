#:tabSize=4:indentSize=4:noTabs=false:
#:folding=explicit:collapseFolds=1:

import treeview, jsxe

useFixture(schema)

# Tests the completion info feature for schema
def test():
	jsxe.setStartingFiles([['schema-test.xml']])
	treeview.assertTree([ [ 'Document Root', 'doc', 'para' ] ])
	treeview.expand('/Document Root/doc')
	

	click('Tools')
	click('Options...')

	window('Global Options')
	select('Validate if DTD or Schema Available', 'true')
	select('Format XML output', 'true')
	select('Soft tabs #{emulated with spaces#}', 'true')
	click('OK')
	close()

	treeview.expand('/Document Root/doc')
	treeview.removeNode('/Document Root/doc/para')
	treeview.assertTree([ [ 'Document Root', 'doc' ] ])

	treeview.addElementNodeByName('/Document Root/doc', "para")

	window('Edit Node')
	select('EditTagDialog$AttributeTable', 'true', 'Set,0')
	select('EditTagDialog$AttributeTable', 'center', 'Value,0')
	click('OK')
	close()

	treeview.choose('/Document Root/doc/para')
	treeview.assertTree([ [ 'Document Root', 'doc', 'para' ] ])
	treeview.assertAttributes([ ['align', 'center'] ])
	close()
