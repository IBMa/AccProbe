#!/bin/sh

# compareFolders.sh script to validate that an scp upload completed correctly (zips or jars)
# run script on emf.torolab to push data to remote eclipse.org server
# Copyright \(c\) 2004-2006, IBM. Nick Boldt. codeslave\(at\)ca.ibm.com
# called from promoteToEclipse.sh

# $Id: compareFolders.sh,v 1.4 2007/12/26 17:17:25 msquillac Exp $

#debug?
debug=0 # 0, default, no; 1, yes

# defaults
local=
remote=
server=

#local user
user=$USER

# do only the MD5 checks, skip the du checks?
md5only=0; # default, do all three checks (3 x du, 1 x md5sum -c)

# file to use for MD5 checking - must be on both servers; if omitted, search for all files available instead
md5file="";

instructions ()
{
	echo " "
	echo "usage: compareFolders.sh"
	echo "-user            <username on local (default is $USER (\$USER))>"
	echo "-md5only         <do md5 check ONLY, not dir contents compare> (optional)"
	echo "-md5file         <path to file relative to -local and -remote dirs containing MD5s to compare";
	echo "                   if omitted, use all available *.md5 files in -local and -remote dirs (much slower)>"
	echo "-local,  -remote <path to folder to compare on local/remote servers>"
	echo "-server          <username@remote-server; default is $USER@\$downloadServerFullName>"
	echo "-debug           <debug ouput> (optional)"
	echo " "
	echo "example (using folders & server): "
	echo "  ./compareFolders.sh \\"
	echo "    -local /home/www-data/path/to/project/subproject/downloads/drops/version/buildID \\"
	echo "    -server $USER@\$downloadServerFullName \\"
	echo "    -remote /home/data/httpd/download.eclipse.org/path/to/project/subproject/downloads/drops/branch/version "
	echo " "
	echo "example (using m5file file to verify UM upload): "
	echo "  ./compareFolders.sh -md5only -md5file ./md5s/project_buildAlias.md5 \\"
	echo "    -local /var/www/html/path/to/updates \\";
	echo "    -server $USER@\$downloadServerFullName \\";
	echo "    -remote /home/data/httpd/download.eclipse.org/path/to/updates ";
	exit 1
}

if [ $# -lt 1 ]; then
	instructions;
fi

# Create local variable based on the input
while [ "$#" -gt 0 ]; do
	case $1 in
		'-debug')
			debug=1;
			shift 0
			;;
		'-user')
			user=$2;
			shift 1
			;;
		'-local')
			local=$2;
			shift 1
			;;
		'-remote')
			remote=$2;
			shift 1
			;;
		'-server')
			server=$2;
			shift 1
			;;
		'-md5only')
			md5only=1;
			shift 0
			;;
		'-md5file')
			md5file=$2;
			shift 1
			;;
	esac
	shift 1
done	

if [ "x$server" = "x" ] || [ "x$local" = "x" ] || [ "x$remote" = "x" ]; then
	instructions;
fi

localTempFile="compareFolders.sh-$user-local-`date +%Y%m%d_%H%M%S`.tmp"; # this file will be in /tmp/
remoteTempFile="compareFolders.sh-$user-remote-`date +%Y%m%d_%H%M%S`.tmp"; # this file will be in /tmp/ locally, then uploaded to ~$user/ remotely

#############################################################################

# begin comparing...
echo " "
echo "[compare] Started `date`"

if [ "x$md5file" != "x" ]; then echo "[compare] MD5 file: $md5file"; fi
echo "[compare] Local:  $user@localhost:$local";
echo "[compare] Remote: $server:$remote";
echo " ";

if [ $md5only -eq 0 ]; then
    echo -n "[compare] Compare dir contents file sizes... "
    echo -n "local... ";  
    find $local -type f -exec stat -c "%s\n" '{}' \; | sort -fn > /tmp/$localTempFile; y=$(cat /tmp/$localTempFile | perl -pe "s/\\\n//g");
    localSizes=""; for f in $y; do localSizes=$localSizes" "$f; done
    echo "echo \$(find $remote -type f -exec stat -c %s '{}' \; | sort -fn)" > /tmp/$remoteTempFile; # cat $remoteTempFile;
    rm -f /tmp/$localTempFile
    # because of mirror issues, remote temp file should live in a folder that's cross-node (~) instead of per-node (/tmp)
    echo -n "remote... "; scp -q /tmp/$remoteTempFile $server:~/$remoteTempFile; remoteSizes=" "`ssh $server ". ~/$remoteTempFile; rm -f ~/$remoteTempFile"`; echo "";
    rm -f /tmp/$remoteTempFile
	echo "";
	if [ "$localSizes" != "$remoteSizes" ]; then
		echo "[compare] Directory sizes do not match! Compare failed."
		echo "[compare] Local sizes (in bytes):";  echo " $localSizes"  
		echo "[compare] Remote sizes (in bytes):"; echo " $remoteSizes" 
		exit 2;
	else 
		echo "[compare] Directory sizes match."
	fi
fi

# dirs & zips match. now check md5s
	#nickb@emf:~/md5-test$ md5sum -c test.txt.md5
	#md5sum: MD5 check failed for 'test.txt'
	#nickb@emf:~/md5-test$ md5sum test.txt > test.txt.md5
	#nickb@emf:~/md5-test$ md5sum -c test.txt.md5
	#nickb@emf:~/md5-test$ md5sum -cv test.txt.md5
	#test.txt       OK

cmd="md5sum -c "; 
echo "[compare] Compare md5sums (local) ... "

echo "cd $local" > /tmp/$localTempFile; 
if [ "x$md5file" = "x" ]; then 
	md5files=`cd $local; find . -maxdepth 2 -type f -name "*.md5" | sort -f`;
else 
	md5files=$md5file
fi

for f in $md5files; do echo $cmd $f >> /tmp/$localTempFile; done
if [ $debug -eq 1 ]; then echo ""; echo "   Running local script..."; echo "   ------"; cat /tmp/$localTempFile | sed -e 's!^!   !g'; echo "   ------"; fi
	
. /tmp/$localTempFile 1>/tmp/$localTempFile.1 2>/tmp/$localTempFile.2

localMD5=`cat /tmp/$localTempFile.1 /tmp/$localTempFile.2 | sed -e "s!:!!g" -e "s! !!g" -e "s!OK! OK!g" `
rm -fr /tmp/$localTempFile*
if [ $debug -eq 1 ]; then
	echo "   Local MD5 results:"
	echo "$localMD5" | sed -e '1~10{=;};s!^!   !g'; # insert line nums every 10 lines
fi

#######################################################

cmd="md5sum -c "; # don't check if we need -v here since we're always publishing to the same server (\$downloadServerFullName)
echo "[compare] Compare md5sums (remote) ... "

echo "cd "$remote > /tmp/$remoteTempFile; 
if [ "x$md5file" = "x" ]; then 
	md5files=`ssh $server "cd $remote; find . -maxdepth 2 -type f -name \"*.md5\" | sort -f"`;
else 
	md5files=$md5file
fi

for f in $md5files; do echo $cmd $f >> /tmp/$remoteTempFile; done
if [ $debug -eq 1 ]; then echo ""; echo "   Running remote script..."; echo "   ------"; cat $remoteTempFile | sed -e 's!^!   !g'; echo "   ------"; fi

scp -q /tmp/$remoteTempFile $server:~$user/$remoteTempFile; 
rm -fr /tmp/$remoteTempFile*;

remoteMD5=`ssh $server "
. ~$user/$remoteTempFile 1>~$user/$remoteTempFile.1 2>~$user/$remoteTempFile.2
cat ~$user/$remoteTempFile.1 ~$user/$remoteTempFile.2 | sed -e 's!:!!g' -e 's! !!g' -e 's!OK! OK!g' 
rm -fr ~$user/$remoteTempFile*
"` ;

#remoteMD5=`echo "$remoteMD5"`;
if [ $debug -eq 1 ]; then
	echo "   Remote MD5 results:"
	echo "$remoteMD5" | sed -e '1~10{=;};s!^!   !g'
fi

if [ "$localMD5" != "$remoteMD5" ]; then
	echo "[compare] MD5s do not match! Compare failed."
	echo "[compare] Local MD5s:";  echo "$localMD5" | sed -e '1~10{=;};s!^!   !g'
	echo "[compare] Remote MD5s:"; echo "$remoteMD5" | sed -e '1~10{=;};s!^!   !g'
	exit 4;
else 
	echo "[compare] MD5s match."
fi

#cleanup
rm -f /tmp/$localTempFile /tmp/$remoteTempFile

echo " "
echo "[compare] Done `date`"
echo " "
