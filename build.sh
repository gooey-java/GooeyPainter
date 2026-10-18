#!/bin/sh
# Builds GooeyPainter (Mac / Linux). Run from anywhere:  ./build.sh
#
#   GooeyPainter.jar           compiled library, for students' lib folders
#   GooeyPainter-sources.jar   source code, so VS Code shows the docs on hover
#   docs/                      the Javadoc website (served by GitHub Pages),
#                              with copies of both jars for download links
set -e
cd "$(dirname "$0")"

rm -rf build docs GooeyPainter.jar GooeyPainter-sources.jar
mkdir -p build

javac --release 11 -Xdoclint:all -d build src/gooeypainter/*.java
jar --create --file GooeyPainter.jar -C build .
jar --create --file GooeyPainter-sources.jar -C src .

javadoc -quiet -Xdoclint:all -d docs \
    -windowtitle "GooeyPainter" -doctitle "GooeyPainter" \
    -sourcepath src gooeypainter
cp GooeyPainter.jar GooeyPainter-sources.jar docs/
touch docs/.nojekyll

rm -rf build
echo "Built GooeyPainter.jar, GooeyPainter-sources.jar, and docs/"
