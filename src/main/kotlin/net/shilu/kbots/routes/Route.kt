package net.shilu.kbots.routes

import io.ktor.http.*
import net.shilu.kbots.KBotsInfo

internal class Route(val method: HttpMethod, private val endpoint: String, val auth: Boolean) {
    internal val rateLimiter = RateLimiter()

    internal fun params(vararg parameter: Any): CompiledRoute {
        return CompiledRoute(this, this.endpoint.format(*parameter.map { "$it" }.toTypedArray()))
    }

    companion object {
        internal lateinit var token: String

        internal val BASE_URL = "https://koreanbots.dev/api/v%d/".format(KBotsInfo.API_VERSION)
        internal val GUILD_COUNT_UPDATE = Route(HttpMethod.Post, "bots/%s/stats", true)
    }
}
