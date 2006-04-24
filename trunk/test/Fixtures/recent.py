#:tabSize=4:indentSize=4:noTabs=false:
#:folding=explicit:collapseFolds=1:

from net.sourceforge.jsxe import jsXe

class Fixture:
	def start_application(self):
		args = ['../test/penguins.xml', '../test/multi-test.xml', '../test/entity-test.xml']
		jsXe.main(args)

	def teardown(self):
		pass

	def setup(self):
		self.start_application()

