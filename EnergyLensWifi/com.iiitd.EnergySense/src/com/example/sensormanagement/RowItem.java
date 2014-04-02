package com.example.sensormanagement;

import com.iiitd.EnergySenseWifi.R;



public class RowItem {
	private int imageId;
	private String title;
	private String desc;
	private boolean selected;
	public RowItem(int imageId, String title, String desc) {
		this.imageId = imageId;
		this.title = title;
		this.desc = desc;
		selected=false;
	}
	public int getImageId() {
		return imageId;
	}
	public void setImageId(int imageId) {
		this.imageId = imageId;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
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
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}