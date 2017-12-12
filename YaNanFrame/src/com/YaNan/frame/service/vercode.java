package com.YaNan.frame.service;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.YaNan.frame.bean.verCodeBean;

public class vercode {
	/**
	 * drawCode(verCodeBean)
	 * 
	 * @param vercodebean
	 */
	public static boolean draw(verCodeBean vercodebean) {
		int panleWidth = vercodebean.getWidth();
		int panleHeight = vercodebean.getHeight();
		Color fontColor = Color.decode(vercodebean.getColor());
		Color backgroundColor = Color.decode(vercodebean.getBackground());
		File filePath = new File(vercodebean.getFilepath());
		String content = vercodebean.getContent();
		BufferedImage bufferedimage = new BufferedImage(panleWidth,
				panleHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gs = bufferedimage.createGraphics();
		gs.setBackground(backgroundColor);
		gs.clearRect(0, 0, panleWidth, panleHeight);
		Font font = new Font("宋体", Font.BOLD, 18);
		gs.setFont(font);
		gs.setColor(fontColor);
		gs.drawString(content + "", 12, 18);
		try {
			System.out.println(filePath);
			ImageIO.write(bufferedimage, "png", filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("验证码：" + content + "，位置" + filePath);
		return true;
	}

	public static void main(String[] args) {
	}

}
