package io.github.irgaly.gradle.rur.extensions

import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList

internal fun Element.getAllElements(tagName: String): Sequence<Element> {
    return getElementsByTagName(tagName).toSequence().map { it as Element }
}

internal fun NodeList.toSequence(): Sequence<Node> {
    return (0 until length).asSequence().map { item(it) }
}

internal fun Node.getElements(tagName: String): Sequence<Element> {
    return childNodes.toSequence().filter {
        (it.nodeType == Node.ELEMENT_NODE) && (it.nodeName == tagName)
    }.map {
        it as Element
    }
}

internal fun Node.getAttributeText(name: String): String? {
    return attributes?.getNamedItem(name)?.nodeValue
}
