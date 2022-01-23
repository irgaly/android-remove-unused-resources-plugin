package io.github.irgaly.gradle.rur.xml

import io.kotest.core.spec.style.DescribeSpec

class OriginalCharactersStaxXmlParserTest: DescribeSpec({
    val xml = """
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE resources [
<!ENTITY valid "VALID">
]>
    <!-- header comment1 -->
<resources>
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
<emoji>👨🏻‍🦱</emoji>
<surrogate>&#x1F6AD;</surrogate>
<surrogate>a🚭a🚭a</surrogate>
<ivs>${ubyteArrayOf(0xE9U, 0x82U, 0x8AU, 0xF3U, 0xA0U, 0x84U, 0x80U).toByteArray().toString(Charsets.UTF_8)}</ivs>
</resources>
    <!-- footer comment1 -->
    <!-- footer comment2 -->
    """.trimIndent()
    val eventMap = mapOf(
        1 to "START_ELEMENT",
        2 to "END_ELEMENT",
        3 to "PROCESSING_INSTRUCTION",
        4 to "CHARACTERS",
        5 to "COMMENT",
        6 to "SPACE",
        7 to "START_DOCUMENT",
        8 to "END_DOCUMENT",
        9 to "ENTITY_REFERENCE",
        10 to "ATTRIBUTE",
        11 to "DTD",
        12 to "CDATA",
        13 to "NAMESPACE",
        14 to "NOTATION_DECLARATION",
        15 to "ENTITY_DECLARATION"
    )
    describe("stax parser") {
        it("simple") {
            val parser = OriginalCharactersStaxXmlParser(xml.byteInputStream())
            while(parser.hasNext()) {
                val event = parser.nextEvent()
                if(event.event.isEndDocument) {
                    println("event ${eventMap[event.event.eventType]} ${event.originalLocation}:|${event.event}||${event.originalText}|")
                } else {
                    println("event ${eventMap[event.event.eventType]} ${event.originalLocation}:|${event.event}|${xml.substring(event.originalLocation)}|${event.originalText}|")
                }
            }
        }
    }
})
