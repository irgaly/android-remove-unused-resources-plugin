package io.github.irgaly.gradle.rur.xml

import org.codehaus.stax2.evt.XMLEvent2

data class XmlEvent(
    val event: XMLEvent2,
    val parent: XmlEvent?,
    val originalLocation: IntRange,
    val originalText: String
) {
    val level: Int
        get() {
            var ret = 0
            var node = parent
            while (node != null) {
                ret++
                node = node.parent
            }
            return ret
        }
}
