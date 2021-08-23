package net.shilu.kbots

@Suppress("unused")
class KBotsClientBuilder {
    private var token: String? = null
    private var botId: Long? = null

    fun setToken(token: String): KBotsClientBuilder {
        this.token = token
        return this
    }

    fun setBotId(botId: Long): KBotsClientBuilder {
        this.botId = botId
        return this
    }

    fun build(): KBotsClient {
        return KBotsClient(
            this.require(this.token, "Koreanbots token"),
            this.require(this.botId, "Discord Bot ID")
        )
    }

    private fun <T> require(prop: T?, name: String): T {
        if (prop == null) {
            throw IllegalStateException("$name is not provided. Cannot build the client")
        }
        return prop
    }
}