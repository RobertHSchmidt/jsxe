all: design.ps design.dvi

design.ps: design.dvi
	dvips design.dvi

design.dvi: design.tex uistructure.eps
	latex design.tex

clean:
	rm -f design.dvi
	rm -f design.ps
	rm -f design.log
	rm -f design.aux
	rm -f *~
