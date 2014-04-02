package com.example.sensormanagement;

import com.iiitd.EnergySenseWifi.R;



public class RowItemLabel {

	private String title;
	private String desc;

	public RowItemLabel( String title) {

		this.title = title;


	}


	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@Override
	public String toString() {
		return title + "\n" + desc;
	}

}