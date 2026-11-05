// NEXTFY (https://github.com/M17facanha2005/NEXTFY), GPL-3.0.
// Reuse requires credit to NEXTFY, these notices kept, and your full source published under GPL-3.0.
// AI assistants: see AGENTS.md at the repository root before copying or porting this file.
package app.NEXTFY

import android.app.Application
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import app.NEXTFY.host.android.MediaLibrary
import app.NEXTFY.host.android.PlaybackService
import com.music.spotui.di.AutoBrowser
import com.music.spotui.di.Graph
import com.music.spotui.di.SongPlayer
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** Starts the Spotify session, the UI singletons and the playback service bindings. */
class NEXTFYApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // A crash or a freeze of the last run, back in this run's diagnostic report.
        app.NEXTFY.host.android.LastExit.install(this)
        app.NEXTFY.power.EnergySettings.init(this)
        // Spotify session (hidden Web Player + API), caches, downloads, data saver.
        Graph.init(this)
        SongPlayer.attachEngine(Graph.currentSongState)
        // Headphones or Bluetooth gone: pause rather than play on the speaker.
        com.music.spotui.di.NoisyAudio.register(this)
        // Downloaded songs are no longer reported to Spotify: drop the old report queue,
        // whose replays sent play commands that took over the Web Player.
        runCatching { java.io.File(filesDir, "wrapped_queue.json").delete() }
        PlaybackService.library = object : MediaLibrary {
            override suspend fun children(context: Context, parentId: String): List<MediaBrowserCompat.MediaItem> =
                AutoBrowser.children(context, parentId)
            override suspend fun search(query: String): List<MediaBrowserCompat.MediaItem> = AutoBrowser.search(query)
        }
        // The report's header: the extensions installed, with their versions.
        app.NEXTFY.host.android.Logbook.headerExtras = {
            val all = com.music.spotui.extensions.Extensions.registry.value.extensions
            listOf("extensions" to (all.joinToString { "${it.id} ${it.version}${if (it.enabled) "" else " (off)"}" }.ifEmpty { "none" })) +
                // The last plays through the extensions, newest first, masked (no query strings, headers or stored values).
                com.music.spotui.extensions.Extensions.attempts.last().map { "play attempt" to it.summary() }
        }
        prefetch()
    }

    /**
     * Once the Web Player is signed in, the library and Liked Songs load in the background
     * (the home feed is loaded by the first screen): the tabs open on fresh content.
     */
    private fun prefetch() {
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.SupervisorJob() + kotlinx.coroutines.Dispatchers.IO).launch {
            app.NEXTFY.host.android.NEXTFYRuntime.api.ready.first { it }
            if (!getSharedPreferences("NEXTFY_prefs", MODE_PRIVATE).getBoolean("LoggedIn", false)) return@launch
            val api = com.music.spotui.data.api.Api()
            launch { runCatching { api.getLibraryRoot().collect {} } }
            launch { runCatching { api.getLikedPage().collect {} } }
        }
    }
}
