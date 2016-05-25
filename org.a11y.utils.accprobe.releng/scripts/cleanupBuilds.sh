#!/bin/bash

norm="\033[0;39m";
grey="\033[1;30m";
green="\033[1;32m";
brown="\033[0;33m";
yellow="\033[1;33m";
blue="\033[1;34m";
cyan="\033[1;36m";
red="\033[1;31m";

function usage()
{
	echo "usage: $0"
	echo -e "${red}-a$norm, ${red}-active$norm   <required: used to prevent accidentally running this script w/o options>"
	echo "-debug        <optional: use -debug 1 or -debug 2 for more output>"
	echo "-p, -preview  <optional: don't actually run a build, just echo the command for preview>"
	echo ""
	echo -e "NOTE: this script ${red}must be run as the web user$norm (${yellow}apache$norm or ${yellow}www-data$norm) or as ${red}root$norm.";
	echo ""
	exit 1
}

if [ $# -lt 1 ]; then
	usage;
fi

if [[ $(whoami) != "apache" ]] && [[ $(whoami) != "www-data" ]] && [[ $(whoami) != "root" ]]; then
	usage
fi

searchDirs=("/home/www-data/build/modeling" "/home/www-data/oldtests" "/home/www-data/jdk14tests" "/home/www-data/jdk50tests");
oldAge=("14" "2" "21" "14" "42" "42"); # ages, in days, as used below
oldDir="/home/www-data/OLD";
depsDir="/home/www-data/build/downloads";
egrepPattern="total|/home/www-data/|build/modeling/|/downloads/drops"; # used to add colour to output

# commandline flags
preview=0;
debug=0;
active=0;
function debug() # $3 can be set to "-n" 
{
	thresh=0;
	if [[ $2 -gt 0 ]]; then thresh=$2; fi
	if [[ $debug -gt $thresh ]]; then echo -e $3 "$1"; fi
} 

while [ "$#" -gt 0 ]; do
	case $1 in
		'-debug') debug=$2; debug "   $1 $2"; shift 1;;
		'-p'|'-preview') preview=1; debug "   $1"; shift 0;;
		'-a'|'-active') active=1; debug "   $1"; shift 0;;
		*) args=$args" $1"; debug "   $1"; shift 0;;
	esac
	shift 1
done

function execCmd()
{
	thresh=2;
	if [[ $preview -eq 1 ]] || [[ $debug -gt $thresh ]]; then # echo
		echo "$*" | perl -pe "s#^#    #";
	fi
	if [[ $preview -eq 0 ]]; then # run
		$*;
	fi
}

function moveBuilds() # $age $dir $name-pattern
{
	a=$1; # age
	d=$2; # dir
	n=$3; # name

	# after x days, move old [NIMS] builds into OLD/ 
	targets=$(find $d -maxdepth 2 -mindepth 2 -type d -name "$n" -mtime +$a);
	if [[ $targets ]]; then
		debug "  Move "$a" day old $n builds into $oldDir";
		for f in $targets; do
			g=${f/www-data/www-data\/OLD}; # OLD dir
			h=${g%/*}; # parent dir
			execCmd mkdir -p $h;
			execCmd mv $f $g;
			execCmd touch $g
		done
		debug " " 1;
	fi
}

function purgeBuilds() # $age $dir $name-pattern
{
	a=$1; # age
	d=$2; # dir
	n=$3; # name

	# after x days, purge from $dir
	if [[ -d $d ]]; then 
		targets=$(find $d -type d -name "$n" -mtime +$a);
		if [[ $targets ]]; then
			debug "  Purge "$a" day old builds from $d";
			for f in $targets; do
				execCmd rm -fr $f;
			done
			debug " " 1;
		fi
	fi
}

function purgeFiles() # $age $dir $name-pattern
{
	a=$1; # age
	d=$2; # dir
	n=$3; # name

	# after x days, purge from $dir
	if [[ -d $d ]]; then 
		targets=$(find $d -type f -name "$n" -mtime +$a);
		if [[ $targets ]]; then
			debug "  Purge "$a" day old dependencies from $d";
			for f in $targets; do
				execCmd rm -f $f;
			done
			debug " " 1;
		fi
	fi
}

function dushc()
{
	d=$*;
	if [[ -d $oldDir ]]; then
		du -shc $d $depsDir $oldDir | egrep $egrepPattern;
	else
		du -shc $d $depsDir | egrep $egrepPattern;
	fi
}

# get search directories	
i=0;
while [[ $i -lt ${#searchDirs[@]} ]]; do
	s=${searchDirs[$i]};
	if [[ -d $s ]]; then
		if [[ ${s##*test*} == "" ]]; then # tests dir
			dirs=$dirs" "$s;
		else
			dirs=$dirs" "$(find $s -mindepth 4 -maxdepth 4 -name "drops"); # drops dir
		fi
	fi
	i=$((i+1));
done
	
if [[ $active -eq 1 ]]; then
	echo "[$(date +%H:%M:%S)] Started.";
	dushc $dirs;
	
	# how many dirs to clean?
	cnt=1;
	newDirs="";
	for dir in $dirs; do
		if [[ $(find $dir -maxdepth 1 -type d) != $dir ]]; then
			newDirs=$newDirs" "$dir; 
			cnt=$((cnt+1));
		fi
	done
	dirs=$newDirs;
	
	# start working 	
	i=0;
	for dir in $dirs; do
		if [[ $(find $dir -maxdepth 1 -type d) != $dir ]]; then 
			i=$((i+1));
			debug "[$i/$cnt] BEFORE: $(du -sh $dir)" 1 | egrep $egrepPattern;
			oDir=${dir/www-data/www-data\/OLD};
			
			# 0. after x days, purge from /home/www-data/OLD/
			purgeBuilds ${oldAge[0]} $oDir "*200*";
			
			# 1. after x days, purge old [N] builds
			purgeBuilds ${oldAge[1]} $dir "N200*"  
			
			# 2. after x days, move old [M] builds into /home/www-data/OLD/
			moveBuilds ${oldAge[2]} $dir "M200*"  
			
			# 3. after x days, move old [I] builds into /home/www-data/OLD/
			moveBuilds ${oldAge[3]} $dir "I200*"  
			
			# 4. after x days, move old [S] builds into /home/www-data/OLD/
			moveBuilds ${oldAge[4]} $dir "S200*"  
			
			if [[ -d $oDir ]]; then
				debug "[$i/$cnt]  AFTER: $(du -shc $dir $oDir)" 1 | egrep $egrepPattern;
			else
				debug "[$i/$cnt]  AFTER: $(du -sh $dir)" 1 | egrep $egrepPattern;
			fi
		fi
	done
	
	i=$((i+1));
	# 5. after x days, purge old dependencies from /home/www-data/build/downloads/
	debug "[$i/$cnt] BEFORE: $(du -sh $depsDir)" 1 | egrep $egrepPattern;
	purgeFiles ${oldAge[5]} $depsDir "*.tar.gz"  
	purgeFiles ${oldAge[5]} $depsDir "*.zip"  
	debug "[$i/$cnt]  AFTER: $(du -sh $depsDir)" 1 | egrep $egrepPattern;

	echo "[$(date +%H:%M:%S)] Done.";
else
	echo -e "Nothing to do! Use ${red}-a$norm flag to activate this script.";
fi
dushc $dirs;
