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
package app.jjerrell.choretender.service.auth

import app.jjerrell.choretender.service.AuthData
import app.jjerrell.choretender.service.auth.model.UserInfo
import app.jjerrell.choretender.service.auth.model.UserSession
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.headers
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.koin.ktor.ext.inject

internal fun Application.setupAuthorization() {
    val httpClient: HttpClient by inject()
    install(Sessions) { cookie<UserSession>("user_session") }

    val redirects = mutableMapOf<String, String>()
    install(Authentication) {
        oauth("auth-oauth-google") {
            // Configure oauth authentication
            urlProvider = { "http://localhost:8080/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "google",
                    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                    accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
                    requestMethod = HttpMethod.Post,
                    clientId = System.getenv("GOOGLE_CLIENT_ID"),
                    clientSecret = System.getenv("GOOGLE_CLIENT_SECRET"),
                    defaultScopes = listOf("https://www.googleapis.com/auth/userinfo.profile"),
                    extraAuthParameters = listOf("access_type" to "offline"),
                    onStateCreated = { call, state ->
                        // saves new state with redirect url value
                        call.request.queryParameters[AuthData.REDIRECT_PARAM]?.let {
                            redirects[state] = it
                        }
                    }
                )
            }
            client = httpClient
        }
    }
    routing { authroute(redirects) }
}

private fun Route.authroute(redirects: Map<String, String>): Route {
    return authenticate("auth-oauth-google") {
        get("/login") {
            // Redirects to 'authorizeUrl' automatically
        }

        get("/callback") {
            val currentPrincipal: OAuthAccessTokenResponse.OAuth2? = call.principal()
            // redirects home if the url is not found before authorization
            currentPrincipal?.let { principal ->
                principal.state?.let { state ->
                    call.sessions.set(UserSession(state, principal.accessToken))
                    redirects[state]?.let { redirect ->
                        call.respondRedirect(redirect)
                        return@get
                    }
                }
            }
            call.respondRedirect("/profile")
        }
    }
}

suspend fun ApplicationCall.getSession(): UserSession? =
    sessions.get()
        ?: run {
            val redirectUrl =
                URLBuilder("http://0.0.0.0:8080/login").run {
                    parameters.append(AuthData.REDIRECT_PARAM, request.uri)
                    build()
                }
            respondRedirect(redirectUrl)
            null
        }

suspend fun getProfile(httpClient: HttpClient, userSession: UserSession): UserInfo =
    httpClient
        .get("https://www.googleapis.com/oauth2/v2/userinfo") {
            headers { append(HttpHeaders.Authorization, "Bearer ${userSession.token}") }
        }
        .body()
