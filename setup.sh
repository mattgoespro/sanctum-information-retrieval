#!/bin/bash

cd /opt
sudo wget http://mirror.za.web4africa.net/apache/maven/maven-3/3.5.4/binaries/apache-maven-3.5.4-bin.tar.gz
sudo tar -xvzf apache-maven-3.5.4-bin.tar.gz
export M2_HOME="/opt/apache-maven-3.5.4"
export PATH=$PATH:/opt/apache-maven-3.5.4/bin
sudo update-alternatives --install "/usr/bin/mvn" "mvn" "/opt/apache-maven-3.5.4/bin/mvn" 0
sudo update-alternatives --set mvn /opt/apache-maven-3.5.4/bin/mvn
sudo wget https://raw.github.com/dimaj/maven-bash-completion/master/bash_completion.bash --output-document /etc/bash_completion.d/mvn
mvn --version