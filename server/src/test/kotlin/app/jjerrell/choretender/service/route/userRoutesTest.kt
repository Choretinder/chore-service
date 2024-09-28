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
package app.jjerrell.choretender.service.route

import app.jjerrell.choretender.service.domain.model.user.UserDetailCreate
import app.jjerrell.choretender.service.domain.model.user.UserDetailUpdate
import app.jjerrell.choretender.service.domain.model.user.UserType
import app.jjerrell.choretender.service.domain.repository.IChoreServiceUserRepository
import app.jjerrell.choretender.service.setupPlugins
import app.jjerrell.choretender.service.util.TestData
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.After
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

class UserRoutesTest {
    @After
    fun resetMocks() {
        unmockkAll()
        stopKoin()
    }

    @Test
    fun `testUserRoutes badRequest`() = testApplication {
        application {
            setupPlugins()
            routing { route("test") { userRoutes() } }
        }

        // Expected to match the `{id?}` route but fail due to missing ID
        assertEquals(HttpStatusCode.BadRequest, client.get("test/user").status)

        // Expected to throw due to missing request body transformation
        assertEquals(HttpStatusCode.BadRequest, client.post("test/user").status)
        assertEquals(HttpStatusCode.BadRequest, client.put("test/user").status)
    }

    @Test
    fun `testUserRoutes serverError`() = testApplication {
        val client = createClient { install(ContentNegotiation) { json() } }
        application {
            setupPlugins()
            routing { route("test") { userRoutes() } }
        }

        // Expected to throw due to missing DI setup
        assertEquals(HttpStatusCode.InternalServerError, client.get("test/user/1").status)
        assertEquals(
            HttpStatusCode.InternalServerError,
            client
                .post("test/user") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        UserDetailCreate(
                            name = "Test",
                            type = UserType.MANAGER,
                            contactInfo = null,
                            createdBy = 0
                        )
                    )
                }
                .status
        )
        assertEquals(
            HttpStatusCode.InternalServerError,
            client
                .put("test/user") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        UserDetailUpdate(
                            id = 1,
                            name = "Test",
                            type = UserType.MANAGER,
                            contactInfo = null,
                            updatedBy = 0
                        )
                    )
                }
                .status
        )
    }

    @Test
    fun `testUserRoutes notFoundError`() = testApplication {
        val client = createClient { install(ContentNegotiation) { json() } }
        application {
            setupPlugins()
            install(Koin) {
                modules(
                    module {
                        factory<IChoreServiceUserRepository> {
                            mockk {
                                coEvery { getUserDetail(any()) } returns null
                                coEvery { createUser(any()) } returns null
                                coEvery { updateUser(any()) } returns null
                            }
                        }
                    }
                )
            }
            routing { route("test") { userRoutes() } }
        }

        // Expected to give a 404 due to all requests returning null
        assertEquals(HttpStatusCode.NotFound, client.get("test/user/1").status)
        assertEquals(
            HttpStatusCode.NotFound,
            client
                .post("test/user") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        UserDetailCreate(
                            name = "Test",
                            type = UserType.MANAGER,
                            contactInfo = null,
                            createdBy = 0
                        )
                    )
                }
                .status
        )
        assertEquals(
            HttpStatusCode.NotFound,
            client
                .put("test/user") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        UserDetailUpdate(
                            id = 1,
                            name = "Test",
                            type = UserType.MANAGER,
                            contactInfo = null,
                            updatedBy = 0
                        )
                    )
                }
                .status
        )
    }

    @Test
    fun `testUserRoutes dataFound`() = testApplication {
        val client = createClient { install(ContentNegotiation) { json() } }
        application {
            setupPlugins()
            install(Koin) {
                modules(
                    module {
                        factory<IChoreServiceUserRepository> {
                            mockk {
                                coEvery { getUserDetail(any()) } returns TestData.defaultUser
                                coEvery { createUser(any()) } returns TestData.defaultUser
                                coEvery { updateUser(any()) } returns TestData.defaultUser
                            }
                        }
                    }
                )
            }
            routing { route("test") { userRoutes() } }
        }

        // Expected to give a 404 due to all requests returning null
        assertEquals(HttpStatusCode.OK, client.get("test/user/1").status)
        assertEquals(
            HttpStatusCode.OK,
            client
                .post("test/user") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        UserDetailCreate(
                            name = "Test",
                            type = UserType.MANAGER,
                            contactInfo = null,
                            createdBy = 0
                        )
                    )
                }
                .status
        )
        assertEquals(
            HttpStatusCode.OK,
            client
                .put("test/user") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        UserDetailUpdate(
                            id = 1,
                            name = "Test",
                            type = UserType.MANAGER,
                            contactInfo = null,
                            updatedBy = 0
                        )
                    )
                }
                .status
        )
    }
}
