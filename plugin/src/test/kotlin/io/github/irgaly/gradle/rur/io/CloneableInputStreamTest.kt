package io.github.irgaly.gradle.rur.io

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

@Suppress("BlockingMethodInNonBlockingContext")
class CloneableInputStreamTest: DescribeSpec({
    describe("cloneable") {
        it("read all text") {
            val source = "The quick brown fox jumped over the lazy dog".byteInputStream()
            val cloneable = CloneableInputStream(source)
            val original = cloneable.reader()
            original.readText() shouldBe "The quick brown fox jumped over the lazy dog"
            cloneable.available() shouldBe 0
            cloneable.close()
        }
        it("text forks") {
            val source = "The quick brown fox jumped over the lazy dog".byteInputStream()
            val cloneable = CloneableInputStream(source)
            val original = cloneable.reader()
            val fork1 = cloneable.fork()
            original.readText() shouldBe "The quick brown fox jumped over the lazy dog"
            fork1.reader().readText() shouldBe "The quick brown fox jumped over the lazy dog"
            cloneable.available() shouldBe 0
            cloneable.close()
        }
        it("sequential forks") {
            val source = byteArrayOf(0, 1, 2, 3, 4, 5).inputStream()
            val cloneable = CloneableInputStream(source)
            val original = cloneable
            original.read() shouldBe 0
            val fork1 = cloneable.fork()
            original.read() shouldBe 1
            fork1.read() shouldBe 1
            val fork2 = cloneable.fork()
            original.read() shouldBe 2
            fork1.read() shouldBe 2
            fork2.read() shouldBe 2
        }
        it("complex sequential forks") {
            val source = byteArrayOf(0, 1, 2, 3, 4, 5).inputStream()
            val cloneable = CloneableInputStream(source)
            val original = cloneable
            original.read() shouldBe 0
            val fork1 = cloneable.fork()
            fork1.read() shouldBe 1
            val fork2 = cloneable.fork()
            fork1.read() shouldBe 2
            original.read() shouldBe 1
            fork2.read() shouldBe 1
            original.readAllBytes() shouldBe byteArrayOf(2, 3, 4, 5)
        }
        it("original leads forks") {
            val source = byteArrayOf(0, 1, 2, 3, 4, 5).inputStream()
            val cloneable = CloneableInputStream(source)
            val original = cloneable
            val fork1 = cloneable.fork()
            original.readAllBytes() shouldBe byteArrayOf(0, 1, 2, 3, 4, 5)
            fork1.readAllBytes() shouldBe byteArrayOf(0, 1, 2, 3, 4, 5)
        }
        it("forks leads original") {
            val source = byteArrayOf(0, 1, 2, 3, 4, 5).inputStream()
            val cloneable = CloneableInputStream(source)
            val original = cloneable
            val fork1 = cloneable.fork()
            val fork2 = cloneable.fork()
            fork1.readAllBytes() shouldBe byteArrayOf(0, 1, 2, 3, 4, 5)
            fork2.readAllBytes() shouldBe byteArrayOf(0, 1, 2, 3, 4, 5)
            original.readAllBytes() shouldBe byteArrayOf(0, 1, 2, 3, 4, 5)
        }
        it("readBytes") {
            val source = byteArrayOf(0, 1, 2, 3, 4, 5).inputStream()
            val cloneable = CloneableInputStream(source)
            val original = cloneable
            val fork1 = cloneable.fork()
            original.read() shouldBe 0
            fork1.readNBytes(2) shouldBe byteArrayOf(0, 1)
            original.readNBytes(2) shouldBe byteArrayOf(1, 2)
            fork1.readNBytes(100) shouldBe byteArrayOf(2, 3, 4, 5)
            original.readNBytes(100) shouldBe byteArrayOf(3, 4, 5)
            original.available() shouldBe 0
        }
    }
})
