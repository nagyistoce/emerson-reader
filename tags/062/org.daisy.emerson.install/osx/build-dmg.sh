#!/bin/sh
# Script to build a Mac OS X dmg
# inherits from DAISY Pipeline GUI installers (http://www.sf.net/projects/daisymfcgui)
# 
# The script assumes that 
# - a PDE export has been done to ../pde/.
# - that EclipseOSXRepackager exists at ., ie the directory of this file.
# - that Apple Developer Tools are installed (or you will have to $ locate SetFile | xargs ls -lut)
#
# The script 
# - repackages the Eclipse PDE-exported application into a proper Mac application bundle
# - create a .dmg disk image and puts the application bundle in it
# - puts license files in the dmg
# - customize the dmg look
# - compress the dmg

# variables
FRAMEWORK="carbon"
PLATFORM="x86" 			#x86 or ppc
VERSION="0.6.0"

PDE_NAME="emerson"
APP_NAME="Emerson.app"
VOL_NAME="Emerson"
FINAL_DMG_NAME="Emerson-${FRAMEWORK}-${PLATFORM}-${VERSION}.dmg"
TMP_DIR="emerson"
SRC_DIR="${TMP_DIR}/src"

#clean and create base dirs
rm -Rf "${TMP_DIR}"
mkdir "${TMP_DIR}"
mkdir "${SRC_DIR}"

#assemble what we need in SRC_DIR
cp -R "../pde/macosx.${FRAMEWORK}.${PLATFORM}/" "${SRC_DIR}"
mkdir "${SRC_DIR}/${PDE_NAME}/features" 		#repackager needs it

# repackage (see http://code.google.com/p/eclipse-osx-repackager/)
# Usage:  EclipseOSXRepackager [options] path_to_eclipse [destination]
./EclipseOSXRepackager "${SRC_DIR}/${PDE_NAME}/" "${SRC_DIR}/${APP_NAME}"

mkdir "${SRC_DIR}/INFO"
cp ../etc/epl-v10 "${SRC_DIR}/INFO"
ln -s /Applications "${SRC_DIR}/Applications"
rm -Rf "${SRC_DIR}/${PDE_NAME}"

# create dmg
mkdir "${TMP_DIR}/dmg"
hdiutil create -srcfolder "${SRC_DIR}" -volname "${VOL_NAME}" -fs HFS+ -fsargs "-c c=64,a=16,e=16" -format UDRW "${TMP_DIR}/dmg/tmp.dmg"

# mount
DEV_NAME=`hdiutil attach -readwrite -noverify -noautoopen "${TMP_DIR}/dmg/tmp.dmg" | egrep '^/dev/' | sed 1q | awk '{print $1}'`
echo "Mounted: ${DEV_NAME}"

#customize
mkdir "/Volumes/${VOL_NAME}/.background"
cp "dmg-background.png" "/Volumes/${VOL_NAME}/.background/background.png"
cp "dmg-icon.icns" "/Volumes/${VOL_NAME}/.VolumeIcon.icns"
osascript customizeDMG.scpt
/Developer/Tools/SetFile -a C "/Volumes/${VOL_NAME}"

# unmount
hdiutil detach "${DEV_NAME}"

# compress image
rm "${FINAL_DMG_NAME}"
hdiutil convert -format UDBZ -imagekey zlib-level=9 "./${TMP_DIR}/dmg/tmp.dmg" -o "${FINAL_DMG_NAME}"

#remove tmp dir
rm -Rf "${TMP_DIR}"
