package com.example

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import routes.authRouting
import routes.publicRoutes

fun Application.configureRouting() {
    val host = environment.config.propertyOrNull("ktor.deployment.host")?.getString() ?: "0.0.0.0"
    val port = environment.config.propertyOrNull("ktor.deployment.port")?.getString() ?: "8080"

    monitor.subscribe(ApplicationStarted) {
        log.info("Servidor ServiConecta listo en http://$host:$port")
    }

    routing {
        get("/") {
            call.respondText("ServiConecta Mobile Online!")
        }
        authRouting()
        publicRoutes()
    }
}