package com.example.spotifycl.ui.viewmodel

import android.media.browse.MediaBrowser
import android.support.v4.media.MediaBrowserCompat
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.spotifycl.exoplayer.MusicServiceConnection
import com.example.spotifycl.model.Song
import com.example.spotifycl.other.Constant.MEDIA_ROOT_ID
import com.example.spotifycl.other.Resource


class MainViewModel @ViewModelInject constructor(private val musicServiceConnection: MusicServiceConnection) {
    private val _mediaItems = MutableLiveData<Resource<List<Song>>>()
    val mediaItems: LiveData<Resource<List<Song>>> = _mediaItems

    val isConnected = musicServiceConnection.isConnected
    val netWorkError = musicServiceConnection.networkError
    val currPlayingSong = musicServiceConnection.currentPlayingSong
    val playBackState = musicServiceConnection.playBackState

    init {
        _mediaItems.postValue(Resource.Loading(null))
        musicServiceConnection.subscribe(MEDIA_ROOT_ID, object :
            MediaBrowserCompat.SubscriptionCallback() {
            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>
            ) {
                super.onChildrenLoaded(parentId, children)
                val items = children.map {
                    Song(
                        it.mediaId!!,
                        it.description.toString(),
                        it.description.subtitle.toString(),
                        it.description.mediaUri.toString(),
                        it.description.iconUri.toString()
                    )
                }

                _mediaItems.postValue(Resource.success(items))

            }
        })
    }

}