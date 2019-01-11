package io.github.droidkaigi.confsched2019.dispatcher

import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.ext.android.Dispatchers
import io.github.droidkaigi.confsched2019.model.SessionContents
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.first
import kotlinx.coroutines.channels.map
import kotlinx.coroutines.channels.take
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.hasItems
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertThat
import org.junit.Test

class DispatcherTest {
    @Test fun sendAndReceive() {
        val sessionContents: SessionContents = mockk()
        val dispatcher = Dispatcher()

        runBlocking {
            val allSessionLoaded = async {
                dispatcher.subscribe<Action.SessionContentsLoaded>().receive()
            }
            val job = launch {
                dispatcher.dispatch(Action.SessionContentsLoaded(sessionContents))
            }
            job.join()
            assertThat(allSessionLoaded.await().sessionContents, `is`(sessionContents))
        }
    }

    @Test fun sendAndMultipleReceive() {
        val sessionContents: SessionContents = mockk()
        val dispatcher = Dispatcher()


        runBlocking {
            val allSessionLoaded1 = async {
                dispatcher.subscribe<Action.SessionContentsLoaded>().receive()
            }
            val allSessionLoaded2 = async {
                dispatcher.subscribe<Action.SessionContentsLoaded>().receive()
            }
            val job = launch {
                dispatcher.dispatch(Action.SessionContentsLoaded(sessionContents))
            }
            job.join()
            assertThat(allSessionLoaded1.await().sessionContents, `is`(sessionContents))
            assertThat(allSessionLoaded2.await().sessionContents, `is`(sessionContents))
        }
    }

    @Test fun multipleSendAndReceive() {
        val sessionContents1: SessionContents = mockk()
        val sessionContents2: SessionContents = mockk()
        val dispatcher = Dispatcher()


        runBlocking {
            val allSessionLoaded1 = async {
                dispatcher.subscribe<Action.SessionContentsLoaded>().take(2).toList()
            }
            launch {
                dispatcher.dispatch(Action.SessionContentsLoaded(sessionContents1))
            }
            launch {
                dispatcher.dispatch(Action.SessionContentsLoaded(sessionContents2))
            }
            assertThat(
                allSessionLoaded1.await().map { it.sessionContents },
                `is`(listOf(sessionContents1, sessionContents2))
            )
        }
    }

    @Test fun multipleSendAndMultipleReceive() {
        val sessionContents1: SessionContents = mockk()
        val sessionContents2: SessionContents = mockk()
        val dispatcher = Dispatcher()


        runBlocking {
            val allSessionLoaded1 = async {
                dispatcher.subscribe<Action.SessionContentsLoaded>().map {
                    it
                }.take(2).toList()
            }
            val allSessionLoaded2 = async {
                dispatcher.subscribe<Action.SessionContentsLoaded>().map {
                    it
                }.take(2).toList()
            }
            launch {
                dispatcher.dispatch(Action.SessionContentsLoaded(sessionContents1))
            }
            launch {
                dispatcher.dispatch(Action.SessionContentsLoaded(sessionContents2))
            }
            assertThat(
                allSessionLoaded1.await().map { it.sessionContents },
                hasItems(sessionContents1, sessionContents2)
            )
            assertThat(
                allSessionLoaded2.await().map { it.sessionContents },
                hasItems(sessionContents1, sessionContents2)
            )
        }
    }

    @Test fun checkConcurrecyOfSendAndReceive() {
        val sessionContents: SessionContents = mockk()
        val dispatcher = Dispatcher()

        runBlocking {
            val action = Action.SessionContentsLoaded(sessionContents)
            val verifier = mockk<(Action.SessionContentsLoaded?) -> Unit>(relaxed = true)

            val job = launch(Dispatchers.Default) {
                delay(1000L)
                verifier(dispatcher.subscribe<Action.SessionContentsLoaded>().first())
            }

            dispatcher.dispatch(action)

            verify(exactly = 0) {
                verifier(any())
            }

            job.join()

            verify {
                verifier(action)
            }
        }
    }
}
