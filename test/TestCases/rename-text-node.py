useFixture(default)

def test():
	# Tests renaming a text node
	window('jsXe - Untitled-1')
	doubleclick('DefaultViewTree', '/Document Root/default_element')
	click('DefaultViewTree', '/Document Root/default_element/default_node')
	select('JEditorPane', 'This is a test')
	assertText('JEditorPane', 'This is a test')
	click('View')
	click('Source View')
	assertText('SourceView$SourceViewTextPane', """<?xml version="1.0" encoding="UTF-8"?>
<default_element>This is a test</default_element>""")
	close()
