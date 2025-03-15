[![Crowdin](https://badges.crowdin.net/lotas-light/localized.svg)](https://crowdin.com/project/lotas-light)
# LoTAS-Light
A very slimmed down version of [LoTAS](https://github.com/MinecraftTAS/LoTAS), in an attempt to make it easily upgradable to newer versions.

Needs Fabric API

## Features
- Tickratechanger
  - Increase tickrate (Default key: <kbd>.</kbd>)
  - Decrease tickrate (<kbd>,</kbd>)
  - Freeze tickrate (<kbd>F8</kbd>)
  - Advance tickrate (While tickrate is frozen) (<kbd>F9</kbd>)
- Savestates
  - Saving (<kbd>J</kbd>)
  - Loading (<kbd>K</kbd>)

## Translations
Help translate this mod with [Weblate](https://weblate.minecrafttas.com/projects/lotas-light/main/)

### Contributions
- Chinese Simplified
  - Blom
  - BoredYukolin
  - JustaSqu1d
- French
  - Elvee
- German
  - ScribbleTAS
- Japanese
  - Naruyoko
- Korean
  - swwgdxed
- LOLCAT
  - ScribbleTAS
- Polish
  - 4NTJ
  - NULLderef

## Setup
This mod uses the preprocessor [Discombobulator](https://github.com/MinecraftTAS/Discombobulator),
which allows us to have one codebase for multiple Minecraft versions.

### 1. Clone this repository into your workspace 
```sh
git clone https://github.com/MinecraftTAS/LoTAS-Light.git
```
### 2. Go into the project 
```sh
cd LoTAS-Light/
``` 
### 3. Setup sub projects
Run
```sh
./gradlew preprocessBase
```
This will copy the files from `LoTAS-Light/src/...` into `LoTAS-Light/1.20.4/src`, `LoTAS-Light/1.20.6/src`, etc...

And code between comments like
```java
// # 1.20.6
//$$ LoTASLight.savestateHandler
// # 1.20.4
  LoTASLight.savestateHandler=null;
// # end
```
will be enabled or disabled accordingly between each version
### 4. Import root project into your IDE
#### IntelliJ
You only need to open the build.gradle file in LoTAS-Light (not in sub projects like 1.20.4, those will be automatically imported!), and it should recognize it as a gradle project
#### Eclipse
Set your workspace folder to the parent folder of the LoTAS-Light folder, so if you have something like `workspace/LoTAS-Light`, set the workspace folder to "workspace".

Click `File>Import` then search for "Existing Gradle project" and click next

Select the "LoTAS-Light" folder as a project root directory then click next

(optional) In this step you can set up a specific JDK and gradle version if you haven't set that up yet, but currently this project needs JDK 23 and a Gradle version greater than 8.12.1. 

Then click finish and wait for it to set up

### 5. Decompile source and download assets
You have to run the gradle task `genSources`. You can do that from within your IDE or run `./gradlew genSources` in the console. This will decompile all Minecraft versions

#### Eclipse
Additionally you need to run the task `eclipse` as well, which will generate .launch configs in each subproject which you can use to start Minecraft from withing your IDE

## Building
After you have set up and preprocessed the versions, you can run either `./gradlew :1.21.4:build` to build only one version (built jar can be found in `LoTAS-Light/1.21.4/build/libs`), or run the task `collectBuilds`. This will build all versions and collect all versions in `LoTAS-Light/build`

For more information on how to work with Discombobulator, check the [Wiki](https://github.com/MinecraftTAS/Discombobulator/wiki) or ask us on [Discord](https://discord.gg/jGhNxpd)