#!/bin/csh
set filename=incoming.fax
unset noclobber
tifftopnm $filename | pnmcut 0 0  1728 1055 > $filename.pbm
pnmcrop $filename.pbm | pnmmargin -white 1 | ./pbmfill >$filename.tmp.pbm

