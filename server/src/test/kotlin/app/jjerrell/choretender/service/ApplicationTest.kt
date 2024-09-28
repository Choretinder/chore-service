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

import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlin.test.assertEquals
import kotlinx.serialization.Serializable
import org.junit.Test

class ApplicationTest {
    @Test
    fun `testApplicationSetup ignoreTrailingSlash`() = testApplication {
        application {
            setupPlugins()
            routing { get("v1/test") { call.respondText("Hello Tester!") } }
        }
        val response = client.get("v1/test/")
        assertEquals("Hello Tester!", response.bodyAsText())
    }

    @Test
    fun `testApplicationSetup contentNegotiation`() = testApplication {
        val client = createClient { install(ContentNegotiation) { json() } }
        application {
            setupPlugins()
            routing { get("v1/testJson") { call.respond(TestJsonData()) } }
        }
        val response = client.get("v1/testJson")
        val responseBody = response.body<TestJsonData>()

        assertEquals("Hello with JSON!", responseBody.name)
    }

    @Serializable private data class TestJsonData(val name: String = "Hello with JSON!")
}
