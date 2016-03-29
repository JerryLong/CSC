package com.guess.image.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class FolderBeanParcel implements Parcelable {
	private String dir;
	private String firstImgPath;
	private String imgPath;
	private String name;
	private int count;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(dir);
		dest.writeString(firstImgPath);
		dest.writeString(imgPath);
		dest.writeString(name);
		dest.writeInt(count);
	}

	public static final Parcelable.Creator<FolderBeanParcel> CREATOR = new Creator<FolderBeanParcel>() {

		@Override
		public FolderBeanParcel[] newArray(int size) {

			return new FolderBeanParcel[size];
		}

		@Override
		public FolderBeanParcel createFromParcel(Parcel source) {
			FolderBeanParcel folder = new FolderBeanParcel();
			folder.dir = source.readString();
			folder.firstImgPath = source.readString();
			folder.imgPath = source.readString();
			folder.name = source.readString();
			folder.count = source.readInt();
			return folder;
		}
	};

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
		int lastIndexOf = this.dir.lastIndexOf("/") + 1;
		this.name = this.dir.substring(lastIndexOf);
	}

	public String getFirstImgPath() {
		return firstImgPath;
	}

	public void setFirstImgPath(String firstImgPath) {
		this.firstImgPath = firstImgPath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

}
