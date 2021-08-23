package net.shilu.kbots.routes

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets
import java.time.Instant

internal class CompiledRoute(private val baseRoute: Route, private val compiledEndpoint: String) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    internal suspend fun request(builder: HttpRequestBuilder.() -> Unit = {}) {
        this.logger.debug("Stacked the request to '${this.compiledEndpoint}'")
        this.baseRoute.rateLimiter.enqueue { this.process(builder) }
    }

    private suspend fun process(builder: HttpRequestBuilder.() -> Unit = {}) {
        val httpClient = HttpClient(CIO) { this.expectSuccess = false }
        httpClient.use { client ->
            this.logger.debug("Requesting to '${this.compiledEndpoint}'")
            val response = client.request<HttpResponse>(Route.BASE_URL + this.compiledEndpoint) {
                this.apply(builder)
                this.method = this@CompiledRoute.baseRoute.method
                if (this@CompiledRoute.baseRoute.auth) {
                    this.header("Authorization", Route.token)
                }
            }

            if (!response.status.isSuccess()) {
                this.logger.debug("Request to '${this.compiledEndpoint}' failed with status code " +
                        "${response.status.value}\nContent: ${response.readText(StandardCharsets.UTF_8)}")
            } else {
                this.logger.info("Successfully called '${this.compiledEndpoint}'")
            }

            if (response.headers[RateLimiter.REMAINING_HEADER].contentEquals("0")) {
                val resetTimestamp = response.headers[RateLimiter.RESET_HEADER]!!.toLong()
                this.baseRoute.rateLimiter.setRateLimit(resetTimestamp)
                this.logger.debug("RateLimit: No requests can be made within " +
                            "${resetTimestamp - Instant.now().epochSecond} seconds")
            }
        }
    }
}
