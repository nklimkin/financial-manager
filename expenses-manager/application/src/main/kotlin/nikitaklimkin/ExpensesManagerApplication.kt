package nikitaklimkin

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import nikitaklimkin.plugin.configureKoinDI
import me.nikitaklimkin.plugin.configureRouting
import me.nikitaklimkin.plugin.configureSerialization

fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureKoinDI()
    configureRouting()
    configureSerialization()
}