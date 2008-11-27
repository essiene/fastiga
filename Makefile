NAME=fastiga
VERSION=0.1

TARGETS=build/$(NAME).jar build/$(NAME).war

all: $(TARGETS)
	@echo "All Done"

build/$(NAME).jar: 
	@ant dist-lib

build/$(NAME).war:
	@ant dist

install: 
	@./install.sh $(DESTDIR)

clean:
	@ant clean

distclean:
	@ant distclean

dist: distclean
	@mkdir -p $(NAME)-$(VERSION)
	@cp -r lib src tests web config examples $(NAME)-$(VERSION)/
	@cp build.properties build.xml install.conf install.sh Makefile TODO README INSTALL $(NAME).spec $(NAME)-$(VERSION)/
	@tar -czvf $(NAME)-$(VERSION).tar.gz $(NAME)-$(VERSION)/
	@rm -rf $(NAME)-$(VERSION)

rpm: dist
	@rm -rf /usr/src/redhat/SOURCES/$(NAME)*
	@rm -rf /usr/src/redhat/RPMS/i386/$(NAME)*
	@mv $(NAME)-$(VERSION).tar.gz /usr/src/redhat/SOURCES/
	@cp $(NAME).spec /usr/src/redhat/SPECS/
	@rpmbuild -bb /usr/src/redhat/SPECS/$(NAME).spec
	@mv /usr/src/redhat/RPMS/i386/$(NAME)*.rpm .
