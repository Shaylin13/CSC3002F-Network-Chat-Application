#
# Assignment 1 Makefile
# azhar rohiman - 8/4/2017

LIB = lib
SRCDIR = src
BINDIR = bin
TESTDIR = test
DOCDIR = doc

TOOLS = $(LIB)/tools

JAVAC = javac
JFLAGS = -g -d $(BINDIR) -cp $(BINDIR)

vpath %.java $(SRCDIR)
vpath %.class $(BINDIR)

# define general build rule for java sources
.SUFFIXES: .java .class

.java.class:
	$(JAVAC) $(JFLAGS) $<
	
all: ChatServer.class ChatClient.class

CLASSFILES = ChatServer.class ChatClient.class

SOURCEFILES = ChatServer.java ChatClient.java

doc: $(CLASSFILES)
	javadoc -version -author -d $(doc) $(SOURCEFILES)

Server:
	java -classpath bin ChatServer

Client:
	java -classpath bin ChatClient
   
clean:
	@rm -f $(BINDIR)/*.class
	@rm -f $(BINDIR)/*/*.class
