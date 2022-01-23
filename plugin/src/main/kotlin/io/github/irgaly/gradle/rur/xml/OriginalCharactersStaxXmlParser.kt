package io.github.irgaly.gradle.rur.xml

import com.ctc.wstx.evt.WstxEventReader
import com.ctc.wstx.stax.WstxInputFactory
import io.github.irgaly.gradle.rur.io.CloneableInputStream
import org.codehaus.stax2.XMLInputFactory2.P_AUTO_CLOSE_INPUT
import org.codehaus.stax2.XMLInputFactory2.P_REPORT_PROLOG_WHITESPACE
import org.codehaus.stax2.evt.XMLEvent2
import java.io.InputStream
import java.io.InputStreamReader
import javax.xml.stream.XMLInputFactory.*

/**
 * preserve original characters XML StAX Parser
 */
class OriginalCharactersStaxXmlParser(input: InputStream) {
    private val originalReader: InputStreamReader
    private val reader: WstxEventReader

    private var currentEvent: XmlEvent? = null

    init {
        val cloneable = CloneableInputStream(input)
        originalReader = cloneable.fork().reader()
        reader = (WstxInputFactory.newInstance() as WstxInputFactory).apply {
            setProperty(P_AUTO_CLOSE_INPUT, true)
            setProperty(P_REPORT_PROLOG_WHITESPACE, true)
            setProperty(IS_COALESCING, true)
            setProperty(IS_REPLACING_ENTITY_REFERENCES, false)
            setProperty(IS_SUPPORTING_EXTERNAL_ENTITIES, false)
        }.createXMLEventReader(cloneable) as WstxEventReader
    }

    fun hasNext(): Boolean {
        return reader.hasNext()
    }

    fun nextEvent(): XmlEvent {
        val event = reader.nextEvent() as XMLEvent2
        val next = reader.peek()
        val parent = if (event.isEndElement) {
            currentEvent?.parent
        } else {
            currentEvent
        }
        val range = if (next != null) {
            event.location.characterOffset until next.location.characterOffset
        } else {
            // empty range for EndDocument
            event.location.characterOffset until event.location.characterOffset
        }
        val text = CharArray(range.count()).let { buffer ->
            var loadSize = 0
            while (loadSize < range.count()) {
                val result = originalReader.read(buffer)
                if (result < 0) {
                    break
                }
                loadSize += result
            }
            String(buffer, 0, loadSize)
        }
        return XmlEvent(event, parent, range, text).also {
            if (event.isStartElement) {
                currentEvent = it
            } else if (event.isEndElement) {
                currentEvent = it.parent
            }
        }
    }

    fun close() {
        reader.close()
        originalReader.close()
    }
}
