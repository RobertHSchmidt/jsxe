useFixture(schema)

# Tests the completion info feature for schema
def test():
	window('jsXe - schema-test.xml')
	doubleclick('DefaultViewTree', '/Document Root/doc')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'doc', 'para' ] ])

	click('Tools')
	click('Options...')

	window('Global Options')
	select('Validate if DTD or Schema Available', 'true')
	select('Format XML output', 'true')
	select('Soft tabs #{emulated with spaces#}', 'true')
	click('OK')
	close()

	rightclick('DefaultViewTree', '/Document Root/doc/para')
	click('Remove Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'doc' ] ])

	rightclick('DefaultViewTree', '/Document Root/doc')
	click("Add")
	click("Element")
	click('para')

	window('Edit Tag')
	select('EditTagDialog$AttributeTable', 'true', 'Set,0')
	select('EditTagDialog$AttributeTable', 'center', 'Value,0')
	click('OK')
	close()

	click('DefaultViewTree', '/Document Root/doc/para')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'doc', 'para' ] ])
	assertContent('AttributesTable', [ [ 'align', 'center'], [ '', '' ] ])
	close()
