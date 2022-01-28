package io.github.irgaly.gradle.rur.extensions

import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Project

fun Project.finalizeAgpDsl(action: (Project) -> Unit) {
    val androidComponents = project.extensions.findByType(AndroidComponentsExtension::class.java)
    if (androidComponents != null) {
        // AGP 7.0.0 ~
        androidComponents.finalizeDsl {
            action(project)
        }
    } else {
        if (project.state.executed) {
            action(project)
        } else {
            project.afterEvaluate(action)
        }
    }
}
