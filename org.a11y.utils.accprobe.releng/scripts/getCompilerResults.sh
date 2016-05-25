#!/bin/bash

# $Id: getCompilerResults.sh,v 1.4 2007/12/26 17:17:25 msquillac Exp $

# given a directory (./compilelogs)
# recurse through all files and look for files containing 
#   975 problems (975 warnings)
# generate a summary file which contains a count of problems, warnings, errors, and failures in the format:
#   975P, 975W, 0E, 0F
# or if none found, file is not created

# set working dir
if [ "x$1" != "x" ]; then
  dir="$1";
else
  dir="./compilelogs";
fi

debug=0;

prob=0;
warn=0;
err=0;
fail=0;

# get files
for f in $(find $dir -type f -name "*.log" | sort); do
  results=$(tail -1 $f | perl -pe 's/([0-9]+) /$1_/g' | perl -pe 's/[^a-z0-9\_\ ]+//g' | egrep "[0-9]+\_(problem|warning|error|failure)");
  if [ $debug -gt 0 ]; then echo -n "$f : "; fi
  for b in $results; do
    p=$(echo $b | perl -pe 's/(\d+)_problems?/$1/g' | egrep "^[0-9]+$"); if [ "$p" != "" ]; then (( prob += p )); fi
    p=$(echo $b | perl -pe 's/(\d+)_warnings?/$1/g' | egrep "^[0-9]+$"); if [ "$p" != "" ]; then (( warn += p )); fi
    p=$(echo $b | perl -pe 's/(\d+)_errors?/$1/g'   | egrep "^[0-9]+$"); if [ "$p" != "" ]; then (( err += p ));  fi
    p=$(echo $b | perl -pe 's/(\d+)_failures?/$1/g' | egrep "^[0-9]+$"); if [ "$p" != "" ]; then (( fail += p )); fi
  done
  if [ $debug -gt 0 ]; then echo $prob"P, "$warn"W, "$err"E, "$fail"F"; fi
done

# no output if all's good, otherwise breakdown by prob, warn, err, fail
if [ $prob -gt 0 ] || [ $warn -gt 0 ] || [ $err -gt 0 ] || [ $fail -gt 0 ]; then
  echo $prob"P, "$warn"W, "$err"E, "$fail"F"; 
fi

