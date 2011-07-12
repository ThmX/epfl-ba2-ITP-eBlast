##############################################################################
# eBlast Project (ITF - EPFL - BA2)
# Doxygen Documentation
##############################################################################

DOXYGEN = doxygen
DOXYFILE = Doxyfile

all : eblast

eblast:
	$(DOXYGEN) $(DOXYFILE)

.PHONY: clean

clean:

mrproper: 
	@rm -rf html/ latex/
