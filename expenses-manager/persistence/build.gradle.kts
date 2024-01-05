project.base.archivesName.set("expenses-manager-persistence")

dependencies {
    implementation(project(":common"))
    implementation(project(":expenses-manager:domain"))
    implementation(project(":expenses-manager:use-case"))

    implementation(Libs.arrow)
    implementation(Libs.kMongo)

    implementation(Libs.logback)
    implementation(Libs.kotlinLogging)

    testImplementation(Libs.koinTest)
    testImplementation(Libs.koinTestJunit5)
    testImplementation(Libs.testContainers)
    testImplementation(Libs.testContainersJunitJupiter)
    testImplementation(Libs.testContainersMongoDb)
    testImplementation(Libs.kotestAssertions)
    testImplementation(testFixtures(project(":expenses-manager:domain")))

    testFixturesImplementation(project(":common"))
    testFixturesImplementation(project(":expenses-manager:domain"))
    testFixturesImplementation(testFixtures(project(":expenses-manager:domain")))
    testFixturesImplementation(Libs.kMongo)
}