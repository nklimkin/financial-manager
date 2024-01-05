project.base.archivesName.set("expenses-manager-use-case")

plugins {
    id(Plugins.serialization) version PluginVersions.serialization
}

dependencies {
    implementation(project(":common"))
    implementation(project(":expenses-manager:domain"))

    implementation(Libs.logback)
    implementation(Libs.kotlinLogging)

    implementation(Libs.arrow)
    implementation(Libs.kotlinXSerialization)

    testImplementation(Libs.kotestAssertions)
    testImplementation(Libs.kotlinTest)
    testImplementation(Libs.mockk)
    testImplementation(testFixtures(project(":expenses-manager:domain")))

    testFixturesImplementation(project(":expenses-manager:domain"))
    testFixturesImplementation(testFixtures(project(":expenses-manager:domain")))
}