from net.sourceforge.jsxe import jsXe

class Fixture:
	def start_application(self):
		args = []
		jsXe.main(args)

	def teardown(self):
		pass

	def setup(self):
		self.start_application()

