package io.github.irgaly.gradle.rur.xml

import io.github.irgaly.gradle.rur.io.CloneableInputStream
import java.io.InputStream
import java.io.InputStreamReader
import javax.xml.stream.XMLEventReader
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamConstants.CHARACTERS
import javax.xml.stream.XMLStreamConstants.END_DOCUMENT
import javax.xml.stream.events.Characters

/**
 * preserve original characters XML StAX Parser
 */
class OriginalCharactersStaxXmlParser(input: InputStream) {
    private val originalReader: InputStreamReader
    private val reader: XMLEventReader

    init {
        val cloneable = CloneableInputStream(input)
        originalReader = cloneable.fork().reader()
        reader = XMLInputFactory.newInstance().apply {
            setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, false)
            setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false)
            setProperty("http://java.sun.com/xml/stream/properties/report-cdata-event", true)
        }.createXMLEventReader(cloneable)
    }

    private var currentOffset = 0

    fun hasNext(): Boolean {
        return reader.hasNext()
    }

    fun nextEvent(): XmlEvent {
        val event = reader.nextEvent()
        var range = when(event.eventType) {
            CHARACTERS -> {
                val e = event.asCharacters()
                val next = reader.peek()
                if (e.isSurrogate) {
                    (currentOffset until event.location.characterOffset)
                } else if (e.isCData) {
                    (currentOffset until event.location.characterOffset)
                } else if (next.isEndElement) {
                    (currentOffset until event.location.characterOffset-2)
                } else {
                    (currentOffset until event.location.characterOffset-1)
                }
            }
            else -> (currentOffset until event.location.characterOffset)
        }
        currentOffset = range.last + 1
        var text =
        if (event.eventType == END_DOCUMENT) {
            originalReader.readText().toCharArray()
        } else {
            CharArray(range.count()).also{
                originalReader.read(it)
            }
        }
        if (event.eventType == END_DOCUMENT) {
            range = (range.first until (range.first+text.size))
        }
        if (event.eventType == CHARACTERS) {
            if (!event.asCharacters().isSurrogate && text.firstOrNull() == '&') {
                text += originalReader.read().toChar()
                range = (range.first until event.location.characterOffset)
                currentOffset = range.last + 1
            }
        }
        return XmlEvent(
            event,
            range,
            String(text)
        )
    }
}

val Characters.isSurrogate: Boolean get() {
    return this.data.last().isLowSurrogate()
}

