package io.github.droidkaigi.confsched2019.dispatcher

import androidx.annotation.WorkerThread
import kotlinx.coroutines.asCoroutineDispatcher

object TestActionDispatcher {
    @WorkerThread
    operator fun invoke() = CurrentThreadExecutorService().asCoroutineDispatcher()
}
