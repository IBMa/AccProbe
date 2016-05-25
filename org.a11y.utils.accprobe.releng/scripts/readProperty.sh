#!/bin/sh



if [[ $# -lt 2 ]]; then



  echo "Usage:   $0 <filename> <propertyName>";



  echo "Example: $0 ../../server-config/build.eclipse.org.properties JAVA_HOME"; 



  exit 1;



fi



file=$1



property=$2



grep $property $file | egrep -v "^#" | tail -1 | sed -e "s/$property=//"



