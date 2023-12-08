targetDir=build/deploy/LazyGui
sourceJarName=LazyGui-with-gson.jar
targetJarName=LazyGui.jar

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
7z a $name.zip $name/

cp $name/library.properties $name.txt
cp $name/library/$name.jar $name.jar

echo "Deployed LazyGui successfully."
echo "Upload the three .jar, .txt and .zip files here:"
echo "  https://github.com/KrabCode/LazyGui/releases/tag/latest"