
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.shilu.kbots.KBotsClientBuilder
import java.util.concurrent.Executors

val scope = CoroutineScope(Executors.newSingleThreadExecutor().asCoroutineDispatcher())

fun main() {
    val client = KBotsClientBuilder()
        .setToken("")
        .setBotId(-1L)
        .build()
    val jobs = mutableListOf<Job>()
    runBlocking {
        repeat(7) {
            scope.launch {
                client.updateGuildCount(0, 0)
            }.let { jobs.add(it) }
        }
        jobs.forEach { it.join() }
    }
}
