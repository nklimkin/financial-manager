project.base.archivesName.set("expenses-manager-rest")

plugins {
    id(Plugins.serialization) version PluginVersions.serialization
}

dependencies {
    implementation(project(":common"))
    implementation(project(":expenses-manager:domain"))
    implementation(project(":expenses-manager:use-case"))

    implementation(Libs.arrow)

    implementation(Libs.logback)
    implementation(Libs.kotlinLogging)

    implementation(Libs.kotlinXSerialization)

    implementation(Libs.ktorServerCore)
    implementation(Libs.ktorServerNetty)
    implementation(Libs.ktorSerializationKotlinxJson)
    implementation(Libs.ktorServerContentNegotiation)
    implementation(Libs.ktorServerAuth)
    implementation(Libs.ktorServerSession)
    implementation(Libs.ktorHttpClient)

    implementation(Libs.insertKoinCore)
    implementation(Libs.insertKoinKtor)

    testImplementation(testFixtures(project(":expenses-manager:domain")))
    testImplementation(testFixtures(project(":expenses-manager:use-case")))
    testImplementation(Libs.ktorServerTestHost)
    testImplementation(Libs.kotestAssertions)
    testImplementation(Libs.koinTest)
    testImplementation(Libs.koinTestJunit5)
    testImplementation(Libs.mockk)

    testFixturesImplementation(project(":common"))
    testFixturesImplementation(project(":expenses-manager:domain"))
    testFixturesImplementation(testFixtures(project(":expenses-manager:domain")))
}