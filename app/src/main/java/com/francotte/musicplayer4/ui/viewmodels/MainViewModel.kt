package com.francotte.musicplayer4.ui.viewmodels

import android.support.v4.media.MediaBrowserCompat
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.francotte.musicplayer4.data.entities.Song
import com.francotte.musicplayer4.exoplayer.MusicServiceConnection
import com.francotte.musicplayer4.other.Resource

class MainViewModel @ViewModelInject constructor(
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {

    private val _mediaItems = MutableLiveData<Resource<List<Song>>>()
    val mediaItems : LiveData<Resource<List<Song>>> = _mediaItems

    val isConnected = musicServiceConnection.isConnected
    val networkError = musicServiceConnection.networkError
    val curPlayingSong = musicServiceConnection.currentPlayingSong
    val playbackState = musicServiceConnection.playbackState


    init {
        _mediaItems.postValue(Resource.loading(null))
        musicServiceConnection.subscribe("root_id", object : MediaBrowserCompat.SubscriptionCallback() {
            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>
            ) {
                super.onChildrenLoaded(parentId, children)
                val items = children.map {
                    Song(
                        it.mediaId!!,
                        it.description.title.toString(),
                        it.description.subtitle.toString(),
                        it.description.mediaUri.toString(),
                        it.description.iconUri.toString()
                    )
                }
                _mediaItems.postValue(Resource.success(items))
            }
        })
    }

    fun skipToNextSong () {
        musicServiceConnection.transportControls.skipToNext()
    }

    fun skipToPreviousSong () {
        musicServiceConnection.transportControls.skipToPrevious()
    }

    fun seekTo (pos : Long) {
        musicServiceConnection.transportControls.seekTo(pos)
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unsubscribe("root_id", object  : MediaBrowserCompat.SubscriptionCallback() {

        })
    }


}