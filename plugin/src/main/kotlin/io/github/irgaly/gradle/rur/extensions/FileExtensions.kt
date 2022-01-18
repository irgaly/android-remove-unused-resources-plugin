package io.github.irgaly.gradle.rur.extensions

import java.io.File

/**
 * Indicates whether the [other] is a descendant of [this].
 * This is a textual computation, there is no file access.
 *
 * Both file path is required to be absolute path.
 *
 * The "." and ".." path is normalized before indicates.
 * Any symbolic links will not resolve, because of no file system access.
 *
 * @param other prospective child file or directory
 * @param includesSelf if true treat same path as descendant. otherwise don't.
 * @throws IllegalStateException [this] has a relative path
 * @throws IllegalArgumentException [other] has a relative path
 */
fun File.containsInDescendants(other: File, includesSelf: Boolean = true): Boolean {
    if (!isAbsolute) {
        error("can not compare if base file path is relative path: $this")
    }
    if (!other.isAbsolute) {
        throw IllegalArgumentException("can not compare if other file path is relative path: $other")
    }
    val base = normalize()
    var target: File? = other.normalize()
    if (!includesSelf) {
        target = target?.parentFile
    }
    while (target != null) {
        if (base == target) {
            return true
        }
        target = target.parentFile
    }
    return false
}
