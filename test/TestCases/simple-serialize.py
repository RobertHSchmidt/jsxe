useFixture(default)

def test():
	window('jsXe - Untitled-1')
	doubleclick('DefaultViewTree', '/Document Root/default_element')
	rightclick('DefaultViewTree', '/Document Root/default_element/default_node')
	click('Remove Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Element Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'New_Element' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/New_Element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'element1')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1' ] ])
	
	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Element Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New_Element' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/New_Element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'element2')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'element2' ] ])
	
	rightclick('DefaultViewTree', '/Document Root/default_element')
	click('Add')
	click('Element Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'element2', 'New_Element' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/New_Element')
	click('Rename Node')
	select('DefaultTreeCellEditor$DefaultTextField', 'element3')
	keystroke('Enter')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'element2', 'element3' ] ])
	
	rightclick('DefaultViewTree', '/Document Root/default_element/element1')
	click('Add')
	click('Text Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'element3' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/element2')
	click('Add')
	click('Text Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3' ] ])

	rightclick('DefaultViewTree', '/Document Root/default_element/element3')
	click('Add')
	click('Text Node')
	assertContent('DefaultViewTree', [ [ 'Document Root', 'default_element', 'element1', 'New Text Node', 'element2', 'New Text Node', 'element3', 'New Text Node' ] ])
	
	click('View')
	click('Source View')
	assertText('SourceView$SourceViewTextPane', """<?xml version="1.0" encoding="UTF-8"?>
<default_element><element1>New Text Node</element1><element2>New Text Node</element2><element3>New Text Node</element3></default_element>""")
	close()
