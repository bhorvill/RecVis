#!/bin/sh

echo "Lancement de RecVis..."


if [ $# = 2 ]; then
	java -classpath bin/ Window "$1" "$2"
elif [ $# = 1 ]; then
	java -classpath bin/ Window "$1"
else
	java -classpath bin/ Window
fi

echo "Done."
