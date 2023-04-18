package org.sample.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

@Suppress("UNUSED_VARIABLE", "LocalVariableName")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.Transparent.toArgb()
        window.navigationBarColor = Color.Transparent.toArgb()
        WindowInsetsControllerCompat(
            window,
            findViewById(android.R.id.content)
        ).isAppearanceLightStatusBars = true
        setContent {
            MaterialTheme {
                LaunchedEffect(Unit) {
                    val samplesub_used =
                        org.sample.app.sample.sub.R.drawable.samplesub_used_drawable_from_sample
                    val used = listOf(
                        R.string.empty1, R.string.empty2, R.string.character_reference,
                        R.string.grater_than, R.string.amp,
                        R.string.apos, R.string.cdata,
                        R.string.surrogate, R.string.ivs
                    )
                }
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Sample")
                }
            }
        }
    }
}
