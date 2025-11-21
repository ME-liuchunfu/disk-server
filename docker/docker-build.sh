#!/bin/bash

WorkPath=$(cd "$(dirname "$0")"; pwd)

cd $WorkPath

docker build -t diskserver:v251121 .
