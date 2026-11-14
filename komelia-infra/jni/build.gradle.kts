import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
}

group = "io.github.snd_r.komelia.infra.jni"
version = "unspecified"

// Architectures are selected by invoking their copy task; order the merge after those selected tasks.
tasks.matching { it.name == "mergeAndroidMainJniLibFolders" }.configureEach {
    mustRunAfter(rootProject.tasks.matching {
        it.name.startsWith("android-") && it.name.endsWith("_copyJniLibs")
    })
}

kotlin {
    android {
        namespace = "io.github.snd_r.komelia.infra.jni"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions { jvmTarget = JvmTarget.JVM_17 }
    }

    jvm {
        compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
    }

    sourceSets {
        commonMain.dependencies {}
        androidMain.dependencies {}
        jvmMain.dependencies {

            implementation(libs.slf4j.api)
            implementation(libs.directories)
        }
    }
}
