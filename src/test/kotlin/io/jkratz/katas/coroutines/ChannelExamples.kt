package io.jkratz.katas.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlin.test.Test

class ChannelExamples {

    @Test
    fun testBasicChannel() = runBlocking {
        val channel = Channel<Int>()
        launch {
            for (x in 1..5) channel.send(x * x)
        }

        repeat(5) {
            println(channel.receive())
        }

        println("Done")
    }

    @Test
    fun testBasicChannelWithDelay() = runBlocking {
        val channel = Channel<Int>()
        launch {
            for (x in 1..5) {
                delay(500)
                channel.send(x * x)
            }
        }

        repeat(5) {
            println(channel.receive())
        }

        println("Done")
    }

    @Test
    fun pipelinesExample() = runBlocking {
        val numbers = produceNumbers() // produces integers from 1 and on
        val squares = square(numbers) // squares integers
        for (i in 1..5) println(squares.receive()) // print first five
        println("Done!") // we are done
        coroutineContext.cancelChildren() // cancel children coroutines
    }

    fun CoroutineScope.produceNumbers() = produce<Int> {
        var x = 1
        while (true) send(x++) // infinite stream of integers starting from 1
    }

    fun CoroutineScope.square(numbers: ReceiveChannel<Int>): ReceiveChannel<Int> = produce {
        for (x in numbers) send(x * x)
    }
}