###### notes by and for krab, the author of this repo, who is the only one creating new official releases for now

---
### How to create a new library release for the PDE

- Increment the versions in [library.properties](library.properties) according to [semantic versioning](https://semver.org/)
- Run `gradle shadowJar` to create a new jar file in the `build/libs` directory including gson classes but not processing classes
- Run [deploy.sh](deploy.sh) which packs docs, sources and jars into a .jar, .txt and .zip format that can be parsed by a third party Processing library utility
- Manually upload the results of deploy.sh to the [latest release page](https://github.com/KrabCode/LazyGui/releases/tag/latest), increment the version in the release name there and add some patch notes
- The PDE always looks for the release in the 'latest' tag of this repository and makes it instantly available in the Tools -> Manage Tools -> Libraries menu
- When the number of "commits to master since this release" gets annoying it can be deleted with `git_delete_latest_tag.sh`
    - But that also turns the latest release on GitHub into a draft so make sure to re-release it for it to be available in Processing

---
### How to update the javadocs
- If the public facing API changed then regenerate javadocs on the master branch from the `Generate Javadoc...` IntelliJ window
- Settings:
    - Custom scope to only include "com.krab.lazy.*" (should be around 6 files that comprise the main API)
    - Output directory: `\docs`
    - Visibility level: public
- Pushing the new \docs to master will then automatically publish the docs at https://krabcode.github.io/LazyGui/