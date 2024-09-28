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

class FamilyRoutesTest {
    @After
    fun resetMocks() {
        unmockkAll()
        stopKoin()
    }

    @Test
    fun `testFamilyRoutes badRequest missingId`() = testApplication {
        application {
            setupPlugins()
            routing { route("test") { familyRoutes() } }
        }

        // Expected to match the {familyId} sub route but fail because 'a' cannot be cast to a Long
        val getIdResponse = client.get("test/family/a")
        assertEquals(HttpStatusCode.BadRequest, getIdResponse.status)
        assertEquals("Missing or invalid ID", getIdResponse.bodyAsText())

        val postIdInviteResponse = client.post("test/family/a/invite")
        assertEquals(HttpStatusCode.BadRequest, postIdInviteResponse.status)
        assertEquals("Missing or invalid ID", postIdInviteResponse.bodyAsText())

        val postIdLeaveResponse = client.post("test/family/a/leave")
        assertEquals(HttpStatusCode.BadRequest, postIdLeaveResponse.status)
        assertEquals("Missing or invalid ID", postIdLeaveResponse.bodyAsText())

        val postIdVerifyResponse = client.post("test/family/a/verify")
        assertEquals(HttpStatusCode.BadRequest, postIdVerifyResponse.status)
        assertEquals("Missing or invalid ID", postIdVerifyResponse.bodyAsText())

        val postIdRoleResponse = client.post("test/family/a/role")
        assertEquals(HttpStatusCode.BadRequest, postIdRoleResponse.status)
        assertEquals("Missing or invalid ID", postIdRoleResponse.bodyAsText())
    }

    @Test
    fun `testFamilyRoutes badRequest invalidBody`() = testApplication {
        application {
            setupPlugins()
            routing { route("test") { familyRoutes() } }
        }

        val familyCreateResponse = client.post("test/family")
        assertEquals(HttpStatusCode.BadRequest, familyCreateResponse.status)
        assertEquals("Missing or invalid request body", familyCreateResponse.bodyAsText())

        // Expected to match the {familyId} sub route but fail due to a missing request body
        val postIdInviteResponse = client.post("test/family/1/invite")
        assertEquals(HttpStatusCode.BadRequest, postIdInviteResponse.status)
        assertEquals("Missing or invalid request body", postIdInviteResponse.bodyAsText())

        val postIdLeaveResponse = client.post("test/family/1/leave")
        assertEquals(HttpStatusCode.BadRequest, postIdLeaveResponse.status)
        assertEquals("Missing or invalid request body", postIdLeaveResponse.bodyAsText())

        val postIdVerifyResponse = client.post("test/family/1/verify")
        assertEquals(HttpStatusCode.BadRequest, postIdVerifyResponse.status)
        assertEquals("Missing or invalid request body", postIdVerifyResponse.bodyAsText())

        val postIdRoleResponse = client.post("test/family/1/role")
        assertEquals(HttpStatusCode.BadRequest, postIdRoleResponse.status)
        assertEquals("Missing or invalid request body", postIdRoleResponse.bodyAsText())
    }

    @Test
    fun `testFamilyRoutes serverError missingDependency`() = testApplication {
        val client = createClient { install(ContentNegotiation) { json() } }
        application {
            setupPlugins()
            routing { route("test") { familyRoutes() } }
        }

        // Expected to fail with a server error because DI isn't configured
        val createFamilyResponse =
            client.post("test/family") {
                contentType(ContentType.Application.Json)
                setBody(TestData.familyDetailCreate)
            }
        assertEquals(HttpStatusCode.InternalServerError, createFamilyResponse.status)

        val getIdResponse = client.get("test/family/1")
        assertEquals(HttpStatusCode.InternalServerError, getIdResponse.status)

        val inviteToFamilyResponse =
            client.post("test/family/1/invite") {
                contentType(ContentType.Application.Json)
                setBody(TestData.familyDetailInvite)
            }
        assertEquals(HttpStatusCode.InternalServerError, inviteToFamilyResponse.status)

        val leaveFamilyResponse =
            client.post("test/family/1/leave") {
                contentType(ContentType.Application.Json)
                setBody(TestData.familyDetailLeave)
            }
        assertEquals(HttpStatusCode.InternalServerError, leaveFamilyResponse.status)

        val verifyMemberResponse =
            client.post("test/family/1/verify") {
                contentType(ContentType.Application.Json)
                setBody(TestData.familyMemberVerify)
            }
        assertEquals(HttpStatusCode.InternalServerError, verifyMemberResponse.status)

        val promoteMemberResponse =
            client.post("test/family/1/role") {
                contentType(ContentType.Application.Json)
                setBody(TestData.familyMemberPromote)
            }
        assertEquals(HttpStatusCode.InternalServerError, promoteMemberResponse.status)
    }
}
