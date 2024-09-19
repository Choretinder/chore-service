/*
 * Choretinder
 * Copyright (C) 2024  Jacob Jerrell (@jjerrell)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package app.jjerrell.choretender.service

import app.jjerrell.choretender.service.di.appModule
import app.jjerrell.choretender.service.route.testRoutes
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    setupPlugins()

    routing {
        testRoutes()
        get("/") { call.respondText("Ktor: ${Greeting().greet()}") }
    }
}

private fun Application.setupPlugins() {
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
            }
        )
    }

    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
}
