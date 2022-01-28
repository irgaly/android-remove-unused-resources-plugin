package io.github.irgaly.gradle.rur.extensions

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.internal.plugins.BasePlugin
import org.gradle.api.Project

fun Project.finalizeAgpDsl(action: (Project) -> Unit) {
    val androidPlugin = project.plugins.withType(BasePlugin::class.java)
    if (androidPlugin.isNotEmpty()) {
        // AGP 7.0.0 ~
        androidPlugin.configureEach {
            project.extensions.configure(AndroidComponentsExtension::class.java) {
                it.finalizeDsl {
                    action(project)
                }
            }
        }
    } else {
        if (project.state.executed) {
            action(project)
        } else {
            project.afterEvaluate(action)
        }
    }
}
