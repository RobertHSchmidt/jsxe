useFixture(default)

def test():
	window('jsXe - Untitled-1')
	doubleclick('DefaultViewTree', '/Document Root/default_element')
	rightclick('DefaultViewTree', '/Document Root/default_element/default_node')
	click('Remove Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element' ] ])
	
	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Comment')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'New Comment Node' ] ])
	
	click('DefaultViewTree', '/Document Root/default_element/New Comment Node')
	select('JEditorPane', 'Comment test 123 ]]> [[> <[!CDATA')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'Comment test 123 ]]> [[> <[!CDATA' ] ])
	
	click('View')
	click('Source View')
	assertText('SourceView$SourceViewTextPane', """<?xml version="1.0" encoding="UTF-8"?>
<default_element><!--Comment test 123 ]]> [[> <[!CDATA--></default_element>""")
	
	close()
