#!/bin/bash

if [ ! -f master.zip ]; then
	wget /home/hadoop https://github.com/mattgoespro/sanctum_ir/archive/master.zip
fi

unzip master.zip
cd sanctum_ir-master
mvn package
cd ..
rm -f master.zip
