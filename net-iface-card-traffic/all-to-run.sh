#!/bin/bash

mvn clean package
cd target/classes
java -cp .:../lib/* com.fsyy.netifacecardtraffic.NetIfaceCardTrafficApplication
