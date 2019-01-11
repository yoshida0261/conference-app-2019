package io.github.droidkaigi.confsched2019.dispatcher

import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class CurrentThreadExecutorService : ThreadPoolExecutor(
    0,
    1,
    0L,
    TimeUnit.SECONDS,
    SynchronousQueue<Runnable>(),
    ThreadPoolExecutor.CallerRunsPolicy()
) {
    override fun execute(command: Runnable?) {
        rejectedExecutionHandler.rejectedExecution(command, this)
    }

    override fun finalize() {
        shutdown()
    }
}
