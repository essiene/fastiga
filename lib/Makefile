all: compile

compile:
	@ant all

run: compile
	@scala -cp build/ Run

javacc:
	@javacc -OUTPUT_DIRECTORY=${ODIR} src/com/konfirmagi/parser/parser.jj
#	@cp src/com/konfirmagi/TestParser.java ${ODIR}
	@javac -d build/ ${ODIR}/*
	@rm -rf ${ODIR}							  

java:
	@cd build
	@java com.konfirmagi.TestParser
