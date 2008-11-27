#! /bin/bash

. ./install.conf
DESTDIR=$1

install -d -m 755 ${DESTDIR}/${CATALINA_COMMON_LIB}/
install -d -m 755 ${DESTDIR}/${CATALINA_WEBAPPS}/

install -m 644 dist/fastiga.jar ${DESTDIR}/${CATALINA_COMMON_LIB}/
install -m 644 dist/fastiga.war ${DESTDIR}/${CATALINA_WEBAPPS}/
