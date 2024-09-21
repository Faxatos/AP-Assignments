#!/bin/sh

PATH="com/mycompany/theeightpuzzle"

cd ../target/classes || { echo "Directory 'com/mycompany/theeightpuzzle' not found. Exiting."; exit 1; }

jar cfv ../../jars/EightTile.jar $PATH/EightTile.class
jar cfv ../../jars/EightController.jar $PATH/EightController.class
jar cfvm ../../jars/EightBoard.jar ../../jars/MANIFEST.MF \
 $PATH/EightBoard.class \
 $PATH/EightBoard'$'1.class \
 $PATH/EightBoard'$'2.class \
 $PATH/EightController.class \
 $PATH/EightTile.class \

chmod +x ../../jars/EightBoard.jar
