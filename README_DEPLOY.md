###### notes by and for krab, the author of this repo, who is the only one creating new official releases for now

---
### Regenerate the javadocs
- If the public facing API changed then regenerate javadocs on the master branch from the `Generate Javadoc...` IntelliJ window
- Settings:
  - Custom scope to only include `com.krab.lazy.*` (should only be around 6 classes that constitute the public facing API)
  - Output directory: `\docs`
  - Visibility level: public
- If you added a new class to the public API then don't forget to add it in git, otherwise its page in the docs will 404
- Pushing the new \docs to master will then automatically publish the docs at https://krabcode.github.io/LazyGui/
---

### Create a new library release for the PDE

- Increment the versions in [library.properties](library.properties) according to [semantic versioning](https://semver.org/)
- Run `gradle shadowJar` to create a new jar file in the `build/libs` directory including gson classes but not processing classes
- Run [deploy.sh](deploy.sh) which packs docs, sources and jars into a .jar, .txt and .zip format that can be parsed by a third party Processing library utility
- Manually upload the results of deploy.sh to the [latest release page](https://github.com/KrabCode/LazyGui/releases/tag/latest), increment the version in the release name there and add some patch notes
- The PDE always looks for the release in the 'latest' tag of this repository and makes it instantly available in the Tools -> Manage Tools -> Libraries menu
- When the number of "commits to master since this release" gets annoying it can be deleted with `git_delete_latest_tag.sh`
    - But that also turns the latest release on GitHub into a draft so make sure to re-release it for it to be available in Processing
---

### Publish a new release to Maven Central

Maven/Gradle users consume the library from Maven Central under the group id `io.github.krabcode` (the `com.krab.lazy` package names are unchanged). This is a separate channel from the PDE `latest` release above.

One-time setup (already done, listed here for reference / a new machine):
- The `io.github.krabcode` namespace is verified on [central.sonatype.com](https://central.sonatype.com) via the GitHub account
- A GPG signing key exists locally (`gpg --list-secret-keys`) and its public half is uploaded to both `keyserver.ubuntu.com` and `keys.openpgp.org` — Central verifies the `.asc` signatures against these
- A Central Portal user token is stored in `~/.gradle/gradle.properties` as `centralUsername` / `centralPassword` (outside the repo, never committed)

Per release:
- Bump the versions in [library.properties](library.properties) (same bump as the PDE step — the Gradle build derives the Maven version from `prettyVersion`, minus the leading `v`)
- Update the version shown in the Maven and Gradle snippets in [README.md](README.md) so the docs match what is live
- Run `gradle publishToCentral` — this builds the plain jar (with font + shaders), sources, javadoc, signs everything, zips a bundle and uploads it to the Central Portal
- The upload is `USER_MANAGED`, so nothing goes public automatically: review the deployment at https://central.sonatype.com/publishing/deployments and click **Publish** (or Drop it to cancel)
- A freshly created signing key can take up to ~30 minutes before Central's validator can find it on the keyservers — if validation fails with "Could not find a public key by the key fingerprint", just wait and re-run
