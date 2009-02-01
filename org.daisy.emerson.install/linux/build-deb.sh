#!/bin/sh
# Script to build a debian package.
# The script assumes that a PDE export has been done to ../pde/.

version="0.6.0"

rm -rf emerson

#create deb dir structure
mkdir emerson
mkdir emerson/DEBIAN/
mkdir emerson/usr/
mkdir emerson/usr/share/
mkdir emerson/usr/share/doc/
mkdir emerson/usr/share/doc/emerson/
mkdir emerson/usr/share/pixmaps/
mkdir emerson/usr/share/applications/
mkdir emerson/usr/share/icons/
mkdir emerson/usr/share/icons/hicolor/
mkdir emerson/usr/share/icons/hicolor/48x48/
mkdir emerson/usr/share/icons/hicolor/48x48/apps/

#copy desktop images
cp ../../org.daisy.emerson.ui/icons/goggle-48x48x32.png emerson/usr/share/icons/hicolor/48x48/apps/emerson.png
cp ../../org.daisy.emerson.ui/icons/goggle-48x48x32.png emerson/usr/share/pixmaps/emerson.png
cp ../../org.daisy.emerson.ui/icons/goggle.xpm emerson/usr/share/pixmaps/emerson.xpm

#copy the app to usr/share
cp -R ../pde/linux.gtk.x86/emerson/ ./emerson/usr/share/

#create the .desktop file
desktop="emerson/usr/share/applications/emerson.desktop"
data="[Desktop Entry]\nVersion=1.0\nEncoding=UTF-8\nName=Emerson Reader\nType=Application\nComment=Read DAISY DTBs\nExec=/usr/share/emerson/emerson\nIcon=emerson\nTerminal=false\nCategories=Audio;AudioVideo;Viewer;Player;GTK;Java"
echo "$data" > $desktop

#create the control file
control="emerson/DEBIAN/control"
data="Package: emerson\nVersion: "$version"\nSection: contrib/sound\nPriority: optional\nArchitecture: i386\nInstalled-Size: 14029\nMaintainer: Markus Gylling <markus.gylling@gmail.com>\nHomepage: http:code.google.com/p/emerson-dtb\nProvides: emerson\nDescription: A Java- and RCP/SWT based Epub and DAISY DTB Player. You will need Java 5 or later installed."
echo "$data" > $control

#create the copyright file
cp ../etc/epl-v10 emerson/usr/share/doc/emerson/copyright

#build the .deb
fakeroot dpkg-deb -b emerson emerson_"$version"_i386.deb
 
#clean up
rm -rf emerson

#check
lintian emerson_"$version"_i386.deb
