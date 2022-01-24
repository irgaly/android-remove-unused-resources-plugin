package io.github.irgaly.gradle.rur

import io.github.irgaly.gradle.rur.xml.OriginalCharactersStaxXmlParser
import io.github.irgaly.gradle.rur.xml.XmlEvent
import java.io.InputStream
import java.io.Writer

class XmlConverter(
    val removeElementCondition: (startElementEvent: XmlEvent) -> Boolean
) {
    fun convert(input: InputStream, output: Writer): Result {
        val removed = mutableListOf<XmlEvent>()
        OriginalCharactersStaxXmlParser(input).use { parser ->
            var keep: XmlEvent? = null
            while (parser.hasNext()) {
                val event = parser.nextEvent()
                if (event.event.isStartElement) {
                    if (removeElementCondition(event)) {
                        removed.add(event)
                        // skip to end element
                        while (parser.hasNext()) {
                            val next = parser.nextEvent()
                            if (next.event.isEndElement && next.parent == event.parent) {
                                break
                            }
                        }
                        val trailing = if (parser.peek()?.isCharacters == true) {
                            val next = parser.nextEvent()
                            // trailing characters
                            // remove white spaces to line end
                            Regex("^\\h*").replace(next.originalText, "")
                        } else ""
                        val leading = keep?.originalText?.let {
                            if (trailing.isNotEmpty()) {
                                // leading characters
                                // remove white spaces in last line
                                Regex("(\r\n|\\v)?\\h*$").replace(it, "")
                            } else it
                        } ?: ""
                        keep = null
                        // write leading & trailing characters
                        output.write(leading)
                        output.write(trailing)
                        continue
                    }
                }
                if (keep != null) {
                    output.write(keep.originalText)
                    keep = null
                }
                if (event.event.isCharacters) {
                    // delay evaluate Characters event
                    keep = event
                } else {
                    output.write(event.originalText)
                }
            }
        }
        return Result(removed.toList())
    }

    data class Result(
        val removed: List<XmlEvent>
    )
}
