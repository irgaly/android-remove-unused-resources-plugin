package io.github.irgaly.gradle.rur.xml

import org.codehaus.stax2.evt.XMLEvent2

data class XmlEvent(
    val event: XMLEvent2,
    val originalLocation: IntRange,
    val originalText: String
)
