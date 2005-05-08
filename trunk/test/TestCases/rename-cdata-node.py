useFixture(default)

# Tests editing a CDATA Section node
def test():
	window('jsXe - Untitled-1')
	doubleclick('DefaultViewTree', '/Document Root/default_element')
	rightclick('DefaultViewTree', '/Document Root/default_element/default_node')
	click('Remove Node')
	assertContent('DefaultViewTree', [ ['Document Root', 'default_element' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Add CDATA Section')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'New CDATA Section' ] ])

	click('DefaultViewTree', '/Document Root/default_element/New CDATA Section')
	select('JEditorPane', 'CDATA Test 123 ]')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'CDATA Test 123 ]' ] ])

	click('View')
	click('Source View')
	assertText('SourceTextArea', """<?xml version="1.0" encoding="UTF-8"?>
<default_element><![CDATA[CDATA Test 123 ]]]></default_element>"""
)
	close()
