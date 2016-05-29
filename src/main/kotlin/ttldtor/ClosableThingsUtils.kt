package ttldtor

fun <T : AutoCloseable, R> T.use(block: (T) -> R): R {
    var closed = false

    try {
        return block(this)
    } catch (e: Exception) {
        closed = true

        try {
            close()
        } catch (_: Exception) {}

        throw e
    } finally {
        if (!closed) {
            close()
        }
    }
}

