project.base.archivesName.set("expenses-manager-domain")

dependencies {
    implementation(project(":common"))
    implementation(Libs.arrow)

    testImplementation(Libs.kotestAssertions)
    testImplementation(Libs.kotestProperty)
    testImplementation(Libs.kotestRunner)

    testFixturesImplementation(Libs.arrow)
    testFixturesImplementation(project(":common"))
}