NAME=fastiga
VERSION=0.1

TARGETS=build/$(NAME).jar

all: $(TARGETS)
	@echo "All Done"

build/$(NAME).jar: 
	@cd lib && ant all && cd ..
	@mkdir -p build
	@cp lib/dist/$(NAME).jar $@

install: 
	@./install.sh $(DESTDIR)

clean:
	@cd lib && ant distclean clean && cd ..
	@rm -f sql/$(NAME).sql 
	@rm -rf build

run:
	@cd lib;make run

dist: clean
	@mkdir -p $(NAME)-$(VERSION)
	@cp -r lib $(NAME)-$(VERSION)/
	@cp install.* Makefile INSTALL $(NAME).spec $(NAME)-$(VERSION)/
	@tar -czvf $(NAME)-$(VERSION).tar.gz $(NAME)-$(VERSION)/*
	@rm -rf $(NAME)-$(VERSION)

rpm: dist
	@rm -rf /usr/src/redhat/SOURCES/$(NAME)*
	@rm -rf /usr/src/redhat/RPMS/i386/$(NAME)*
	@mv $(NAME)-$(VERSION).tar.gz /usr/src/redhat/SOURCES/
	@cp $(NAME).spec /usr/src/redhat/SPECS/
	@rpmbuild -bb /usr/src/redhat/SPECS/$(NAME).spec
	@mv /usr/src/redhat/RPMS/i386/$(NAME)*.rpm .
