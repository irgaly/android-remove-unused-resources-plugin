package org.sample

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class GreetingTask : DefaultTask() {
    var who = "mate"
    @TaskAction
    fun greet() {
        println("Hi $who!!!")
    }
}
