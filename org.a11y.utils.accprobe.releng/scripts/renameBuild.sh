#!/bin/bash

#  build renamer script - fix/rename all of a given I/M/S build's artefacts instead of regen'ing them
#    copy existing build folder; rename zips, md5s; sed text files

# default path to builds
buildDropsDir=$PWD;

tmpdir=/home/`whoami`/tmp-renameBuild.sh-$USER;
mkdir -p $tmpdir;

verbose="";
pairs="";

# to use these colour escapes, must do `echo -e -e`
red="\033[1;31m";		green="\033[1;32m";
yellow="\033[1;33m";	blue="\033[1;34m"
norm="\033[0;39m";

# default to default properties file
defaultPropertiesFile=./promoteToEclipse.properties
propertiesFiles="";

if [ $# -lt 1 ]; then
  echo "";
  echo "[ren] This script is used to copy, then rename an existing local build so that it can be promoted";
  echo "      again under a new name. By renaming instead of rebuilding, binary compatibility is possible";
  echo "      w/o having to retest. If -sub is not specified, script will look for the folder to rename ";
  echo -e "      in the current folder ("$yellow""$PWD""$norm").";
  echo "";
  echo "[ren] Usage (<> = required, [] = optional, string replacements must be listed LAST, and will be";
  echo "      processed IN ORDER listed.):";
  echo "";
  echo -e " "$green"sudo -u www-data $PWD/renameBuild.sh"$norm" [-sub <subprojectName>] \\"
  echo -e "   -branch [sourceBranch"$blue"="$norm"]<targetBranch> -buildID <sourceBuildID"$blue"="$norm"targetBuildID> \\"
  echo -e "   -buildAlias [sourceAlias"$blue"="$norm"]<targetAlias> \\";
  echo -e "   [-verbose] [-buildDropsDir /path/to/some/folder] \\";
  echo -e "   [beforestring"$blue"="$norm"afterstring] [beforestring2"$blue"="$norm"afterstring2] ... \\";
  echo -e "   "$yellow"2>&1 | tee ~/renameBuild_\`date +%Y%m%d_%H%M%S\`.txt"$norm"";
  echo "";
  echo "[ren] To rename with string replacements [Eclipse driver folder & file]: ";
  echo "";
  echo -e " "$green"sudo -u www-data $PWD/renameBuild.sh"$norm" -sub "$red"emf"$norm" -verbose \\";
  echo -e "   -branch 2.2.0 -buildID S200606271057"$blue"="$norm"R200606271057 -buildAlias 2.2.0RC9a"$blue"="$norm"2.2.0 \\";
  echo -e "   eclipse-SDK-M20060609-1217-linux-gtk.tar.gz"$blue"="$norm"eclipse-SDK-3.2-linux-gtk.tar.gz \\";
  echo -e "   M20060609-1217"$blue"="$norm"R-3.2-200606281325 \\";
  echo -e "   /eclipse/downloads/drops/"$blue"="$norm"http://download.eclipse.org/downloads/drops/ \\"; # TODO: remove this hack once index.html pages are fixed after 2.2.0
  echo -e "   fullmoon.torolab.ibm.com"$blue"="$norm"download.eclipse.org";
  echo "";
  echo "[ren] To rename with string replacements [Eclipse + EMF driver folders & files]: ";
  echo "";
  echo -e " "$green"sudo -u www-data $PWD/renameBuild.sh"$norm" -sub "$red"uml2"$norm" -verbose \\";
  echo -e "   -branch 2.0.0 -buildID S200606221411"$blue"="$norm"R200606221411 -buildAlias 2.0RC9a"$blue"="$norm"2.0.0 \\";
  echo -e "   eclipse-SDK-M20060609-1217-linux-gtk.tar.gz"$blue"="$norm"eclipse-SDK-3.2-linux-gtk.tar.gz \\";
  echo -e "   M20060609-1217"$blue"="$norm"R-3.2-200606281325 \\";
  echo -e "   /eclipse/downloads/drops/"$blue"="$norm"http://download.eclipse.org/downloads/drops/ \\";   # TODO: remove this hack once index.html pages are fixed after 2.0.0
  echo -e "   emf-sdo-xsd-SDK-2.2.0RC9.zip"$blue"="$norm"emf-sdo-xsd-SDK-2.2.0.zip \\";
  echo -e "   S200606221156"$blue"="$norm"R200606271057 \\";
  #echo -e "   /tools/emf/downloads/drops/"$blue"="$norm"../../../../../../tools/emf/downloads/drops/ \\"; # TODO: remove this hack once index.html pages are fixed after 2.0.0
  echo -e "   fullmoon.torolab.ibm.com"$blue"="$norm"download.eclipse.org";
  echo -e "   emf.torolab.ibm.com"$blue"="$norm"download.eclipse.org";
  echo "";
  echo -e " "$green"sudo -u www-data $PWD/renameBuild.sh"$norm" -sub "$red"ocl"$norm" -verbose \\";
  echo -e "   -branch 1.0.0 -buildID S200606261119"$blue"="$norm"R200606261119 -buildAlias 1.0.0RC6"$blue"="$norm"1.0.0 \\";
  echo -e "   eclipse-SDK-M20060609-1217-linux-gtk.tar.gz"$blue"="$norm"eclipse-SDK-3.2-linux-gtk.tar.gz \\";
  echo -e "   M20060609-1217"$blue"="$norm"R-3.2-200606281325 \\";
  echo -e "   emf-sdo-xsd-SDK-2.2.0RC9.zip"$blue"="$norm"emf-sdo-xsd-SDK-2.2.0.zip \\";
  echo -e "   S200606221156"$blue"="$norm"R200606271057 \\";
  echo -e "   fullmoon.torolab.ibm.com"$blue"="$norm"download.eclipse.org";
  echo -e "   emf.torolab.ibm.com"$blue"="$norm"download.eclipse.org";
  echo "";
  exit;
fi

echo "";
echo -e "[ren] Started `date +%Y%m%d\ %H\:%M\:%S`.";  

# collect cmdline options
while [ $# -gt 0 ]; do
    case $1 in
    	'-branch') 
   			# check if param 2 contains a "="
			t=$2;t=${t##*=*}; # get a nullstring if there was a "=" in the string
			if [ "x$t" = "x" ]; then # $
	    		sourceBranch=$2; sourceBranch=${sourceBranch%%=*}; # trim from = to end
				targetBranch=$2; targetBranch=${targetBranch##*=}; # trim up to the =
			else
				sourceBranch=$2;
				targetBranch=$2;
			fi
			shift 1;
			;;
    	'-buildID') 
    		sourceBuildID=$2; sourceBuildID=${sourceBuildID%%=*}; # trim from = to end
			targetBuildID=$2; targetBuildID=${targetBuildID##*=}; # trim up to the =
			shift 1;
			;;
    	'-buildAlias') 
			# check if param 2 contains a "="
			t=$2;t=${t##*=*}; # get a nullstring if there was a "=" in the string
			if [ "x$t" = "x" ]; then # $
	    		sourceAlias=$2; sourceAlias=${sourceAlias%%=*}; # trim from = to end
				targetAlias=$2; targetAlias=${targetAlias##*=}; # trim up to the =
			else
				sourceAlias=$sourceBuildID;
				targetAlias=$2;
			fi
			shift 1;
			;;
		'-sub')
			subprojectName=$2;
			#echo "   $1 $2";
			# chain them together in order of priority: override (if applic), subproj specific one, default
			propertiesFiles=$propertiesFiles" ./promoteToEclipse."$subprojectName".properties "$defaultPropertiesFile; 
			loaded=0;
			for propertiesFile in $propertiesFiles; do
			  if [ "$loaded" -eq 0 ] && [ -r $propertiesFile ]; then 
				echo -n "    [loading $propertiesFile ... "; . $propertiesFile; echo "done]"; loaded=1;
			  fi
			done
			if [ "$loaded" -eq 0 ]; then
			    echo "    [Can't load any of: $propertiesFiles. Exiting!]";
			    exit 99;
			fi
			shift 1
			;;

        '-buildDropsDir')
            buildDropsDir=$2;
            shift 1;
            ;;
        '-verbose')
            verbose="true";
            shift 0;
            ;;
        *)
            pairs=$pairs" "$1;
            shift 0;
            ;;
    esac
    shift 1
done

if [ "$subprojectName" = "" ]; then # no value set!
  echo "[ren] No subproject name set in properties file or by -sub flag. Script cannot continue. Exiting...";
  exit 99;
fi

getBuildType () 
{
  tmpType=$1; tmpType=${tmpType:0:1}; # one of N, M, I, S, R
  #echo -e "tmpType=$tmpType";
  case $tmpType in
    'N')
    tmpType='Nightly';
    ;;
    'M')
    tmpType='Maintenance';
    ;;
    'I')
    tmpType='Integration';
    ;;
    'S')
    tmpType='Stable';
    ;;
    'R')
    tmpType='Release';
    ;;
  esac
}

getBuildType $sourceBuildID; sourceType=$tmpType;
getBuildType $targetBuildID; targetType=$tmpType;

echo "";
echo -e "[ren] Source: branch=$red$sourceBranch$norm; build=$red$sourceBuildID$norm; type=$red$sourceType$norm; alias=$red$sourceAlias$norm";
echo -e "[ren] Target: branch=$green$targetBranch$norm; build=$green$targetBuildID$norm; type=$green$targetType$norm; alias=$green$targetAlias$norm";
if [ "x$verbose" != "x" ] && [ "x$pairs" != "x" ]; then
  echo -e "[ren] Substitution pairs:";
  for pair in $pairs; do
      before=$pair; before=${before%%=*}; # trim from the = to the end 
      after=$pair;  after=${after##*=};  # trim up to the = 
      if [ "$before" != "$after" ]; then
          echo -e "        $red$before$norm -> $green$after$norm";
      fi
  done
fi

# define source/target folders
sourceFolder="$buildDropsDir/$sourceBranch/$sourceBuildID";
targetFolder="$buildDropsDir/$targetBranch/$targetBuildID";

echo ""; 
echo -e "[ren] Step 1: copy $sourceFolder";
echo -e "                to $targetFolder";
mkdir -p $targetFolder; cp -r $sourceFolder/* $targetFolder/;

echo ""; echo -e "[ren] Step 2: rename any zip/md5 files in target folder matching "$sourceBuildID;
cd $targetFolder;

list=`find $targetFolder -name "*$sourceBuildID\.zip*" -o -name "*$sourceAlias\.zip*"`
num=0;
for file in $list; do
  (( num++ ));
done

cnt=0;
for file in $list; do
  (( cnt++ ));
  targ="$file";
  targ="${targ//$sourceAlias/$targetAlias}";
  targ="${targ//$sourceBuildID/$targetAlias}";
  targ="${targ//$sourceBranch/$targetBranch}";
  if [ "x$verbose" != "x" ]; then
    echo -e "[ren] [$blue$cnt$norm/$blue$num$norm] "${file##*\/}" -> "${targ##*\/};
  fi
  mv -f "$file" "$targ";
done

if [ "x$verbose" = "x" ]; then
  echo -e "[ren] $green$num$norm files renamed.";
fi

echo ""; 
echo -e "[ren] Step 3: fix text files (xml, md5, html, txt) with $red$sourceAlias$norm, $red$sourceBuildID$norm, $red$sourceBranch$norm or $red$sourceType$norm";

# pass in a /fully/qualifed/path/to/a/file and a group owner (eg., www-data); fsize will contain the filesize in bytes
getFileSize() 
{
  f=$1; # file name
  g=$2; # group name
  fsize=`alias ls='ls' && ls -ls "$f"`; fsize=${fsize%%"$f"*}; fsize=${fsize##*$g}; 
  fsize=`echo -e $fsize | sed -e 's/[a-zA-Z\-]//g' -e 's/.*       //g' -e 's/[0-9]\+\ \+[0-9]\+\:[0-9]\+//g' -e 's/[\/\ \.]\+//g'`
}

totalnum=0;
for ext in "xml" "md5" "html" "txt" "cfg"; do
  list=`find $targetFolder -name "*\.$ext"`;
  num=0;
  for file in $list; do
    (( totalnum++ ));
    (( num++ ));
  done

  cnt=0;
  for file in $list; do
    (( cnt++ ));
    if [ `cat $file | grep -c "$sourceBuildID\|$sourceBranch"` > 0 ]; then # file contains the string, must sed it
      filename="${file##*\/}";
      tmpfile="$tmpdir/$filename";
      tmpfile2="$tmpdir/$filename"2;
      if [ "x$verbose" != "x" ]; then
        echo -e -n "[ren] [$blue$cnt$norm/$blue$num$norm] Replacing $yellow$filename$norm: ";
      fi

      # get old filesize
      getFileSize "$file" "www-data"; res1=$fsize;

      # make changes: 
      # replace "N2004...".zip with "2.0.2".zip
      # replace "Nightly Build: N2004..." with "Release Build: 2.0.2"
      # replace "Nightly" with "Release"
      # replace "N2004..." with "R2004..."
      # replace "2.0.3.1" with "2.0.4"
      
      # to escape "." as "\." use ${foo//./\\.} instead of $foo
      cat "$file" | sed \
        -e 's/'${sourceAlias//./\\.}'/'${targetAlias//./\\.}'/g' \
        -e 's/'${sourceBuildID//./\\.}\.zip'/'${targetAlias//./\\.}\.zip'/g' \
        -e 's/'$sourceType' Build\: '${sourceBuildID//./\\.}'/'$targetType' Build\: '${targetAlias//./\\.}'/g' \
        -e 's/'$sourceType'/'$targetType'/g' \
        -e 's/'${sourceBuildID//./\\.}'/'${targetBuildID//./\\.}'/g' \
        -e 's/'${sourceBranch//./\\.}'/'${targetBranch//./\\.}'/g' \
        > "$tmpfile";

      # replace <beforestring> with <afterstring>, eg., eclipse-SDK-I20050201-0800-linux-gtk.zip with eclipse-SDK-3.1M5-linux-gtk.tar.gz
      if [ "x$pairs" != "x" ]; then
          for pair in $pairs; do
              before=$pair; before=${before%%=*}; # trim from the = to the end 
              after=$pair;  after=${after##*=};  # trim up to the = 
              before=${before//\//\\\/}; before=${before//./\\.}; # escape "." and "/"
              after=${after//\//\\\/};   after=${after//./\\.};   # escape "." and "/"
              if [ "$before" != "$after" ]; then
                  cat "$tmpfile" | sed -e 's/'"$before"'/'"$after"'/g' > "$tmpfile2";
                  mv -f "$tmpfile2" "$tmpfile";
              fi
          done
      fi

      # get new filesize
      getFileSize "$tmpfile" "www-data"; res2=$fsize;

      # replace file
      if [ "x$verbose" != "x" ]; then
        echo -e "size: $res1 -> $res2";
      fi
      mv -f "$tmpfile" "$file";
    fi
  done
  if [ "x$verbose" != "x" ]; then
    echo "";
  fi
done

if [ "x$verbose" = "x" ]; then
  echo -e "[ren] $green$totalnum$norm files changed.";
  echo "";
fi

rm -fr $tmpdir;
echo "[ren] Finished `date +%Y%m%d\ %H\:%M\:%S`. Please verify that your build's index.html contains no invalid links.";  
echo "";
