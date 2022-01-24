package io.github.irgaly.gradle.rur.xml

import javax.xml.namespace.QName
import javax.xml.stream.events.Attribute
import javax.xml.stream.events.StartElement

fun StartElement.hasAttribute(name: String): Boolean {
    return attributes.asSequence().any {
        (it as Attribute).name.localPart == name
    }
}

fun StartElement.getAttributeValue(name: String): String? {
    return getAttributeByName(QName(name))?.value
}

fun StartElement.getAttributeValue(name: QName): String? {
    return getAttributeByName(name)?.value
}
