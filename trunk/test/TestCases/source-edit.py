useFixture(default)

def test():
	window('jsXe - Untitled-1')
	click('View')
	click('Source View')
	select('SourceView$SourceViewTextPane', '''<?xml version="1.0" encoding="UTF-8"?>
<root>
	<text_element>Text</text_element>
	<cdata_element>
		<![CDATA[CDATA]]>
	</cdata_element>
	<pi_element>
		<?Instruction New Processing Instruction?>
	</pi_element>
	<comment_element>
		<!--comment-->
	</comment_element>
</root>''')
	click('View')
	click('Tree View')

	assertContent('DefaultViewTree', [ [ 'Document Root', 'root', 'text_element', 'Text', 'cdata_element', 'CDATA', 'pi_element', 'Instruction', 'comment_element', 'comment' ] ])
	close()
