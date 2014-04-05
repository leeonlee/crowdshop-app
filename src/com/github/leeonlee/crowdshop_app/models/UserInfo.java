package com.github.leeonlee.crowdshop_app.models;

import android.graphics.Bitmap;
import android.net.Uri;

public final class UserInfo {
	
	public final String username;
	public final String personalName;
	public final Uri photoUri;

	public UserInfo(String username, String personalName, Uri photoUri) {
		if (username == null)
			throw new NullPointerException("username");
		if (personalName == null)
			throw new NullPointerException("personalName");
		if (photoUri == null)
			throw new NullPointerException("photoUri");
		
		this.username = username;
		this.personalName = personalName;
		this.photoUri = photoUri;
	}

	public Bitmap getPhoto() {
		// TODO: Either download or use cached file
		return null;
	}
}
