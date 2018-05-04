package com.ubt.alpha1e.onlineaudioplayer.model;


/**
 * @作者：ubt
 * @日期: 2018/4/20 13:43
 * @描述:
 */


public class PlayerEvent {
    Event event;
    private int currentPlayingIndex;
    private int currentClickingIndex;
    private String currentPlayingSongName;

    public String getLoop() {
        return loop;
    }

    public void setLoop(String loop) {
        this.loop = loop;
    }

    private String loop;

    public String getCateogryId() {
        return cateogryId;
    }

    public void setCateogryId(String cateogryId) {
        this.cateogryId = cateogryId;
    }

    private String cateogryId;

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    private String albumId;

    public String getCurrentPlayingSongName() {
        return currentPlayingSongName;
    }

    public void setCurrentPlayingSongName(String currentPlayingSongName) {
        this.currentPlayingSongName = currentPlayingSongName;
    }



    public int getCurrentPlayingIndex() {
        return currentPlayingIndex;
    }

    public void setCurrentPlayingIndex(int currentPlayingIndex) {
        this.currentPlayingIndex = currentPlayingIndex;
    }


    public int getCurrentClickingIndex() {
        return currentClickingIndex;
    }

    public void setCurrentClickingIndex(int currentClickingIndex) {
        this.currentClickingIndex = currentClickingIndex;
    }



    public PlayerEvent(Event event) {
        this.event = event;
    }
    public enum Event{
        CONTROL_PLAY_NEXT,
        CONTROL_STOP,
        GET_PLAYING_INDEX,
        GET_ROBOT_ONLINEPLAYING_STATUS,
        TAP_HEAD,
        GET_LOOP_MODE
    }
    public PlayerEvent.Event getEvent() {
        return event;
    }
}

