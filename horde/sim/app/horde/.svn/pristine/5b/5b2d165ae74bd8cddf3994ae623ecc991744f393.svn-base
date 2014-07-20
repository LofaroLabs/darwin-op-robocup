
#JAVAC = /System/Library/Frameworks/JavaVM.framework/Versions/1.6/Home/bin/javac ${JAVACFLAGS}
JAVAC = javac ${JAVACFLAGS}

JAVACFLAGS = -target 1.6 -source 1.6 ${FLAGS}
JIKESFLAGS = -target 1.6 +Pno-shadow ${FLAGS}
FLAGS = -g -nowarn

# Main java files, not including the 3D stuff
DIRS = \
./*.java \
behaviors/*.java \
comm/*.java \
darwin/*.java \
darwin/behaviors/*.java \
darwin/features/*.java \
darwin/agent/*.java \
darwin/targets/*.java \
darwin/comm/*.java 

# Make the main MASON code, not including 3D code
all:
	${JAVAC} ${DIRS}


# Delete all jmf gunk, checkpoints, backup emacs gunk classfiles,
# documentation, and odd MacOS X poops
clean:
	find . -name "*.class" -exec rm -f {} \;
	find . -name "jmf.log" -exec rm -f {} \;
	find . -name ".DS_Store" -exec rm -f {} \; 
	find . -name "*.checkpoint" -exec rm -f {} \;
	find . -name "*.java*~" -exec rm -f {} \;
	find . -name ".#*" -exec rm -rf {} \;
	find . -name "*.java.orig" -exec rm -f {} \;	

# Indent to your preferred brace format using emacs.  MASON's default
# format is Whitesmiths at 4 spaces.  Yes, I know.  Idiosyncratic.
# Anyway, beware that this is quite slow.  But it works!
indent: 
	touch ${HOME}/.emacs
	find . -name "*.java" -print -exec emacs --batch --load ~/.emacs --eval='(progn (find-file "{}") (mark-whole-buffer) (setq indent-tabs-mode nil) (untabify (point-min) (point-max)) (indent-region (point-min) (point-max) nil) (save-buffer))' \;
