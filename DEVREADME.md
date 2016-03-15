# DiscourseDB Core Developer Instructions

This file contains instructions for DiscourseDB developers including DevOps details. Users of DiscourseDB should look at the general [README](https://github.com/DiscourseDB/discoursedb-core/blob/master/README.md) and the documentation in each module.

## Project architecture

DiscourseDB Core is a multi-module maven project in a single Git repository. The root folder contains the parent pom which is resposinble for project-wide dependency management and for defining the sub-modules that belong to DiscourseDB Core. It is also the pom that is read by the continuous integration system (CI) and determines what is being built on a regular basis.

## Creating new modules
To create a new module in eclipse, (1) check out DiscourseDB Core, (2) right click on the discoursedb-core project (3) select Maven > New Maven Module Project. Then follow the instructions. This will create a new module project and add it to the modules in the parent pom. 

## DevOps

### Jenkins

#### Builds
Whenever new code is pushed to GitHub, [Jenkins](http://moon.lti.cs.cmu.edu:8080/) starts a new build of DiscourseDB.
If the build succeeds, the new snapshot artifacts are deployed to Artifactory and from then on available for developers. Furthermore, the javadoc documentation is updated. 

Jenkins keeps up to 100 builds and provides statistics, style checks and bug analyses for each of them.

#### Creating a Release with Jenkins
Before creating a new release, make sure the current snapshot does not have any snapshot dependencies. If a module has a snapshot dependency, either replace it with a stable version or - if not possible, exclude the module from the release (i.e. remove the module from the core pom, commit, release, add module back to the core pom in the new snapshot).

Once the codebase is ready to be release, login to Jenkins with the discoursedb admin account and select the DiscourseDB job.
In the menu on the left, select "Artifactory Release Staging". You can now adapt the release information, if necessary (optional). Then click on "Build and Release to Artifactory". Jenkins automatically changes the artifact versions to the new release version, builds the codebase, creates a release branch, deploys the release artifacts to artifactory and changes the versions in the master branch to the new snapshot version. 
When this process completes, the release process is finished (since we do not split the process into "staging" and "promotion") There is no need to promote the release nor is it possible with the non-pro Artifactory.

Note: Releases are created by the GitHub user "discoursedbcmu".
