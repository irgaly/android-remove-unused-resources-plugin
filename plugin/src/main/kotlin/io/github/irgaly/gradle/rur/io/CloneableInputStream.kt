package io.github.irgaly.gradle.rur.io

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * this is not thread safe
 */
class CloneableInputStream(private val input: InputStream): InputStream() {
    private val originalOutputStream = ArrayDequeOutputStream()
    private val originalInputStream = ArrayDequeInputStream(originalOutputStream)
    private val forks = mutableSetOf(originalOutputStream)

    fun fork(): InputStream {
        val output = ArrayDequeOutputStream(
            originalOutputStream.buffer.toTypedArray()
        )
        forks.add(output)
        return ArrayDequeInputStream(output)
    }

    override fun read(): Int {
        return originalInputStream.read()
    }

    override fun read(bytes: ByteArray, offset: Int, length: Int): Int {
        return originalInputStream.read(bytes, offset, length)
    }

    override fun available(): Int {
        return originalInputStream.available()
    }

    override fun close() {
        input.close()
    }

    private class ArrayDequeOutputStream(initial: Array<Int> = emptyArray()): OutputStream() {
        val buffer = ArrayDeque<Int>().apply {
            addAll(initial)
        }
        var closed = false
        override fun write(byte: Int) {
            if (closed) {
                throw IOException("already closed")
            }
            buffer.add(byte)
        }

        override fun close() {
            closed = true
            buffer.clear()
        }
    }

    private inner class ArrayDequeInputStream(private val source: ArrayDequeOutputStream): InputStream() {
        private val buffer = source.buffer
        private var closed = false
        override fun read(): Int {
            if (closed) {
                throw IOException("already closed")
            }
            return if (buffer.isNotEmpty()) {
                buffer.removeFirst()
            } else {
                val byte = input.read()
                applyForks {
                    it.write(byte)
                }
                return byte
            }
        }

        override fun read(bytes: ByteArray, offset: Int, length: Int): Int {
            if (closed) {
                throw IOException("already closed")
            }
            return if (buffer.isEmpty()) {
                val read = input.read(bytes, offset, length)
                if (0 < read) {
                    applyForks {
                        it.write(bytes, offset, read)
                    }
                }
                read
            } else {
                var index = offset
                var read = 0
                while (read < length && buffer.isNotEmpty()) {
                    bytes[index] = buffer.removeFirst().toByte()
                    read++
                    index++
                }
                if (read < length) {
                    val inputRead = input.read(bytes, index, length - read)
                    if (0 < inputRead) {
                        read += inputRead
                        applyForks {
                            it.write(bytes, index, inputRead)
                        }
                    }
                }
                read
            }
        }

        override fun available(): Int {
            return (buffer.size + input.available())
        }

        override fun close() {
            closed = true
            forks.remove(source)
        }

        private fun applyForks(block: (OutputStream) -> Unit) {
            forks.forEach {
                if (it != source) {
                    block(it)
                }
            }
        }
    }
}
