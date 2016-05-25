# removeCR.sh

# remove carriage returns (\r) from files in windows

# occurs if exporting or checking out from CVS client on windwos

# arg: scripts directory



for file in $1/*.sh; do

    tmpfile=$file".tmp";

    newfile=$file;

    tr "\r\n" "\n" < $file > $tmpfile;

    rm -f $file;

    mv $tmpfile $newfile;

done