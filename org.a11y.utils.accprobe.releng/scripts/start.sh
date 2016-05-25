#!/bin/bash



export PATH=/bin:/usr/bin/:/usr/local/bin:$PATH

export CVS_RSH=/usr/bin/ssh



# TODO conditionally invoke cygpath based on os

# *** if using cygwin on Windows ***

# insure build directory is near root drive else paths will exceed 255-character maximum

# TODO only runtime component is built for now; see individual project buildAll.xml files

# TODO build of RCP products requires entire RCP delta pack in baseLocation (eg eclipse-RCP-SDK-3.3.1.1-win32.zip)

# *** do not let delta pack write over config.ini ***



#default values

writableBuildRoot=""; # required

version=""; # REQUIRED

eclipseBase=""; # REQUIRED



# move to a .properties file

# dependencies include:

# eclipse-sdk: http://archive.eclipse.org/eclipse/downloads/drops/R-3.3.1-200709211145/download.php?dropFile=eclipse-SDK-3.3.1-win32.zip

# RCP windows binary: http://archive.eclipse.org/eclipse/downloads/drops/R-3.3.1-200709211145/download.php?dropFile=eclipse-RCP-3.3.1-win32.zip

# RCP delta pack: http://archive.eclipse.org/eclipse/downloads/drops/R-3.3.1-200709211145/download.php?dropFile=eclipse-RCP-3.3.1-delta-pack.zip

# AJDT: http://www.eclipse.org/downloads/download.php?file=/tools/ajdt/33/update/ajdt_1.5.1_for_eclipse_3.3.zip

# eclipseURL="http://archive.eclipse.org/eclipse/downloads/drops/S-3.3M3-200611021715/eclipse-SDK-3.3M3-win32.zip";

eclipseURL="http://archive.eclipse.org/eclipse/downloads/drops/R-3.3.2-200802211800/eclipse-SDK-3.3.2-win32.zip";

dependURL="$eclipseURL";

orbitBuildId="I20080228221323";



branch=HEAD

projRelengBranch=HEAD; # default set below

a11yRelengBranch=HEAD;

basebuilderBranch=HEAD;

buildAlias=""

buildType=N

buildTimestamp=`date +%Y%m%d%H%M`""; # default set below

archivePrefix="eclipse"; # changed to productName if building RCP app

validationFeatureId=""; # changed if building validation componentry



downloadsDir=""; # default set below

clean="none"; # clean up files when done

quietCVS=-Q; # QUIET!

depsFile=""; # dependencies file 



function usage()

{

    echo "usage: start.sh"

    echo "-sub            <REQUIRED: shortname of the project to be build, eg. common, validation, javaco, webelo>"

    echo "-product <optional: shortname of the exemplary tool/product to be build, eg. accprobe>; subproject defaults to 'examples'"

    echo "-eclipseBase <REQUIRED: base of eclipse installation against which to build eg., \$eclipseBase/eclipse>"

    echo "-version        <REQUIRED: version to use, eg., 1.0.0>"

    echo "-orbitBuildId <Optional: buildId of orbit build for 3rd-party bundles; default is $orbitBuildId>"

    echo "-URL            <The URLs of the Eclipse driver and any other zips that need to be unpacked into"

    echo "                 the eclipse install to resolve all dependencies. Enter one -URL [...] per required URL.>"     

    echo "-branch         <REQUIRED: CVS branch of the files to be built (eg., build_200409171617); default HEAD)>"

    echo "-projRelengBranch   <CVS branch of org.a11y.\$subprojectName.releng>"

    echo "-basebuilderBranch <CVS branch of org.eclipse.releng.basebuilder>"

    echo "-buildAlias     <The Alias of the build (for named S and R builds), eg. 2.0.2RC1; default: none>"

    echo "-buildType      <The type of the build: N, I, M, S, R; default: N>"

    echo "-javaHome       <The JAVA_HOME directory; default: $JAVA_HOME or value in releng-common/server-config/*.properties>"

    echo "-downloadsDir   <The directory where dependent zips are downloaded; default: \$writableBuildRoot/downloads>"

    echo "-buildTimestamp <optional: YYYYmmddhhMM timestamp to be used to label the build; default will be generated>"

    echo "-writableBuildRoot <Required: dir where builds will occur, eg., /home/www-data>"

    echo "-buildDir       <The directory of this build; default: \$writableBuildRoot/\$subProjectName/\$version/\$buildType\$buildTimestamp>"

    echo "-clean        <optional: can be none, some, all, where 'all' will remove downloads in addition to temp files and CVS exports>"

    echo "-addSDK         <optional: if used, add the resulting SDK zip to the specified dependencies file for use with other builds>"

    echo ""

    echo "example: "

    echo "./start.sh \\"

    echo "  -sub common -version 1.0.0 -basebuilderBranch M3_33 -clean some \\"

    echo "  -URL http://download.eclipse.org/eclipse/downloads/drops/S-3.3M3-200611021715/eclipse-SDK-3.3M3-linux-gtk.tar.gz \\"

    exit 1

}



if [[ $# -eq 0 ]]; then

    usage;

fi



echo "[`date +%Y%m%d\ %H\:%M\:%S`] start.sh executing with the following options:"

tmpfile=`mktemp`;

echo "#Build options (all but -URL)" >> $tmpfile;



while [ "$#" -gt 0 ]; do

    case $1 in

        '-writableBuildRoot') writableBuildRoot=$2; echo "   $1 $2"; echo "${1:1}=$2" >> $tmpfile; shift 1;;

        '-sub')

           	subprojectName=$2;

            echo "subprojectName=${2}" >> $tmpfile;

            echo "   $1 $2";

            shift 1

        ;;

        '-product')

            subprojectName="utils"; productName=$2; archivePrefix=$2;

            echo "   $1 $2 (subprojectName='utils')";

            echo "productName=${2}" >> $tmpfile;

            shift 1

        ;;

        '-version') version=$2; echo "   $1 $2"; echo "${1:1}=$2" >> $tmpfile; shift 1;;

        '-orbitBuildId') orbitBuildId=$2; echo "   $1 $2"; echo "${1:1}=$2" >> $tmpfile; shift 1;;

        '-eclipseBase') eclipseBase=$2; echo "   $1 $2"; echo "${1:1}=$2" >> $tmpfile; shift 1;;

        '-branch') branch=$2; echo "   $1 $2"; echo "${1:1}=$2" >> $tmpfile; shift 1;;

        '-URL')

            if [ "x$dependURL" != "x" ]; then

              dependURL="$dependURL "

            fi

            dependURL=$dependURL"$2"; echo "   $1 $2"; shift 1;;

        '-javaHome') javaHome=$2; echo "   $1 $2"; shift 1;;

        '-buildAlias') buildAlias=$2; echo "   $1 $2"; echo "${1:1}=$2" >> $tmpfile; shift 1;;

        '-buildType') buildType=$2; echo "   $1 $2"; echo "${1:1}=$2" >> $tmpfile; shift 1;;

        '-buildDir') buildDir=$2; echo "   $1 $2"; echo "${1:1}=$2" >> $tmpfile; shift 1;;

        '-downloadsDir') downloadsDir=$2; echo "   $1 $2"; echo "${1:1}=$2" >> $tmpfile; shift 1;;

        '-buildTimestamp') buildTimestamp=$2; echo "   $1 $2"; echo "${1:1}=$2" >> $tmpfile; shift 1;;

        '-basebuilderBranch') basebuilderBranch=$2; echo "   $1 $2"; echo "${1:1}=$2" >> $tmpfile; shift 1;;

        '-projRelengBranch') projRelengBranch=$2; echo "   $1 $2"; echo "${1:1}=$2" >> $tmpfile; shift 1;;

        '-branch') branch=$2; echo "   $1 $2"; echo "${1:1}=$2" >> $tmpfile; shift 1;;

        '-clean') clean=$2; echo "   $1 $2"; echo "${1:1}=$2" >> $tmpfile; shift 0;;

        '-addSDK') depsFile="$2"; echo "   $1 $2"; shift 0;;

    esac

    shift 1

done    

# postpone writing of subprojectName and archivePrefix until we know if we're building a product

echo "subprojectName=${subprojectName}" >> $tmpfile;

echo "archivePrefix=$archivePrefix" >> $tmpfile;



# collect values from input / set defaults from input values

if [ "x$version" = "x" ]; then

    echo "";

    echo "*** version is required ***";

    echo "";

    usage;

elif [ "x$writableBuildRoot" = "x" ]; then

    echo "";

    echo "*** writableBuildRoot is required ***";

    echo "";

    usage;

elif [ "x$eclipseBase" = "x" ]; then

    echo "";

    echo "*** eclipseBase is required ***";

    echo "";

    usage;

fi



if [ "x$downloadsDir" = "x" ]; then

    downloadsDir=$writableBuildRoot/downloads

    echo "[start] downloads=$downloadsDir";

fi



if [ "x$buildDir" = "x" ]; then

    if [ "x$productName" = "x" ]; then

        buildDir=$writableBuildRoot/a11y/$subprojectName/$version/$buildType$buildTimestamp

    else

        buildDir=$writableBuildRoot/a11y/utils/$productName/$version/$buildType$buildTimestamp

    fi

   else

    buildDir=$writableBuildRoot/$buildDir;

fi



if [[ "$branch" != "HEAD" ]] && [[ !$projRelengBranch ]]; then

    echo "  **** Defaulting -projRelengBranch to $branch. If that's not good, override using a -debug build. ****"

    projRelengBranch="$branch";

fi



echo "[start] Creating build directory $buildDir"

mkdir -p $buildDir/$archivePrefix;

cd $buildDir;



hostname=`hostname`;

configfile=$writableBuildRoot/config/$hostname.properties; # eg., utils.properties, emf.torolab.ibm.com.properties



echo "[start] Export org.a11y.utils.accprobe.releng using "$a11yRelengBranch;

if [[ ! -d $buildDir/org.a11y.utils.accprobe.releng ]]; then 

  cmd="cvs -d :pserver:teegala:ribdev01@ibmac50.austin.ibm.com:/rib $quietCVS ex -r $a11yRelengBranch -d org.a11y.utils.accprobe.releng A11yAccProbe/org.a11y.utils.accprobe.releng";

  echo "  "$cmd; $cmd; 

  chmod 754 org.a11y.utils.accprobe.releng/scripts/*.sh;

  

  if [ "${OS##*Windows}" != "$OS" ]; then

      # remove CR (\r) from exported script files when on windows

      scriptsDir=$buildDir/org.a11y.utils.accprobe.releng/scripts;

      tr "\r\n" "\n" < $scriptsDir/removeCR.sh > $scriptsDir/removeCR.sh.tmp;

      $scriptsDir/removeCR.sh.tmp $scriptsDir;

  fi

  echo "[start] Export done."

else

  echo "[start] Export skipped (dir already exists)."

fi

echo ""



relengCommonBuilderDir=$buildDir/org.a11y.utils.accprobe.releng;

scriptsDir=$relengCommonBuilderDir/scripts;



# set environment variables

export BUILD_HOME=$writableBuildRoot

if [ "x$javaHome" != "x" ]; then

    export JAVA_HOME=$javaHome;

else # use default

    export JAVA_HOME=$($scriptsDir/readProperty.sh $configfile JAVA_HOME)

    javaHome="$JAVA_HOME"

fi

export ANT_HOME=$($scriptsDir/readProperty.sh $configfile ANT_HOME);

export ANT=$ANT_HOME"/bin/ant";



echo "Environment variables: ";

echo "  BUILD_HOME      = $BUILD_HOME";

echo "  JAVA_HOME = $JAVA_HOME";

echo "  ANT_HOME  = $ANT_HOME";

echo "  ANT       = $ANT";

echo "";



echo "" >> $tmpfile;

echo "# Environment variables"  >> $tmpfile;

echo "BUILD_HOME=$BUILD_HOME" >> $tmpfile;

echo "JAVA_HOME=$JAVA_HOME" >> $tmpfile;

echo "ANT_HOME=$ANT_HOME" >> $tmpfile;

echo "ANT=$ANT" >> $tmpfile;



echo "[start] Check if dependent drivers exist or can be downloaded:"



checkZipExists ()

{

    theURL=$1;

    theFile=`echo $theURL | sed -e 's/^.*\///'`

    winFile=`cygpath --windows $theFile`;

    winDownloadsDir=`cygpath --windows $downloadsDir`;

        mkdir -p $downloadsDir;

    $ANT -f $scriptsDir/checkZipExists.xml -DdownloadsDir=$winDownloadsDir -DtheFile=$winFile -DtheURL=$theURL

}



for dep in $dependURL; do

    outfile=`mktemp`;

    checkZipExists $dep 2>&1 | tee $outfile;

    result=`cat $outfile | grep -c FAILED`

    rm -fr $outfile

    if [ "$result" != "0" ]; then

        echo "[start] An error occurred finding or downloading $dep."

        echo "[start] This script will now exit."

        exit 99;

    fi

done



# add some properties to build.cfg

buildcfg="$buildDir/build.cfg";

echo "Storing build properties in $buildcfg";

echo -n "" > $buildcfg; # truncate file if exists; create if not



cat $tmpfile >> $buildcfg;

echo "" >> $buildcfg;

rm -fr $tmpfile



echo "#Build options (more)" >> $buildcfg;

repoInfoFile=$buildDir/org.a11y.utils.accprobe.releng/repoInfo.properties;

echo "repoInfoFile=$repoInfoFile" >> $buildcfg;

echo "" >> $buildcfg;



echo "[start] Export org.a11y.$subprojectName.$productName.releng using "$projRelengBranch;

if [[ ! -d $buildDir/org.a11y.$subprojectName.$productName.releng ]]; then 

  cmd="cvs -d :pserver:teegala@ibmac50.austin.ibm.com:/rib $quietCVS ex -r $projRelengBranch -d org.a11y.$subprojectName.$productName.releng A11yAccprobe/org.a11y.$subprojectName.$productName.releng";

  echo "  "$cmd; $cmd; 

  echo "[start] Export done."

else

  echo "[start] Export skipped (dir already exists)."

fi

echo ""



if [[ $basebuilderBranch ]]; then

  echo "[start] Export org.eclipse.releng.basebuilder using "$basebuilderBranch;

else

  echo "[start] Export org.eclipse.releng.basebuilder using HEAD";

fi

if [[ ! -d $buildDir/org.eclipse.releng.basebuilder ]]; then 

  cmd="cvs -d :pserver:anonymous@dev.eclipse.org:/cvsroot/eclipse $quietCVS ex -r $basebuilderBranch org.eclipse.releng.basebuilder"

  echo "  "$cmd; $cmd; 

  echo "[start] Export done."

else

  echo "[start] Export skipped (dir already exists)."

fi

echo ""



##### BEGIN RUN #####



# Default value for the build timestamp

buildID=$buildTimestamp



# org.eclipse.*releng* directories

relengBuilderDir=$buildDir/org.a11y.$subprojectName.$productName.releng

relengBaseBuilderDir=$buildDir/org.eclipse.releng.basebuilder



echo "relengBuilderDir: $relengBuilderDir"

echo "relengCommonBuilderDir: $relengCommonBuilderDir"

echo "relengBaseBuilderDir: $relengBaseBuilderDir"



contextQualifier=$buildType$buildTimestamp;

buildTag=$branch;

buildDirArchive="$buildDir/$archivePrefix"



echo "buildID: $buildID"

echo "buildTag: $buildTag"

echo "buildDirArchive: $buildDirArchive"



$scriptsDir/executeCommand.sh "mkdir -p $buildDirArchive $downloadsDir"



##### BEGIN BUILD #####



# different ways to get the launcher and Main class

if [[ -f $relengBaseBuilderDir/startup.jar ]]; then 

  cpAndMain=`cygpath --windows $relengBaseBuilderDir/startup.jar`" org.eclipse.core.launcher.Main"; # up to M4_33

elif [[ -f $relengBaseBuilderDir/plugins/org.eclipse.equinox.launcher.jar ]]; then

  cpAndMain=`cygpath --windows $relengBaseBuilderDir/plugins/org.eclipse.equinox.launcher.jar`" org.eclipse.equinox.launcher.Main"; # M5_33

else

  cpAndMain=`find $relengBaseBuilderDir/ -name "org.eclipse.equinox.launcher_*.jar" | sort | head -1`" org.eclipse.equinox.launcher.Main"; 

fi



echo "Invoking Eclipse build with -enableassertions and -cp $cpAndMain ...";



command="$javaHome/bin/java -enableassertions -cp $cpAndMain"

command=$command" -application org.eclipse.ant.core.antRunner"

command=$command" -f `cygpath --windows $relengBuilderDir/buildAll.xml` run"

command=$command" -DmapVersionTag=$buildTag"

command=$command" -DbuildType=$buildType"

command=$command" -DbuildID=$buildID"

command=$command" -DbuildLabel=$buildType$buildID"

command=$command" -DbuildVersion=$version"

command=$command" -DforceContextQualifier=$contextQualifier"

command=$command" -DbaseLocation=$eclipseBase/eclipse"

command=$command" -DorbitBuildId=$orbitBuildId"

command=$command" -Dtimestamp=$buildTimestamp"

command=$command" -DarchivePrefix=$archivePrefix"

command=$command" -DcollectingFolder=$archivePrefix"

command=$command" -DbuildDirectory=`cygpath --windows $buildDirArchive`"

if [[ $buildAlias ]]; then

    command=$command" -DbuildAlias=$buildAlias";

fi

if [[ $validationFeatureId ]]; then

    command=$command" -DvalidationFeatureId=$validationFeatureId";

fi

if [[ $productName ]]; then

    command=$command" -DproductName=$productName"

    command=$command" -Dproduct="`cygpath --windows $buildDirArchive/plugins/org.a11y.$subprojectName.$productName/$productName.product`

    command=$command" -DrunPackager=true"

fi

command=$command" -DdownloadsDir=$downloadsDir"

$scriptsDir/executeCommand.sh "$command"



##### END BUILD #####



# generate a log of any compiler problems, warnings, errors, or failures

echo -n "Generating compilelogs summary... ";

if [ -d $buildDir/compilelogs ]; then

  summary=$($scriptsDir/getCompilerResults.sh $buildDir/compilelogs);

  echo $summary > $buildDir/compilelogs/summary.txt

  if [ "x$summary" != "x" ]; then 

    echo $summmary": ";

  fi

  echo "done.";

else

  echo "skipped.";

fi 



# add build to dependencies file?

if [[ $depsFile != "" ]]; then

    if [[ -f $depsFile ]]; then

        depNum=$(cat $depsFile | grep "$subprojectName=" | tail -1); depNum=${depNum%%$subprojectName=*};

        SDKURL=$(find $buildDir -maxdepth 1 -name a11y"-$subprojectName-*SDK*.zip" | tail -1);

        if [[ $SDKURL != "" ]]; then 

            SDKURL=${SDKURL##$buildDir}; SDKURL=${SDKURL##*/}; 

            SDKURL=http://$hostname/modeling/$projectName/$subprojectName/downloads/drops/$version/$buildType$buildTimestamp/$SDKURL; 

            if [[ -w $depsFile ]]; then 

                echo "$depNum$subprojectName=$SDKURL" >> $depsFile;

                echo "[promote] $SDKURL ($depNum$subprojectName) appended to $depsFile.";

            else

                echo "[promote] *** WARNING: File $depsFile is not writable. Add this manually:";

                echo "$depNum$subprojectName=$SDKURL"

                echo "[promote] ***";

            fi

        else

            echo "[start] *** WARNING: no SDK zip found in $buildDir. ***"; 

        fi

    else

        echo "[start] *** WARNING: cannot store SDK. File $depsFile does not exist. ***";

    fi

fi



##### END RUN #####



cd $buildDir;



if [ $clean = "all" ]; then

    echo "[start] Cleaning up & removing temporary directories in $buildDir and all files in $writableBuildRoot"

    rm -fr $buildDir/org.a11y.utils.accprobe.releng

    rm -fr $buildDir/org.eclipse.releng.basebuilder

    rm -fr $writableBuildRoot/downloads

fi



if [ $clean = "some" ]; then

    echo "[start] Cleaning up & removing temporary directories in $buildDir"

    rm -fr $buildDir/org.a11y.$subprojectName.$productName.releng

    rm -fr $buildDir/$archivePrefix

    rm -fr $buildDir/testing

elif [ clean = "none" ]; then

    echo "[start] Please scrub the following folders manually:"

    echo "[start]   $buildDir/org.a11y.$subprojectName.$productName.releng"

    echo "[start]   $buildDir/org.eclipse.releng.basebuilder"

    echo "[start]   $buildDir/$archivePrefix"

    echo "[start]   $buildDir/testing"

    echo ""

    echo "[start] You may also want to scrub the following folders:"

    echo "[start] $writableBuildRoot/downloads"

    echo "[start] $writableBuildRoot/config"

fi



echo "[start] start.sh finished on: `date +%Y%m%d\ %H\:%M\:%S`"

echo ""

