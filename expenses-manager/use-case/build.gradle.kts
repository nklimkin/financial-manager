project.base.archivesName.set("expenses-manager-use-case")

dependencies {
    implementation(project(":common"))
    implementation(project(":expenses-manager:domain"))

    implementation(Libs.logback)
    implementation(Libs.kotlinLogging)

    implementation(Libs.arrow)

    testImplementation(Libs.kotestAssertions)
    testImplementation(Libs.kotlinTest)
    testImplementation(Libs.mockk)
    testImplementation(testFixtures(project(":expenses-manager:domain")))

    testFixturesImplementation(project(":expenses-manager:domain"))
    testFixturesImplementation(testFixtures(project(":expenses-manager:domain")))
}