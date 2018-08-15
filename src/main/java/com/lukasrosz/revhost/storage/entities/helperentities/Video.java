package com.lukasrosz.revhost.storage.entities.helperentities;

public class Video {
	
	private String title;
	private String url;
	private String shortUrl;
	
	public Video() {
		
	}
	
	public Video(String title, String url, String shortUrl) {
		this.title = title;
		this.url = url;
		this.shortUrl = shortUrl;
	}

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getShortUrl() {
		return shortUrl;
	}
	
	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}
	
	@Override
	public String toString() {
		return "Video [title=" + title + ", url=" + url + ", shortUrl=" + shortUrl + "]";
	}

}
