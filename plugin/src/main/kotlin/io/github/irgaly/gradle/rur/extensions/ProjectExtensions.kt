package io.github.irgaly.gradle.rur.extensions

import org.gradle.api.Project

fun Project.afterEvaluateOrExecute(action: (Project) -> Unit) {
    if (project.state.executed) {
        action(project)
    } else {
        project.afterEvaluate(action)
    }
}
