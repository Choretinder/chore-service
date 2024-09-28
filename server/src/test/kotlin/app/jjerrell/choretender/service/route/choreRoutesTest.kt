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

import app.jjerrell.choretender.service.setupPlugins
import app.jjerrell.choretender.service.util.TestData
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.unmockkAll
import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.After
import org.koin.core.context.stopKoin

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
            routing { route("test") { choreRoutes() } }
        }

        val getChoresResponse = client.get("test/family/a/chore")
        assertEquals(HttpStatusCode.BadRequest, getChoresResponse.status)
        assertEquals("Missing or invalid ID", getChoresResponse.bodyAsText())

        val getChoreInvalidFamilyResponse = client.get("test/family/a/chore/a")
        assertEquals(HttpStatusCode.BadRequest, getChoreInvalidFamilyResponse.status)
        assertEquals("Missing or invalid Family ID", getChoreInvalidFamilyResponse.bodyAsText())

        val getChoreInvalidChoreResponse = client.get("test/family/1/chore/a")
        assertEquals(HttpStatusCode.BadRequest, getChoreInvalidChoreResponse.status)
        assertEquals("Missing or invalid Chore ID", getChoreInvalidChoreResponse.bodyAsText())

        val createChoreResponse = client.post("test/family/a/chore")
        assertEquals(HttpStatusCode.BadRequest, createChoreResponse.status)
        assertEquals("Missing or invalid ID", createChoreResponse.bodyAsText())

        val updateChoreResponse = client.post("test/family/a/chore/update")
        assertEquals(HttpStatusCode.BadRequest, updateChoreResponse.status)
        assertEquals("Missing or invalid ID", updateChoreResponse.bodyAsText())
    }

    @Test
    fun `testChoreRoutes badRequest invalidBody`() = testApplication {
        application {
            setupPlugins()
            routing { route("test") { choreRoutes() } }
        }

        val createChoreResponse = client.post("test/family/1/chore")
        assertEquals(HttpStatusCode.BadRequest, createChoreResponse.status)
        assertEquals("Missing or invalid request body", createChoreResponse.bodyAsText())

        val updateChoreResponse = client.post("test/family/1/chore/update")
        assertEquals(HttpStatusCode.BadRequest, updateChoreResponse.status)
        assertEquals("Missing or invalid request body", updateChoreResponse.bodyAsText())
    }

    @Test
    fun `testChoreRoutes serverError missingDependency`() = testApplication {
        val client = createClient { install(ContentNegotiation) { json() } }
        application {
            setupPlugins()
            routing { route("test") { choreRoutes() } }
        }

        val getChoresResponse = client.get("test/family/1/chore")
        assertEquals(HttpStatusCode.InternalServerError, getChoresResponse.status)

        val getChoreInvalidFamilyResponse = client.get("test/family/1/chore/1")
        assertEquals(HttpStatusCode.InternalServerError, getChoreInvalidFamilyResponse.status)

        val createChoreResponse =
            client.post("test/family/1/chore") {
                contentType(ContentType.Application.Json)
                setBody(TestData.choreDetailCreate)
            }
        assertEquals(HttpStatusCode.InternalServerError, createChoreResponse.status)

        val updateChoreResponse =
            client.post("test/family/1/chore/update") {
                contentType(ContentType.Application.Json)
                setBody(TestData.choreDetailUpdate)
            }
        assertEquals(HttpStatusCode.InternalServerError, updateChoreResponse.status)
    }
}
