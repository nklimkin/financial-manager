plugins {
    id(Plugins.kotlin) version PluginVersions.kotlin apply false
}

group = "me.nikitaklimkin"
version = "1.0-SNAPSHOT"

subprojects {

    configurations.all {
        resolutionStrategy
    }

    repositories {
        jcenter()
        mavenCentral()
        mavenLocal()
    }

    apply {
        plugin("java")
        plugin(Plugins.kotlin)
        plugin(Plugins.javaTestFixtures)
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}