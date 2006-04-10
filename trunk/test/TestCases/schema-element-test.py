#:tabSize=4:indentSize=4:noTabs=false:
#:folding=explicit:collapseFolds=1:

useFixture(schema-test)

# Tests the completion info feature for schema
def test():
	window('jsXe - schema-test.xml')
	doubleclick('TreeViewTree', '/Document Root/doc')
	assertContent('TreeViewTree', [ [ 'Document Root', 'doc', 'para' ] ])

	click('Tools')
	click('Options...')

	window('Global Options')
	select('Validate if DTD or Schema Available', 'true')
	select('Format XML output', 'true')
	select('Soft tabs #{emulated with spaces#}', 'true')
	click('OK')
	close()

	doubleclick('TreeViewTree', '/Document Root/doc')
	rightclick('TreeViewTree', '/Document Root/doc/para')
	click('Remove Node')
	assertContent('TreeViewTree', [ [ 'Document Root', 'doc' ] ])

	rightclick('TreeViewTree', '/Document Root/doc')
	click("Add")
	click("Element")
	click('para')

	window('Edit Node')
	select('EditTagDialog$AttributeTable', 'true', 'Set,0')
	select('EditTagDialog$AttributeTable', 'center', 'Value,0')
	click('OK')
	close()

	click('TreeViewTree', '/Document Root/doc/para')
	assertContent('TreeViewTree', [ [ 'Document Root', 'doc', 'para' ] ])
	assertContent('TreeViewAttributesTable', [ [ 'align', 'center'], [ '', '' ] ])
	close()
