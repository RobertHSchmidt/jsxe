useFixture(default)

def test():
	# Tests splitting CDATA nodes
	window('jsXe - Untitled-1')
	doubleclick('DefaultViewTree', '/Document Root/default_element')
	rightclick('DefaultViewTree', '/Document Root/default_element/default_node')
	click('Remove Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('CDATA Section')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'New CDATA Node' ] ])

	click('DefaultViewTree', '/Document Root/default_element/New CDATA Node')
	select('JEditorPane', 'CDATA1 ]]> CDATA2')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'CDATA1 ]]> CDATA2' ] ])

	click('View')
	click('Source View')
	assertText('SourceView$SourceViewTextPane', """<?xml version="1.0" encoding="UTF-8"?>
<default_element><![CDATA[CDATA1 ]]]]><![CDATA[> CDATA2]]></default_element>""")
	
	click('View')
	click('Tree View')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'CDATA1 ]]', '> CDATA2' ] ])

	close()
