useFixture(default)

# Tests adding and removing and editing attributes
# in the attributes table
def test():
	window('jsXe - Untitled-1')
	click('DefaultViewTree', '/Document Root/default_element')
	rightclick('AttributesTable', 'Attribute,0')
	click('Add Attribute')
	select('AttributesTable', 'test1', 'Attribute,0')
	select('AttributesTable', '123', 'Value,0')
	assertContent('AttributesTable', [ [ 'test1', '123' ], [ '', '' ] ])

	rightclick('AttributesTable', 'Attribute,0')
	click('Add Attribute')
	select('AttributesTable', 'test2', 'Attribute,1')
	select('AttributesTable', '124', 'Value,1')
	assertContent('AttributesTable', [ [ 'test1', '123' ], [ 'test2', '124' ], [ '', '' ] ])

	rightclick('AttributesTable', 'Attribute,0')
	click('Remove Attribute')
	assertContent('AttributesTable', [ [ 'test2', '124' ], [ '', '' ] ])

	select('AttributesTable', 'blah', 'Attribute,0')
	select('AttributesTable', '1234', 'Value,0')
	assertContent('AttributesTable', [ [ 'blah', '1234' ], [ '', '' ] ])
	close()
