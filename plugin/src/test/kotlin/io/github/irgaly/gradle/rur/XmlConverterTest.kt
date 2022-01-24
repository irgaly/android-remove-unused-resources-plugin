package io.github.irgaly.gradle.rur

import io.github.irgaly.gradle.rur.xml.getAttributeValue
import io.github.irgaly.gradle.rur.xml.hasAttribute
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.io.StringWriter
import javax.xml.namespace.QName

class XmlConverterTest: DescribeSpec({
    describe("xml converter") {
        it ("remove tag") {
            val xml = """
<root>
    <element1 name="test">
        <element2/>
    </element1>
<element3 name="other"/>
</root>
    """.trimIndent()
            val converter = XmlConverter { startElementEvent ->
                val startElement = startElementEvent.event.asStartElement()
                startElement.hasAttribute("test")
                (startElement.getAttributeValue("name") == "test")
            }
            val output = StringWriter()
            converter.convert(xml.byteInputStream(), output)
            output.toString() shouldBe """
<root>
<element3 name="other"/>
</root>
            """.trimIndent()
        }
        it ("remove tag 1") {
            val xml = """
<root> <a/> 
</root>
    """.trimIndent()
            val converter = XmlConverter {
                it.event.asStartElement().name == QName("a")
            }
            val output = StringWriter()
            converter.convert(xml.byteInputStream(), output)
            output.toString() shouldBe """
<root>
</root>
            """.trimIndent()
        }
        it ("remove tag 1") {
            val xml = """
<root>
 <a/> <b/>
</root>
    """.trimIndent()
            val converter = XmlConverter {
                it.event.asStartElement().name == QName("a")
            }
            val output = StringWriter()
            converter.convert(xml.byteInputStream(), output)
            output.toString() shouldBe """
<root>
 <b/>
</root>
            """.trimIndent()
        }
        it ("remove tag 1") {
            val xml = """
<root> <a/> </root>
    """.trimIndent()
            val converter = XmlConverter {
                it.event.asStartElement().name == QName("a")
            }
            val output = StringWriter()
            converter.convert(xml.byteInputStream(), output)
            output.toString() shouldBe """
<root> </root>
            """.trimIndent()
        }
    }
})
