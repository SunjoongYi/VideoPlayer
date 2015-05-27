package com.example.videoplayer.aidl;

interface IVideoService{
	String getString();
	void setAudio(String path, int currentTime);
	int getCurrentTime();
}