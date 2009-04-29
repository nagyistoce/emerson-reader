#!/bin/sh
# Script to build a tar atchive.
# The script assumes that a PDE export has been done to ../pde/.

version="0.6.2"

rm -rf emerson

cp -R ../pde/linux.gtk.x86/emerson/ .
cp ../etc/epl-v10 ./emerson
cp ../etc/emerson-release-notes.txt ./emerson

tar czf emerson_"$version"_i386.tar.gz emerson

rm -rf emerson