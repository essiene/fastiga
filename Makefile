all: compile

compile:
	@ant all

run: compile
	scala -cp build/WEB-INF/classes Run
