package net.shilu.kbots

import net.shilu.kbots.routes.Route
import net.shilu.kbots.utilities.JsonMapper

class KBotsClient(token: String, private val botId: Long) {
    init {
        Route.token = token
    }

    suspend fun updateGuildCount(guildCount: Int, shardCount: Int = 1) {
        if (shardCount < 1)
            throw IllegalArgumentException("Shard Count cannot be less than 1. Did you submit Shard ID?")
        if (guildCount < 0)
            throw IllegalArgumentException("Guild Count cannot be less than 0")

        Route.GUILD_COUNT_UPDATE.params(this.botId)
            .request {
                this.body = JsonMapper.EMPTY_OBJECT
                    .apply {
                        this.put("servers", "$guildCount")
                        this.put("shards", "$shardCount")
                    }.format()
            }
    }
}
