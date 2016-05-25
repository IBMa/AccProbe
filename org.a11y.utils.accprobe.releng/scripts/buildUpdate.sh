# !/bin/sh
# $Id: buildUpdate.sh,v 1.5 2007/12/26 17:17:25 msquillac Exp $

# buildUpdate.sh script to generate Update Managers jars & promote them to download.eclipse
# Copyright \(c\) 2004-2006, IBM. Nick Boldt. codeslave\(at\)ca.ibm.com

# This script should build the required project plugins, features + site.xml 

# requires accompanying properties file, promoteToEclipse.*.properties or buildUpdate.properties, where * = emf or uml2
# can also specify any other properties file with -f flag

echo "[umj] buildUpdate.sh started on: `date +%Y%m%d\ %H\:%M\:%S`"

##########################################################################################

if [ $# -lt 1 ]; then
	echo " "
	echo "usage: buildUpdate.sh"
	echo "-f            <properties file used to run script (default ./promoteToEclipse.properties)>"
	echo "-sub          <REQUIRED: specify subproject as ocl, validation, query, transaction, etc.>"
	echo "-user         <username on *.eclipse.org (default is $USER)>"
	echo "-q, -Q        <scp, unzip, cvs checkout: -Q (quieter) where possible, -q elsewhere>"
	echo "-siteXMLOnly  <skip feature/plugin manipulation and just generate site*.xml>"
	echo "-debug        <debug this script and ProductUpdateBuilder script [0|1|2] (default 0)>"
	echo "-buildID      <perform UM site jars build on which build ID (eg., I200601191242)>"
	echo "-branch       <branch of the files to be built, eg., 1.0.0, 1.0.1 (overrides value in property file)>"
	echo "-promote      <promote built jars to download> (optional)"
	echo "-notrackstats <do not track stats in site*.xml> (optional)"
	echo "-nobuilder    <skip checking out o.e.releng.basebuilder> (optional)"
	echo "-skipjars     <skip uploading jars to download.eclipse (just the new XML)> (optional)"
	echo "-nocleanup    <don't delete temp files when done> (optional)"
	echo "-noCompareUMFolders <after uploading the drop, DO NOT compare source and target for matching MD5s, etc.>"
	echo "-basebuilderBranch  <org.eclipse.releng.basebuilder CVS branch>" #; default to value in build.options.txt if avail>"
	echo "-no4thPart    <build 3-part jars (2.0.1) instead of the default, 4-part jars (2.0.1.I200601191242)>"
	echo " "
	echo "Examples:"
	echo "Build (normal):     ./buildUpdate.sh -sub ocl -Q -buildID I200601191242 \\"
	echo "                       2>&1 | tee ~/buildUpdate_\`date +%Y%m%d_%H%M%S\`.txt"
	echo "Build then promote: ./buildUpdate.sh -sub query -Q -buildID I200601191242 -promote"
	echo "Build 3-part jars:  ./buildUpdate.sh -sub query -Q -buildID I200601191242 -no4thPart"
	echo " "
	exit 1
fi

# default to default properties file
defaultPropertiesFile=./promoteToEclipse.properties
propertiesFiles="";
trackstats=true;

# Create local variable based on the input
while [ "$#" -gt 0 ]; do
	case $1 in
		'-f')
			propertiesFile=$2;
			echo "   $1 $2";
			shift 1
			;;

		'-sub')
			subprojectName=$2;
			echo "   $1 $2";
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
		'-coordsite')
			echo "   $1 $2"; 
			coordsiteSuffix="-"$2; 
			shift 1
			;;
		'-user')
			echo "   $1 $2";
			user=$2;
			shift 1
			;;
		'-buildID')
			echo "   $1 $2";
			buildID=$2;
			shift 1
			;;
		'-branch')
			echo "   $1 $2";
			branch=$2;
			shift 1
			;;
		'-promote')
			echo "   $1";
			promote=1;
			build=1;
			shift 0
			;;
		'-nobuilder')
			echo "   $1";
			builder=0;
			shift 0
			;;
		'-notrackstats')
			echo "   $1";
			trackstats=false;
			shift 0
			;;
		'-nocleanup' | '-noclean')
			echo "   $1";
			cleanup=0;
			shift 0
			;;
		'-skipjars')
			echo "   $1";
			skipjars=1;
			shift 0
			;;
		'-q')
			echo "   $1";
			quietCVS=-q;
			quiet=-q;
			shift 0
			;;
		'-Q')
			echo "   $1";
			quietCVS=-Q;
			quiet=-q;
			shift 0
			;;
		'-debug')
			echo "   $1 $2";
			debug=$2;
			shift 1
			;;
		'-siteXMLOnly')
			echo "   $1";
			siteXMLOnly="true";
			shift 0
			;;
		'-basebuilderBranch')
			echo "   $1 $2";
			basebuilderBranch=$2;
			shift 1
			;;
		'-noCompareUMFolders')
			echo "   $1";
			noCompareUMFolders=1;
			shift 0
			;;
		'-no4thPart')
			echo "   $1";
			no4thPart=1;
			shift 0
			;;
	esac
	shift 1
done

if [ "$subprojectName" = "" ]; then # no value set!
  echo "[promote] No subproject name set in properties file or by -sub flag. Script cannot continue. Exiting...";
  exit 99;
fi

if [ "$branch" = "" ]; then # no value set!
  echo "[promote] No branch value set in properties file or by -branch flag. Script cannot continue. Exiting...";
  exit 99;
fi

if [ "$buildID" = "" ]; then # no value set!
  echo "[promote] No build ID value set in properties file or by -buildID flag. Script cannot continue. Exiting...";
  exit 99;
fi

##########################################################################################

#users (for ssh and cvs connections)
buildServerCVSUser=$user"@"$buildServerFullName

#path to update site on build server
localUpdatesWebDir=$localWebDir/updates

# path to update site on download
updatesDir=$projectWWWDir/updates

CVSRep=":ext:"$user"@"$eclipseServerFullName":/cvsroot/modeling"
wwwCVSRep=":ext:"$user"@"$eclipseServerFullName":/cvsroot/org.eclipse"
wwwRemote=$user"@"$downloadServerFullName

# temp folder
tmpfolder=$BUILD_HOME/tmp-buildUpdate.sh-$subprojectName-$user-`date +%Y%m%d_%H%M%S`

# working directory for CVS checkouts & building
buildDir=$tmpfolder/1 ;

bootclasspath="$javaHome/jre/lib/*.jar:$buildDir/org.eclipse.releng.generators/buildTools.jar:$buildDir/org.eclipse.releng.generators/productUpdateBuilder.jar"

##########################################################################################

getBuildIDactual ()
{
	buildIDactual=`find $buildDropsDir/$branch/$buildID -name "$SDKfilenamepattern"`
	buildIDactual=${buildIDactual##*SDK-}; # trim up to SDK-
	buildIDactual=${buildIDactual##*incubation-}; # trim off "incubation-"
	buildIDactual=${buildIDactual%%\.zip}; # trim off .zip
	#echo $buildIDactual
}

##########################################################################################

buildIDactual=buildID;
getBuildIDactual;
buildType=${buildID:0:1};

if [[ ${SDKfilenamepattern##*incubation*} = "" ]]; then 
	isIncubation="true"; 
else
	isIncubation="false"; 
fi

##########################################################################################

mkdir -p $buildDir;

if [ $promote -eq 1 ]; then
	mkdir -p $buildDir/../2;
fi
cd $buildDir ;

if [ $builder -eq 1 ]; then
	if [ "x$basebuilderBranch" = "x" ]; then
		if [[ ! $basebuilderBranch ]]; then
			basebuilderBranch=$(cat $buildDropsDir/$branch/$buildID/build.cfg | grep basebuilderBranch); basebuilderBranch=${basebuilderBranch#*basebuilderBranch=}; # echo $basebuilderBranch
			if [[ ! $basebuilderBranch ]]; then
				basebuilderBranch="HEAD";
			fi
		fi
	fi
	echo "[umj-co] [1] Checking out org.eclipse.releng.basebuilder from dev using $basebuilderBranch"
	cd $buildDir; cvs -d :pserver:anonymous@dev.eclipse.org:/cvsroot/eclipse $quietCVS co -P -r $basebuilderBranch org.eclipse.releng.basebuilder;
else
	echo "[umj-co] [1] Checking out org.eclipse.releng.basebuilder from dev... omitted."
fi

# org.eclipse.releng.basebuilder directory
relengBaseBuilderDir=$buildDir/org.eclipse.releng.basebuilder
echo "[umj] relengBaseBuilderDir: $relengBaseBuilderDir"

#unpack files from cvs to get buildUpdateJars.xml, productUpdateBuilder.jar, buildTools.jar

echo "[umj-co] [2] Checking out $relengGeneratorsCVSPath"
cd $buildDir; cvs -d $CVSRep $quietCVS co -P -d org.eclipse.releng.generators $relengGeneratorsCVSPath; # on local

# one update site per project
updatesCVSPath=www/modeling/$projectName/updates
echo "[umj-co] [3] Checking out $updatesCVSPath/site/* from $wwwCVSRep"
cd $buildDir/../1;
cvs -d $wwwCVSRep $quietCVS co -P -d site $updatesCVSPath/site.xml;
cvs -d $wwwCVSRep $quietCVS co -P -d site $updatesCVSPath/site-interim.xml;
if [[ $coordsiteSuffix ]]; then
	cvs -d $wwwCVSRep $quietCVS co -P -d site $updatesCVSPath/site${coordsiteSuffix}.xml;
	cvs -d $wwwCVSRep $quietCVS co -P -d site $updatesCVSPath/site-interim${coordsiteSuffix}.xml;
	if [[ ! -f $updatesCVSPath/site${coordsiteSuffix}.xml ]]; then
		echo "[umj-co] Creating new site${coordsiteSuffix}.xml";
		pushd site; cp site.xml site${coordsiteSuffix}.xml; cvs add site${coordsiteSuffix}.xml; popd;
	fi
	if [[ ! -f $updatesCVSPath/site-interim${coordsiteSuffix}.xml ]]; then
		echo "[umj-co] Creating new site-interim${coordsiteSuffix}.xml";
		pushd site; cp site-interim.xml site-interim${coordsiteSuffix}.xml; cvs add site-interim${coordsiteSuffix}.xml; popd;
	fi
fi

cd $buildDir;
#run ant script

echo "[umj] [4] Invoking Eclipse build to create UM jars for build ID $buildID..."
if [ "$buildIDactual" != "$buildID" ]; then
	buildDesc=$buildIDactual; # eg., 2.0.1RC1 != M200409081700
else
	buildDesc=$branch; # eg., 2.0.1
fi

# new, for use with plugins as jars: unpack SDK zips and then replace foo.jar with foo/ folders instead

rm -fr $buildDir/org.eclipse.releng.generators/updateJars
mkdir -p $buildDir/org.eclipse.releng.generators/updateJars/site

index=0;
element_count=${#filePrefixesToUnzipArray[@]}
while [ "$index"  -lt "$element_count" ]; do
	zipfilename=${filePrefixesToUnzipArray[$index]}"$buildIDactual.zip"
	unzip -uo -qq $buildDropsDir/$branch/$buildID/$zipfilename -d $buildDir/org.eclipse.releng.generators/updateJars
	let "index = $index + 1";
done

# can we skip this if -siteXMLOnly ?
if [[ ! $siteXMLOnly ]]; then 
	jarlist=`find $buildDir/org.eclipse.releng.generators/updateJars/eclipse/plugins -maxdepth 1 -name "*.jar"`
	for jarfile in $jarlist; do
		folder=${jarfile%\.jar}
		echo "Unpacking "${jarfile#*plugins/}" ...";
		#echo "Unpacking $jarfile into $folder ...";
		unzip -uo -qq $jarfile -d $folder;
		#echo "Removing $jarfile ...";
		rm -fr $jarfile;
	done
fi

# java home & vm used to run the build.  Defaults to java on system path
## must be Sun 1.4 - IBM 1.4 crashes server and Sun 5.0 throws NPE (SAXParser problem)
javaHome=/opt/sun-java2-1.4
vm=$javaHome/bin/java

# different ways to get the launcher and Main class
if [[ -f $relengBaseBuilderDir/startup.jar ]]; then 
  cpAndMain="$relengBaseBuilderDir/startup.jar org.eclipse.core.launcher.Main"; # up to M4_33
elif [[ -f $relengBaseBuilderDir/plugins/org.eclipse.equinox.launcher.jar ]]; then
  cpAndMain="$relengBaseBuilderDir/plugins/org.eclipse.equinox.launcher.jar org.eclipse.equinox.launcher.Main"; # M5_33
else
  cpAndMain=`find $relengBaseBuilderDir/ -name "org.eclipse.equinox.launcher_*.jar" | sort | head -1`" org.eclipse.equinox.launcher.Main"; 
fi

command="$vm -cp $cpAndMain"
command=$command" -application org.eclipse.ant.core.antRunner"
command=$command" -f $buildDir/$antScript $target"
command=$command" -Dbootclasspath=$bootclasspath"
command=$command" -Dtimestamp=$buildID"
command=$command" -DbuildDesc=$buildDesc"
command=$command" -DprojectName=$projectName"
command=$command" -DsubprojectName=$subprojectName"
command=$command" -Dincubation=$isIncubation"
command=$command" -Ddebug=$debug"
command=$command" -Dtrackstats=$trackstats"

if [[ $buildType = "R" ]] || [[ $no4thPart -eq 1 ]] || [[ "$buildIDactual" = "$branch" ]]; then 
	sitexml="site.xml"; 
	command=$command" -DfourthPart=no4thPart"
	command=$command" -Dsitexml=$sitexml"
	no4thPart=1;
else
	sitexml="site-interim.xml";
	command=$command" -DfourthPart=add4thPart"
	command=$command" -Dsitexml=$sitexml"
fi

if [[ $siteXMLOnly ]]; then command=$command" -DsiteXMLOnly=$siteXMLOnly"; fi

cp -f $buildDir/site/$sitexml $buildDir/org.eclipse.releng.generators/updateJars/site/

echo "$command" | perl -pe "s/ -/\n  -/g" ; # pretty printing
$command;

# create a second version of $sitexml where tracking URLs have been removed
# [196087] support creating both a coordsite (no stat tracking URLs in site.xml) and a non-coordsite (with download stat tracking)
# TODO when ganymede is available, revisit this
if [[ $coordsiteSuffix ]]; then
	coordsitexml=${sitexml%\.xml}${coordsiteSuffix}".xml"; # eg., site-interim-europa.xml
	cat $buildDir/site/$sitexml | perl -pe "s#http://www.eclipse.org/downloads/download.php\?r=1\&amp;file=/modeling/$projectName/updates/features/#features/#g" > $buildDir/site/$coordsitexml
	#diff $buildDir/site/$sitexml $buildDir/site/$coordsitexml;
fi

# if we didn't generate the jars/features, we have to manually copy them over
if [[ $siteXMLOnly ]]; then 
	echo "[umj] Copy jarred plugins ...";
	mkdir -p $buildDir/org.eclipse.releng.generators/updateJars/site/plugins/
	cp $buildDir/org.eclipse.releng.generators/updateJars/eclipse/plugins/*.jar $buildDir/org.eclipse.releng.generators/updateJars/site/plugins/

	echo "[umj] Copy and jar features ...";
	mkdir -p $buildDir/org.eclipse.releng.generators/updateJars/site/features/
	for featdir in `find $buildDir/org.eclipse.releng.generators/updateJars/eclipse/features/ -mindepth 1 -maxdepth 1 -type d -and -not -name "."`; do
	  #echo -n "        Zip $featdir as ${featdir}.jar ...";
	  cd $featdir; 
	  zip -r9q ${featdir}.jar *;
	  mv ${featdir}.jar $buildDir/org.eclipse.releng.generators/updateJars/site/features/ 
	  cd -;
	  #echo " done.";
	done
fi

# generate MD5s
md5file="./md5s/"$subprojectName"_"$buildIDactual".md5";
md5filepath=$buildDir"/../"$md5file
mkdir -p $buildDir/../md5s;
cd $buildDir/org.eclipse.releng.generators/updateJars/site;
md5sum features/*.jar plugins/*.jar > $md5filepath; # list md5s for all new jars

echo "[umj] [5a] Copy new jars & site/* to $localUpdatesWebDir ..."
# copy new jars & site.xml to /var/www/modeling/$projectName/updates
cd $buildDir/org.eclipse.releng.generators/updateJars/site && cp -r . $localUpdatesWebDir && cd $buildDir/site && cp -r . $localUpdatesWebDir;

# copy md5 file into both places, too: first to local build/cvs server
mkdir -p $localUpdatesWebDir/md5s && cp $md5filepath $localUpdatesWebDir/md5s;

# removed 20070608
#echo "[umj] [5b] Fix permissions in $localUpdatesWebDir ..."
#ssh $buildServerCVSUser " \
#	cd $localUpdatesWebDir ; \
#	chgrp -fR $buildUserGroup *; \
#	find $localUpdatesWebDir -type f -exec chmod -f $eclipsePermsFile {} \; ; \
#	find $localUpdatesWebDir -type f -exec chmod -f $eclipsePermsFile {} \; ; \
#";

#promote to download
if [ $promote -eq 1 ]; then
	echo "[umj] [6] Update site/* to dev.eclipse.org ..." ;
	cd $buildDir/site ;
	cvs -d $wwwCVSRep $quietCVS ci -m "buildUpdate: $subprojectName $branch $buildID" ;

	if [ $skipjars -eq 0 ]; then
		echo "[umj] [7a] Promoting jars to $downloadServerFullName..." ;
		cd $buildDir/org.eclipse.releng.generators/updateJars/site ;
		scp -r -v $buildDir/org.eclipse.releng.generators/updateJars/site/. $wwwRemote:$updatesDir ;
	else
		echo "[umj] [7a] Promoting jars to $downloadServerFullName... omitted." ;
	fi

	echo "[umj] [7b] Promoting site/* to $downloadServerFullName..." ;
	cd $buildDir/site ;
	scp -r $quiet $buildDir/site/. $wwwRemote:$updatesDir ;

	# copy md5 file into both places, too: second onto production server
	ssh $wwwRemote "mkdir -p $updatesDir/md5s/" ;
	scp $quiet $md5filepath $wwwRemote:$updatesDir/md5s/ ;

	# removed 20070608
	#echo "[umj] [7c] Fix permissions in $updatesDir ..." ;
	#ssh $wwwRemote "
	#	find $updatesDir         -exec chgrp -f $eclipseUserGroup {} \; ;
	#	find $updatesDir -type d -exec chmod -f $eclipsePermsDir  {} \; ;
	#	find $updatesDir -type f -exec chmod -f $eclipsePermsFile {} \; ;
	#";

	# validate MD5s
	if [ $noCompareUMFolders -eq 0 ]; then
		### CHECK MD5s and compare dir filesizes for match (du -s)
		echo "[umj] [7d] [`date +%H:%M:%S`] Comparing local and remote folders MD5 sums to ensure SCP completeness... "
		cmd="$buildScriptsDir/compareFolders.sh -md5only -md5file $md5file -user $user -local $localUpdatesWebDir -remote $updatesDir -server $wwwRemote"
		if [ $debug -gt 0 ]; then
			echo "$cmd" | perl -pe "s/ -/\n  -/g";
		fi
		$cmd;
		returnCode=$?
		if [ $returnCode -gt 0 ]; then
			echo "[umj] [`date +%H:%M:%S`] ERROR! Script exiting with code $returnCode from compareFolders.sh"
			exit $returnCode;
		fi
	else
		echo "[umj] [7d] [`date +%H:%M:%S`] Comparing local and remote folders to ensure SCP completeness ... omitted."
	fi

else
	echo "[umj] [6] Check in new site/* to CVS... omitted."
	echo "[umj] [7] Promoting jars & site/* to $downloadServerFullName... omitted."
fi

# cleanup
if [ $cleanup -eq 1 ]; then
	rm -fr $tmpfolder/;
fi

########### DONE ###########

echo "[umj] buildUpdate.sh completed on: `date +%Y%m%d\ %H\:%M\:%S`"

