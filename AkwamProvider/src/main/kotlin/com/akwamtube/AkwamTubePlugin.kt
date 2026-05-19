package com.akwamtube

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context

@CloudstreamPlugin
class AkwamTubePlugin: Plugin() {
    override fun load(context: Context) {
        registerMainAPI(AkwamTube())
    }
}
