package io.github.irgaly.gradle.rur.xml

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.io.StringWriter

class OriginalCharactersStaxXmlParserTest: DescribeSpec({
    val xml = """
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE resources [
<!ENTITY valid "VALID">
]>
    <!-- header comment1 -->
<resources xmlns:tools="http://schemas.android.com/tools">
    <!-- comment1 -->
    <element1>element1</element1>
    <element2>test<!-- comment2 -->test</element2>
    <element3/>
    <empty></empty>
<element4>4</element4><element5>5</element5>
<cdata>abc<![CDATA[def]]>def</cdata>
<refs>test©&#169;test&valid;&invalid;</refs>
<refs>test%ref;test</refs>
<amp>test&amp;#169;test</amp>
<attrs name="attr >">attr ></attrs>
<attrs tools:override="true"></attrs>
<emoji>👨🏻‍🦱</emoji>
<surrogate>&#x1F6AD;</surrogate>
<surrogate>a🚭a🚭a</surrogate>
<ivs>${
        ubyteArrayOf(0xE9U, 0x82U, 0x8AU, 0xF3U, 0xA0U, 0x84U, 0x80U).toByteArray()
            .toString(Charsets.UTF_8)
    }</ivs>
</resources>
    <!-- footer comment1 -->
    <!-- footer comment2 -->
    
    """.trimIndent()
    describe("stax parser") {
        it("parsed xml is same as input") {
            val parser = OriginalCharactersStaxXmlParser(xml.byteInputStream())
            val output1 = StringWriter()
            val output2 = StringWriter()
            while (parser.hasNext()) {
                val event = parser.nextEvent()
                output1.append(xml.substring(event.originalLocation))
                output2.append(event.originalText)
            }
            output1.toString() shouldBe xml
            output2.toString() shouldBe xml
        }
    }
})
