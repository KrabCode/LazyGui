name=LazyGui
targetDir=out/deploy/LazyGui

echo "Cleaning saves from data/gui/"
rm -rf /data/gui/*

echo "Cleaning $targetDir..."
rm -rf $targetDir/

echo "Deploying to $targetDir..."
mkdir -p $targetDir/data/ && cp -r data/ $targetDir/
mkdir -p $targetDir/src/ && cp -r src $targetDir/
mkdir -p $targetDir/reference/ && cp -r docs/* $targetDir/reference
mkdir -p $targetDir/examples/ && cp -r src/main/java/com/krab/lazy/examples $targetDir/
mkdir -p $targetDir/library/ && cp out/artifacts/LazyGui.jar $targetDir/library/LazyGui.jar
cp library.properties $targetDir/library.properties
cp README.md $targetDir/README.md
cp LICENSE.md $targetDir/src/LICENSE.md

echo "Zipping..."
cd out/deploy || exit
rm -rf $name.zip
7z a $name.zip $name/ > NUL

cp $name/library.properties $name.txt
cp $name/library/$name.jar $name.jar

echo "Deployed LazyGui successfully."