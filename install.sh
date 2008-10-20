#! /bin/bash

. ./install.conf
DESTDIR=$1

install -d -m 755 ${DESTDIR}/${CATALINA_COMMON_LIB}/
install -m 644 build/konfirmagi.jar ${DESTDIR}/${CATALINA_COMMON_LIB}/
