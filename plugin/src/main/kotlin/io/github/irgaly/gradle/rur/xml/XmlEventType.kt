package io.github.irgaly.gradle.rur.xml

import javax.xml.stream.XMLStreamConstants.*

@Suppress("unused")
enum class XmlEventType(val eventId: Int) {
    StartElement(START_ELEMENT),
    EndElement(END_ELEMENT),
    ProcessingInstruction(PROCESSING_INSTRUCTION),
    Characters(CHARACTERS),
    Comment(COMMENT),
    Space(SPACE),
    StartDocument(START_DOCUMENT),
    EndDocument(END_DOCUMENT),
    EntityReference(ENTITY_REFERENCE),
    Attribute(ATTRIBUTE),
    Dtd(DTD),
    Cdata(CDATA),
    Namespace(NAMESPACE),
    NotationDeclaration(NOTATION_DECLARATION),
    EntityDeclaration(ENTITY_DECLARATION);
    companion object {
        fun valueOf(eventId: Int): XmlEventType {
            return values().firstOrNull { it.eventId == eventId } ?: throw IllegalArgumentException("eventId")
        }
    }
}
