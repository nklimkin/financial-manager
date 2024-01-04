project.base.archivesName.set("expenses-manager-rest")

dependencies {
    implementation(project(":common"))
    implementation(project(":expenses-manager:domain"))
    implementation(project(":expenses-manager:use-case"))

    implementation(Libs.arrow)

    implementation(Libs.kotlinLogging)

    implementation(Libs.ktorServerCore)
    implementation(Libs.ktorServerNetty)
    implementation(Libs.ktorSerializationKotlinxJson)
    implementation(Libs.ktorServerContentNegotiation)

    implementation(Libs.insertKoinCore)
    implementation(Libs.insertKoinKtor)

    testImplementation(Libs.ktorServerTestHost)
    testImplementation(Libs.kotestAssertions)
    testImplementation(Libs.koinTest)
    testImplementation(Libs.koinTestJunit5)
    testImplementation(Libs.mockk)

    testFixturesImplementation(testFixtures(project(":expenses-manager:domain")))
}