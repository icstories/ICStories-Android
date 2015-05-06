package com.eeshana.icstories.common;

public class MyVideos {
	String Title;
	String VideoPath;
	String ThumbPath;
	String Date;
	String Location;
	String Description;
	String VideoId;

	public MyVideos(String title, String videoPath,String thumbPath, String date,String location,String description,String videoId) {
		super();
		Title = title;
		VideoPath = videoPath;
		ThumbPath=thumbPath;
		Date = date;
		Description = description;
		Location=location;
		VideoId=videoId;
	}

	public String getVideoId() {
		return VideoId;
	}

	public void setVideoId(String videoId) {
		VideoId = videoId;
	}

	public String getThumbPath() {
		return ThumbPath;
	}

	public void setThumbPath(String thumbPath) {
		ThumbPath = thumbPath;
	}

	public String getLocation() {
		return Location;
	}

	public void setLocation(String location) {
		Location = location;
	}

	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public String getVideoPath() {
		return VideoPath;
	}
	public void setVideoPath(String videoPath) {
		VideoPath = videoPath;
	}
	public String getDate() {
		return Date;
	}
	public void setDate(String date) {
		Date = date;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
}
