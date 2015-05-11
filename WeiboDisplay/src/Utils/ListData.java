package Utils;

import java.io.Serializable;

import android.graphics.Bitmap;

public class ListData {

	private String filename;
	private String filesize;
	private String filepath;
	private String fileUID;
	private int id= 0;
//	private String 

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilesize() {
		return filesize;
	}

	public void setFilesize(String filesize) {
		this.filesize = filesize;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFileUID() {
		return fileUID;
	}

	public void setFileUID(String fileUID) {
		this.fileUID = fileUID;
	}

}
