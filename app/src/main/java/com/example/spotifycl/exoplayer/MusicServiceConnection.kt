package com.example.spotifycl.exoplayer

import android.content.ComponentName
import android.content.Context
import android.media.browse.MediaBrowser
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.spotifycl.other.Constant.NETWORK_ERROR
import com.example.spotifycl.other.Event
import com.example.spotifycl.other.Resource

class MusicServiceConnection(
    context: Context
) {
    private val _isConnected = MutableLiveData<Event<Resource<Boolean>>>()
    val isConnected: LiveData<Event<Resource<Boolean>>> = _isConnected

    private val _networkError = MutableLiveData<Event<Resource<Boolean>>>()
    val networkError: LiveData<Event<Resource<Boolean>>> = _networkError

    private val _playBackState = MutableLiveData<PlaybackStateCompat?>()
    val playBackState: LiveData<PlaybackStateCompat?> = _playBackState

    private val _currentPlayingSong = MutableLiveData<MediaMetadataCompat>()
    val currentPlayingSong: LiveData<MediaMetadataCompat> = _currentPlayingSong

    lateinit var mediaControllerCompat: MediaControllerCompat

    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaControllerCompat.transportControls

    private val mediaBrowserConnectionCallBack = MediaBrowserConnectionCallBack(context)

    private val mediaBrowser = MediaBrowserCompat(
        context,
        ComponentName(context, MusicServiceConnection::class.java),
        mediaBrowserConnectionCallBack,
        null
    ).apply {
        connect()
    }


    fun subscribe(parentId:String,callBack:MediaBrowserCompat.SubscriptionCallback){
        mediaBrowser.subscribe(parentId,callBack)
    }

    fun unSubscribe(parentId:String,callBack:MediaBrowserCompat.SubscriptionCallback){
        mediaBrowser.unsubscribe(parentId,callBack)
    }


    private inner class MediaBrowserConnectionCallBack(
        private val context:
        Context
    ) : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            mediaControllerCompat = MediaControllerCompat(context, mediaBrowser.sessionToken)
                .apply {
                    registerCallback(MediaControllerCallBack())
                }
            _isConnected.postValue(Event(Resource.success(true)))
        }

        override fun onConnectionSuspended() {
            _isConnected.postValue(Event(Resource.error("The connection was suspended", false)))
        }

        override fun onConnectionFailed() {
            _isConnected.postValue(Event(Resource.error("The connection is failed", false)))
        }


    }

    private inner class MediaControllerCallBack : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            _playBackState.postValue(state)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            _currentPlayingSong.postValue(metadata)
        }

        override fun onSessionEvent(event: String?, extras: Bundle?) {
            when (event) {
                NETWORK_ERROR -> _networkError.postValue(
                    Event(
                        Resource.error(
                            "Couldn't connect to the server." +
                                    " Please check your internet connection", null
                        )
                    )
                )
            }
        }

        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallBack.onConnectionSuspended()
        }


    }

}