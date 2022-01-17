package org.sample.app

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import org.sample.app.databinding.ActivityMainBinding

@Suppress("UNUSED_VARIABLE", "LocalVariableName")
class MainActivity : AppCompatActivity(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bindinng = ActivityMainBinding.bind(findViewById<ViewGroup>(android.R.id.content)[0])
        val samplesub_used = R.drawable.samplesub_used_drawable_from_sample
    }
}
