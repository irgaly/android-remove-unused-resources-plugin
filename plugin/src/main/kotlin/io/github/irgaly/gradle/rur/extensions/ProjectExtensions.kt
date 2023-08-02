package io.github.irgaly.gradle.rur.extensions

import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Project

fun Project.safeAfterEvaluate(action: (Project) -> Unit) {
    val androidComponents =
        project.extensions.findByType(AndroidComponentsExtension::class.java)
    if (androidComponents != null) {
        // AGP has already applied
        androidComponents.finalizeDsl {
            action(project)
        }
    } else {
        if (project.state.executed) {
            // project has already evaluated
            action(project)
        } else {
            // project will be evaluate
            project.afterEvaluate(action)
        }
    }
}
