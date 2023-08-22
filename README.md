
## Usage

#### Step 1. Configure your build.gradle (in top level directory)
```gradle
repositories {
    ...
    maven { url 'https://jitpack.io' }
}
dependencies {
    ...
    classpath "com.github.CodingGay:BlackObfuscator-ASPlugin:3.7"
}
```
#### Step 2. Apply the plugin in your app module
```gradle
...
apply plugin: 'com.android.application'
// Add
apply plugin: 'top.niunaijun.blackobfuscator'
```
or you can do it like this
```gradle
plugins {
    id 'com.android.application'
    // Add
    id 'top.niunaijun.blackobfuscator'
}
```
#### Step 3. Add configuration in your build.gradle (Module: app)
```gradle
android {
    ...

    defaultConfig {
       ...
    }
}

// Configuration
MbbankObfuscator {
    // Enabled state
    enabled true
    // Obfuscation depth
    depth 2
    // The classes which need to be obfuscated
    obfClass = ["mbbank.secure", "com.abc"]
    // It will not obfuscate the classes that in blackClass
    blackClass = ["mbbank.secure.black"]
}

dependencies {
    ...
}