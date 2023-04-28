name=LazyGui
targetDir=out/deploy/LazyGui

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
7z a $name.zip $targetDir

echo "Deployed LazyGui successfully."