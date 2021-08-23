package net.shilu.kbots.routes

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Instant
import java.util.concurrent.ConcurrentLinkedQueue

internal class RateLimiter {
    private val mutex = Mutex()
    private val requestQueue = ConcurrentLinkedQueue<suspend () -> Unit>()
    private var nextReset: Long = -1L

    internal fun setRateLimit(nextReset: Long) {
        this.nextReset = nextReset
    }

    private suspend fun await() {
        delay((this.nextReset - Instant.now().epochSecond) * 1000L)
    }

    private fun isRateLimited(): Boolean {
        return Instant.now().epochSecond < this.nextReset
    }

    internal suspend fun enqueue(invoke: suspend () -> Unit) {
        this.requestQueue.offer(invoke)
        this.mutex.withLock {
            if (this.isRateLimited()) {
                this.await()
            }
            this.requestQueue.poll().invoke()
        }
    }

    companion object {
        const val RESET_HEADER = "x-ratelimit-reset"
        const val REMAINING_HEADER = "x-ratelimit-remaining"
    }
}
