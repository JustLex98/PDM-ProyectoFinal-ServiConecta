package plugins

import io.ktor.server.plugins.cors.routing.*
import io.ktor.http.*
import io.ktor.server.application.*

fun Application.configureCORS() {
    install(CORS) {
        anyHost() // Esto permite que tu emulador de Android se conecte
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Get)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
    }
}