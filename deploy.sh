targetDir=build/deploy/LazyGui
sourceJarName=LazyGui-with-gson.jar
targetJarName=LazyGui.jar

echo "Deleting data/gui/* ..."
# testing saves are stored in /data/gui
rm -rf ./data/gui

echo "Cleaning up build/deploy/ ..."
# clean deploy directory
rm -rf ./build/deploy/*

echo "Copying sources..."
mkdir -p $targetDir/data/ && cp -r data/ $targetDir/
mkdir -p $targetDir/src/ && cp -r src $targetDir/
echo "Copying docs..."
mkdir -p $targetDir/reference/ && cp -r docs/* $targetDir/reference
mkdir -p $targetDir/examples/ && cp -r src/main/java/com/krab/lazy/examples $targetDir/
echo "Copying jar..."
mkdir -p $targetDir/library/ && cp build/libs/$sourceJarName $targetDir/library/$targetJarName
cp library.properties $targetDir/library.properties
cp README.md $targetDir/README.md
cp LICENSE.md $targetDir/src/LICENSE.md

name=LazyGui
echo "Zipping..."
cd $targetDir/.. || exit
rm -rf $name.zip
7z a -bb0 $name.zip $name/ > nul

cp $name/library.properties $name.txt
cp $name/library/$name.jar $name.jar

echo
echo "Deployed LazyGui successfully to:"
pwd
echo
echo "Version in library.properties:"
grep prettyVersion $name/library.properties
echo
echo "To publish this release upload these files"
echo "  - LazyGui.jar"
echo "  - LazyGui.txt"
echo "  - LazyGui.zip"
echo
echo "--> https://github.com/KrabCode/LazyGui/releases/tag/latest"