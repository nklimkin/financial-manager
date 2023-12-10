project.base.archivesName.set("expenses-manager-use-case")

plugins {
    id(Plugins.serialization) version PluginVersions.serialization
}

dependencies {
    implementation(project(":common"))
    implementation(project(":expenses-manager:domain"))
    implementation(Libs.arrow)
    implementation(Libs.kotlinXSerialization)

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}