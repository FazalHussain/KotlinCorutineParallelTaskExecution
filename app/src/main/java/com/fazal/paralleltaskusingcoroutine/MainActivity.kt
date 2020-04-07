package com.fazal.paralleltaskusingcoroutine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //fakeApiCallWithAsyncAwait()
        fakeApiCallWithJob()

    }

    private fun fakeApiCallWithJob() {
        val startTime = System.currentTimeMillis()
        val parentJob = CoroutineScope(IO).launch {
            val job1 = launch {
                val time1 = measureTimeMillis {
                    println("debug: launching job1 in thread ${Thread.currentThread().name}")
                    runOnMainThread(getResultFromApi1())
                }
                println("debug: completed job1 in $time1 ms")
            }

            val job2 = launch {
                val time2 = measureTimeMillis {
                    println("debug: launching job2 in thread ${Thread.currentThread().name}")
                    runOnMainThread(getResultFromApi2())
                }
                println("debug: completed job2 in $time2 ms")
            }
        }

        parentJob.invokeOnCompletion {
            println("debug: total elapsed time: ${System.currentTimeMillis() - startTime}")
        }
    }

    private fun fakeApiCallWithAsyncAwait() {
        CoroutineScope(IO).launch {
            val executionTime = measureTimeMillis {
                val result1 : Deferred<String> = async {
                    println("debug: launching job1 in thread ${Thread.currentThread().name}")
                    getResultFromApi1()
                }

                val result2 : Deferred<String> = async {
                    println("debug: launching job2 in thread ${Thread.currentThread().name}")
                    getResultFromApi2()
                }

                runOnMainThread("Got ${result1.await()}")
                runOnMainThread(result2.await())
            }


            println("debug: total elasped time: $executionTime")
        }

    }

    suspend fun runOnMainThread(text: String) {
        withContext(Main) {
            println("debug: $text")
        }
    }

    private suspend fun getResultFromApi1(): String {
        delay(10000)
        return "Result 1"
    }

    private suspend fun getResultFromApi2(): String {
        delay(15000)
        return "Result 2"
    }
}
