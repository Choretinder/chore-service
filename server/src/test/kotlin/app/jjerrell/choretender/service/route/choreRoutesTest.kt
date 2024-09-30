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

import app.jjerrell.choretender.service.domain.model.chore.ChoreDetailRead
import app.jjerrell.choretender.service.domain.repository.IChoreServiceChoreRepository
import app.jjerrell.choretender.service.setupPlugins
import app.jjerrell.choretender.service.setupRouting
import app.jjerrell.choretender.service.util.TestData
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
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

class ChoreRoutesTest {
    @After
    fun resetMocks() {
        unmockkAll()
        stopKoin()
    }

    @Test
    fun `testChoreRoutes badRequest missingId`() = testApplication {
        application {
            setupPlugins()
            setupRouting()
        }

        val getChoresResponse = client.get("v1/family/a/chore")
        assertEquals(HttpStatusCode.BadRequest, getChoresResponse.status)
        assertEquals("Missing or invalid ID", getChoresResponse.bodyAsText())

        val getChoreInvalidFamilyResponse = client.get("v1/family/a/chore/a")
        assertEquals(HttpStatusCode.BadRequest, getChoreInvalidFamilyResponse.status)
        assertEquals("Missing or invalid Family ID", getChoreInvalidFamilyResponse.bodyAsText())

        val getChoreInvalidChoreResponse = client.get("v1/family/1/chore/a")
        assertEquals(HttpStatusCode.BadRequest, getChoreInvalidChoreResponse.status)
        assertEquals("Missing or invalid Chore ID", getChoreInvalidChoreResponse.bodyAsText())

        val createChoreResponse = client.post("v1/family/a/chore")
        assertEquals(HttpStatusCode.BadRequest, createChoreResponse.status)
        assertEquals("Missing or invalid ID", createChoreResponse.bodyAsText())

        val updateChoreResponse = client.post("v1/family/a/chore/update")
        assertEquals(HttpStatusCode.BadRequest, updateChoreResponse.status)
        assertEquals("Missing or invalid ID", updateChoreResponse.bodyAsText())
    }

    @Test
    fun `testChoreRoutes badRequest invalidBody`() = testApplication {
        application {
            setupPlugins()
            setupRouting()
        }

        val createChoreResponse = client.post("v1/family/1/chore")
        assertEquals(HttpStatusCode.BadRequest, createChoreResponse.status)
        assertEquals("Missing or invalid request body", createChoreResponse.bodyAsText())

        val updateChoreResponse = client.post("v1/family/1/chore/update")
        assertEquals(HttpStatusCode.BadRequest, updateChoreResponse.status)
        assertEquals("Missing or invalid request body", updateChoreResponse.bodyAsText())
    }

    @Test
    fun `testChoreRoutes serverError missingDependency`() = testApplication {
        val client = createClient { install(ContentNegotiation) { json() } }
        application {
            setupPlugins()
            setupRouting()
        }

        val getChoresResponse = client.get("v1/family/1/chore")
        assertEquals(HttpStatusCode.InternalServerError, getChoresResponse.status)

        val getChoreInvalidFamilyResponse = client.get("v1/family/1/chore/1")
        assertEquals(HttpStatusCode.InternalServerError, getChoreInvalidFamilyResponse.status)

        val createChoreResponse =
            client.post("v1/family/1/chore") {
                contentType(ContentType.Application.Json)
                setBody(TestData.choreDetailCreate)
            }
        assertEquals(HttpStatusCode.InternalServerError, createChoreResponse.status)

        val updateChoreResponse =
            client.post("v1/family/1/chore/update") {
                contentType(ContentType.Application.Json)
                setBody(TestData.choreDetailUpdate)
            }
        assertEquals(HttpStatusCode.InternalServerError, updateChoreResponse.status)
    }

    @Test
    fun `testChoreRoutes getAllChores`() = testApplication {
        val client = createClient { install(ContentNegotiation) { json() } }
        application {
            setupPlugins()
            install(Koin) {
                modules(
                    module {
                        factory<IChoreServiceChoreRepository> {
                            mockk {
                                coEvery { getFamilyChoreDetails(familyId = 1) } returns
                                    listOf(TestData.choreDetailReadOne, TestData.choreDetailReadTwo)
                            }
                        }
                    }
                )
            }
            setupRouting()
        }

        val getChoresResponse = client.get("v1/family/1/chore")
        assertEquals(HttpStatusCode.OK, getChoresResponse.status)
        assertEquals(
            listOf(TestData.choreDetailReadOne, TestData.choreDetailReadTwo),
            getChoresResponse.body<List<ChoreDetailRead>>()
        )
    }

    @Test
    fun `testChoreRoutes getChore`() = testApplication {
        val client = createClient { install(ContentNegotiation) { json() } }
        application {
            setupPlugins()
            install(Koin) {
                modules(
                    module {
                        factory<IChoreServiceChoreRepository> {
                            mockk {
                                coEvery { getChoreDetail(familyId = 1, choreId = 1) } returns
                                    TestData.choreDetailReadOne
                            }
                        }
                    }
                )
            }
            setupRouting()
        }

        val getChoreResponse = client.get("v1/family/1/chore/1")
        assertEquals(HttpStatusCode.OK, getChoreResponse.status)
        assertEquals(TestData.choreDetailReadOne, getChoreResponse.body<ChoreDetailRead>())
    }

    @Test
    fun `testChoreRoutes createChore`() = testApplication {
        val client = createClient { install(ContentNegotiation) { json() } }
        application {
            setupPlugins()
            install(Koin) {
                modules(
                    module {
                        factory<IChoreServiceChoreRepository> {
                            mockk {
                                coEvery {
                                    createChore(familyId = 1, detail = TestData.choreDetailCreate)
                                } returns TestData.choreDetailReadOne
                            }
                        }
                    }
                )
            }
            setupRouting()
        }

        val createChoreResponse =
            client.post("v1/family/1/chore") {
                contentType(ContentType.Application.Json)
                setBody(TestData.choreDetailCreate)
            }
        assertEquals(HttpStatusCode.OK, createChoreResponse.status)
        assertEquals(TestData.choreDetailReadOne, createChoreResponse.body<ChoreDetailRead>())
    }

    @Test
    fun `testChoreRoutes updateChore`() = testApplication {
        val client = createClient { install(ContentNegotiation) { json() } }
        application {
            setupPlugins()
            install(Koin) {
                modules(
                    module {
                        factory<IChoreServiceChoreRepository> {
                            mockk {
                                coEvery {
                                    updateChore(familyId = 1, detail = TestData.choreDetailUpdate)
                                } returns TestData.choreDetailReadOneUpdated
                            }
                        }
                    }
                )
            }
            setupRouting()
        }

        val updateChoreResponse =
            client.post("v1/family/1/chore/update") {
                contentType(ContentType.Application.Json)
                setBody(TestData.choreDetailUpdate)
            }
        assertEquals(HttpStatusCode.OK, updateChoreResponse.status)
        assertEquals(
            TestData.choreDetailReadOneUpdated,
            updateChoreResponse.body<ChoreDetailRead>()
        )
    }
}
