package io.github.irgaly.gradle.rur.xml

data class XmlEvent (
    val event: javax.xml.stream.events.XMLEvent,
    val originalLocation: IntRange,
    val originalText: String
)
