#!/bin/sh

echo " "; echo "[`date +%H\:%M\:%S`]"; echo "  $1" | perl -pe "s/ -/\n  -/g";
$1
