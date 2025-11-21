#!/bin/bash

WorkPath=$(cd "$(dirname "$0")"; pwd)

cd $WorkPath

APP_FILE_NAME=disk-server-1.0-SNAPSHOT.jar
LOG_FILE=start.log
java -jar $APP_FILE_NAME >> $LOG_FILE 2>$1 &

