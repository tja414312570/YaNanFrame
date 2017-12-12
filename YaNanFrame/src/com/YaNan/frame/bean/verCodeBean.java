package com.YaNan.frame.bean;

public class verCodeBean {
	private String color;
	private String background;
	private String content = "s1dd";
	private String folder;
	private String ext = "jpg";
	private String filepath;
	private String filename;
	private String font = "宋体";
	private String weight = null;
	private int width = 60;
	private int height = 23;
	private int size = 14;

	public verCodeBean() {
	}

	public verCodeBean(String color, String background, String content,
			String folder, String filepath, String filename, String font,
			String weight, int width, int height) {
		this.color = color;
		this.background = background;
		this.content = content;
		this.folder = folder;
		this.filepath = filepath;
		this.filename = filename;
		this.font = font;
		this.weight = weight;
		this.width = width;
		this.height = height;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void make() {
		this.filename = this.content + "." + this.ext;
		this.filepath = this.folder + "/" + this.filename;
	}

	@Override
	public String toString() {
		return "verCodeBean [color=" + color + ", background=" + background
				+ ", content=" + content + ", folder=" + folder + ", ext="
				+ ext + ", filepath=" + filepath + ", filename=" + filename
				+ ", font=" + font + ", weight=" + weight + ", width=" + width
				+ ", height=" + height + ", size=" + size + "]";
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

}