# choose deploy target dir
t=deploy

echo "Cleaning $t..."
rm -rf $t/

echo "Deploying LazyGui to $t..."
mkdir -p $t/data/ && cp -r data/ $t/
mkdir -p $t/src/ && cp -r src $t/
mkdir -p $t/reference/ && cp -r docs/ $t/reference
mkdir -p $t/examples/ && cp -r src/main/java/com/krab/lazy/examples $t/
mkdir -p $t/library/ && cp out/artifacts/LazyGui.jar $t/library/LazyGui.jar
cp library.properties $t/library.properties
cp README.md $t/README.md

echo "Deployed LazyGui successfully."