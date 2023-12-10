object LibVersions {
    const val kotlinXSerialization = "1.6.0"

    const val arrow = "1.2.1"

    const val kotest = "5.8.0"

    const val kMongo = "4.6.0"

    const val kotlinLogging = "2.0.11"
    const val logback = "1.2.6"

    const val ktorVersion = "2.3.0"
    const val ktorServerCore = ktorVersion
    const val ktorServerNetty = ktorVersion
    const val ktorServerContentNegotiation = ktorVersion
    const val ktorSerializationKotlinxJson = ktorVersion

    const val koinCoreVersion = "3.4.0"
}

object Libs {

    const val kotlinXSerialization =
        "org.jetbrains.kotlinx:kotlinx-serialization-json:${LibVersions.kotlinXSerialization}"

    const val arrow = "io.arrow-kt:arrow-core:${LibVersions.arrow}"

    const val kotestRunner = "io.kotest:kotest-runner-junit5:${LibVersions.kotest}"
    const val kotestAssertions = "io.kotest:kotest-assertions-core:${LibVersions.kotest}"
    const val kotestProperty = "io.kotest:kotest-property:${LibVersions.kotest}"

    const val kMongo = "org.litote.kmongo:kmongo:${LibVersions.kMongo}"

    const val kotlinLogging = "io.github.microutils:kotlin-logging-jvm:${LibVersions.kotlinLogging}"
    const val logback = "ch.qos.logback:logback-classic:${LibVersions.logback}"

    const val ktorServerCore = "io.ktor:ktor-server-core-jvm:${LibVersions.ktorServerCore}"
    const val ktorServerNetty = "io.ktor:ktor-server-netty-jvm:${LibVersions.ktorServerNetty}"
    const val ktorServerContentNegotiation =
        "io.ktor:ktor-server-content-negotiation-jvm:${LibVersions.ktorServerContentNegotiation}"
    const val ktorSerializationKotlinxJson =
        "io.ktor:ktor-serialization-kotlinx-json-jvm:${LibVersions.ktorSerializationKotlinxJson}"

    const val insertKoinKtor = "io.insert-koin:koin-ktor:${LibVersions.koinCoreVersion}"
    const val insertKoinCore = "io.insert-koin:koin-core:${LibVersions.koinCoreVersion}"

}

object PluginVersions {
    const val kotlin = "1.8.0"
    const val ktor = "2.3.0"
    const val serialization = "1.8.0"
}

object Plugins {
    const val kotlin = "org.jetbrains.kotlin.jvm"
    const val ktor = "io.ktor.plugin"
    const val serialization = "org.jetbrains.kotlin.plugin.serialization"

}