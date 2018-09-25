package ru.maksimov.andrey.prograph.component;

import java.awt.Color;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {

	public static String tailCut(String name) {
		//return name.replaceFirst("[.][^.]+$", "");
		int index = name.lastIndexOf('.');
		if (index > 0) {
			return name.substring(0, index);
		} else {
			return name;
		}
	}

	public static boolean checkWithRegExp(String input, String regex) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(input);
		return m.matches();
	}

	/**
	 * Получить случайное число в интервале
	 * 
	 * @param min
	 *            минимальная граница
	 * @param max
	 *            максимальная граница
	 * @return случайное число
	 */
	public static int random(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max);
	}

	/*
	{"#3366cc","#dc3912","#ff9900","#109618","#990099","#0099c6","#dd4477","#66aa00","#b82e2e","#316395","#994499","#22aa99","#aaaa11","#6633cc","#e67300","#8b0707","#651067","#329262","#5574a6","#3b3eac","#b77322","#16d620","#b91383","#f4359e","#9c5935","#a9c413","#2a778d","#668d1c","#bea413","#0c5922","#743411"}
	 */
	public static Color getColor(PropertyType type) {
		//TODO fix
		return null;
	}
	public static Color getColor(int intColor) {
		System.out.println(intColor);
		float r = 0;
		float g = 0;
		float b = 0;
		for (int count = 2; count < 9; count++) {
			if (intColor % count == 0) {
				if(r<1) {
					r += 0.18;
				} else {
					r -= 0.20;
				}
				if(b<1) {
					b += 0.10;
				} else {
					b -= 0.12;
				}
				
			} else {
				if(g<1) {
					g += 0.10;
				} else {
					g -= 0.12;
				}
				if(b<1) {
					b += 0.05;
				} else {
					b -= 0.07;
				}

			}
		}

		return new Color(r, g, b);
	}
}
