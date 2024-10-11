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

import app.jjerrell.choretender.service.auth.getProfile
import app.jjerrell.choretender.service.auth.getSession
import app.jjerrell.choretender.service.auth.setupAuthorization
import app.jjerrell.choretender.service.di.appModule
import app.jjerrell.choretender.service.domain.repositoryModule
import app.jjerrell.choretender.service.route.choreRoutes
import app.jjerrell.choretender.service.route.familyRoutes
import app.jjerrell.choretender.service.route.userRoutes
import io.ktor.client.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.inject
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.event.Level

fun main() {
    embeddedServer(
            Netty,
            port = DefaultConfig.SERVER_PORT,
            host = "0.0.0.0",
            module = Application::module
        )
        .start(wait = true)
}

fun Application.module() {
    setupPlugins()
    setupKoin()
    setupRouting()
    setupAuthorization()
}

internal fun Application.setupRouting() {
    val httpClient: HttpClient by inject()
    routing {
        route("v1") {
            userRoutes()
            familyRoutes()
            choreRoutes()
        }

        get("home") {
            val sessionData = call.getSession()
            sessionData?.let { call.respond("State: ${it.state}; Token: ${it.token}") }
        }
        get("profile") {
            val sessionData = call.getSession()
            sessionData?.let {
                val profile = getProfile(httpClient = httpClient, userSession = it)
                call.respond(profile)
            }
        }
    }
}

internal fun Application.setupPlugins() {
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
            }
        )
    }

    install(IgnoreTrailingSlash)

    install(CallLogging) { level = Level.DEBUG }
}

internal fun Application.setupKoin() {
    install(Koin) {
        slf4jLogger()
        modules(appModule + repositoryModule)
    }
}
