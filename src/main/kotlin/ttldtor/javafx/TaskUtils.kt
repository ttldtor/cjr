package ttldtor.javafx

import javafx.application.Platform
import javafx.concurrent.Task

fun <T> task(func: () -> T) = object : Task<T>() {
    override fun call(): T {
        return func()
    }
}.apply {
    setOnFailed({ Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), exception) })
    Thread(this).start()
}

infix fun <T> Task<T>.success(func: (T) -> Unit): Task<T> {
    Platform.runLater {
        setOnSucceeded { func(value) }
    }

    return this
}

fun <T> runAsync(func: () -> T) = task(func)

infix fun <T> Task<T>.ui(func: (T) -> Unit) = success(func)