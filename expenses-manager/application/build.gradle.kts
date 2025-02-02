project.base.archivesName.set("expenses-manager-application")

plugins {
    id(Plugins.ktor) version PluginVersions.ktor
    id(Plugins.serialization) version PluginVersions.serialization
    application
}

dependencies {

    implementation(project(":common"))
    implementation(project(":expenses-manager:domain"))
    implementation(project(":expenses-manager:persistence"))
    implementation(project(":expenses-manager:use-case"))
    implementation(project(":expenses-manager:rest"))

    implementation(Libs.arrow)

    implementation(Libs.kMongo)

    implementation(Libs.insertKoinCore)
    implementation(Libs.insertKoinKtor)

    implementation(Libs.kotlinLogging)
    implementation(Libs.logback)

    implementation(Libs.ktorServerCore)
    implementation(Libs.ktorServerNetty)
    implementation(Libs.ktorServerContentNegotiation)
    implementation(Libs.ktorSerializationKotlinxJson)
    implementation(Libs.ktorHttpClient)
    implementation(Libs.ktorHttpClientCIO)
    implementation(Libs.ktorHttpClientContentNegotiation)
}

application {
    mainClass.set("ExpensesManagerApplication.kt")
}