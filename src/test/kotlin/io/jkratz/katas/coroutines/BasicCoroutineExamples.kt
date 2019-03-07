package io.jkratz.katas.coroutines

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis
import kotlin.test.Test

class BasicCoroutineExamples {

    @Test
    fun synchronousCode() {
        println("one Thread: ${Thread.currentThread().name}")
        println("two Thread: ${Thread.currentThread().name}")
        println("three Thread: ${Thread.currentThread().name}")
    }

    @Test
    fun exampleBlocking() {
        println("one Thread: ${Thread.currentThread().name}")
        printLineSleep() // Blocking call
        println("three Thread: ${Thread.currentThread().name}")
    }

    @Test
    fun exampleDelay() {
        println("one Thread: ${Thread.currentThread().name}")
        runBlocking {
            printLineDelay() // Non blocking code
        }
        println("three Thread: ${Thread.currentThread().name}")
    }

    /**
     * Similar to above, but more idiomatic code
     */
    @Test
    fun exampleDelay2() = runBlocking {
        println("one Thread: ${Thread.currentThread().name}")
        printLineDelay() // Non blocking code
        println("three Thread: ${Thread.currentThread().name}")
    }

    @Test
    fun exampleBlockingDispatcher() {
        runBlocking(Dispatchers.Default) {
            println("1 - Thread: ${Thread.currentThread().name}")
            printLineDelay()
        }
        println("3 - Thread: ${Thread.currentThread().name}")
    }

    /**
     * The second line containing 2 will not print as the application finishes
     * execution before the delay finishes
     */
    @Test
    fun exampleLaunchGlobal() = runBlocking {
        println("1 - Thread: ${Thread.currentThread().name}")

        // This is NON blocking
        GlobalScope.launch {
            printLineDelay()
        }

        println("3 - Thread: ${Thread.currentThread().name}")
    }

    @Test
    fun exampleLaunchGlobalWaiting() = runBlocking {
        println("1 - Thread: ${Thread.currentThread().name}")

        // This is NON blocking
        val j = GlobalScope.launch {
            printLineDelay()
        }

        println("3 - Thread: ${Thread.currentThread().name}")
        j.join() // waits for job to finish
    }

    /**
     * Since we are using the local Coroutine scope instead of the global scope
     * we don't have to call join on the job as it will wait by default since
     * a job isn't considered complete until all children are complete
     */
    @Test
    fun exampleLaunchCoroutineScope() = runBlocking {
        println("1 - Thread: ${Thread.currentThread().name}")

        // This is NON blocking
        val j = launch { // NOTE: It is possible to pass own Dispatcher of Executor here as well
            printLineDelay()
        }

        println("3 - Thread: ${Thread.currentThread().name}")
    }

    @Test
    fun exampleAsyncAwait() = runBlocking {
        val time = measureTimeMillis {
            val deferred1 = async { calculateHardThings(10) }
            val deferred2 = async { calculateHardThings(20) }
            val deferred3 = async { calculateHardThings(30) }

            val sum = deferred1.await() + deferred2.await() + deferred3.await()
            println(sum)
        }
        println(time)
    }

    @Test
    fun exampleWithContext() = runBlocking {
        val time = measureTimeMillis {
            val result1 = withContext(Dispatchers.Default) { calculateHardThings(10) }
            val result2 = withContext(Dispatchers.Default) { calculateHardThings(result1) }
            val result3 = withContext(Dispatchers.Default) { calculateHardThings(result2) }

            val sum = result1 + result2 + result3
            println(sum)
        }
        println(time)
    }

    fun printLineSleep() {
        Thread.sleep(3000) // Blocking: Thread is blocked until the sleep time ends
        println("2 - Thread: ${Thread.currentThread().name}")
    }

    suspend fun printLineDelay() {
        delay(3000) // This does NOT block the thread, it suspends it
        println("2 - Thread: ${Thread.currentThread().name}")
    }

    // Simulate long running calculation
    suspend fun calculateHardThings(startNum: Int): Int {
        delay(2000)
        return startNum * 10
    }
}
