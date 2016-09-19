package ttldtor

import org.slf4j.LoggerFactory
import java.sql.Connection

infix fun <T : Connection, R> T.useWithTx(block: (T) -> R): R {
    val log = LoggerFactory.getLogger(this.javaClass)
    var closed = false;

    try {
        return block(this)
    } catch (e: Exception) {
        closed = true

        if (!autoCommit) {
            rollback()
        }

        try {
            close()
        } catch (onClose: Exception) {
            log.error("onClose: ", onClose)
        }

        throw e
    } finally {
        if (!closed) {
            if (!autoCommit) {
                commit()
            }

            close()
        }
    }
}

infix fun <T : AutoCloseable, R> T.use(block: (T) -> R): R {
    val log = LoggerFactory.getLogger(this.javaClass)
    var closed = false

    try {
        return block(this)
    } catch (e: Exception) {
        closed = true

        try {
            close()
        } catch (onClose: Exception) {
            log.error("onClose: ", onClose)
        }

        throw e
    } finally {
        if (!closed) {
            close()
        }
    }
}

class ResourceHolder : AutoCloseable {
    val resources = arrayListOf<AutoCloseable>()

    fun <T : AutoCloseable> T.autoClose(): T {
        resources.add(this)
        return this
    }

    override fun close() {
        resources.reversed().forEach { it.close() }
    }
}

fun <R> using(block: ResourceHolder.() -> R): R {
    val holder = ResourceHolder()
    try {
        return holder.block()
    } finally {
        holder.close()
    }
}

