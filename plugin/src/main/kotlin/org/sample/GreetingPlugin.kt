package org.sample

import org.gradle.api.Plugin
import org.gradle.api.Project

class GreetingPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("greeting", GreetingExtension::class.java)
        val task = project.tasks.register("greeting", GreetingTask::class.java)
        project.afterEvaluate {
            task.configure { target ->
                extension.who?.let { who ->
                    target.who = who
                }
            }
        }
    }
}
