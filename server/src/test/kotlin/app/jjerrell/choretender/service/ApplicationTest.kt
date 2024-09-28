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
import kotlinx.serialization.Serializable
import org.junit.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun `testApplicationSetup ignoreTrailingSlash`() = testApplication {
        application {
            setupPlugins()
            routing {
                get("v1/test") {
                    call.respondText("Hello Tester!")
                }
            }
        }
        val response = client.get("v1/test/")
        assertEquals("Hello Tester!", response.bodyAsText())
    }

    @Test
    fun `testApplicationSetup contentNegotiation`() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        application {
            setupPlugins()
            routing {
                get("v1/testJson") {
                    call.respond(TestJsonData())
                }
            }
        }
        val response = client.get("v1/testJson")
        val responseBody = response.body<TestJsonData>()

        assertEquals("Hello with JSON!", responseBody.name)
    }

    @Serializable
    private data class TestJsonData(
        val name: String = "Hello with JSON!"
    )
}