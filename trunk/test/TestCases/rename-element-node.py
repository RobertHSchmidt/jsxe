useFixture(default)

def test():
	window('jsXe - Untitled-1')
	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'renamed_element')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'renamed_element', 'default_node' ] ])
	click('View')
	click('Source View')
	assertText('SourceView$SourceViewTextPane', """<?xml version="1.0" encoding="UTF-8"?>
<renamed_element>default_node</renamed_element>""")
	close()

