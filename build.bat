@echo off
rem Builds GooeyPainter (Windows). Double-click it or run:  build.bat
rem
rem   GooeyPainter.jar           compiled library, for students' lib folders
rem   GooeyPainter-sources.jar   source code, so VS Code shows the docs on hover
rem   docs\                      the Javadoc website (served by GitHub Pages),
rem                              with copies of both jars for download links
cd /d "%~dp0"

if exist build rmdir /s /q build
if exist docs rmdir /s /q docs
if exist GooeyPainter.jar del GooeyPainter.jar
if exist GooeyPainter-sources.jar del GooeyPainter-sources.jar
mkdir build

javac --release 11 -Xdoclint:all -d build src\gooeypainter\*.java || goto :error
jar --create --file GooeyPainter.jar -C build . || goto :error
jar --create --file GooeyPainter-sources.jar -C src . || goto :error

javadoc -quiet -Xdoclint:all -d docs -windowtitle "GooeyPainter" -doctitle "GooeyPainter" -sourcepath src gooeypainter || goto :error
copy GooeyPainter.jar docs\ >nul
copy GooeyPainter-sources.jar docs\ >nul
type nul > docs\.nojekyll

rmdir /s /q build
echo Built GooeyPainter.jar, GooeyPainter-sources.jar, and docs\
exit /b 0

:error
echo Build failed.
exit /b 1
