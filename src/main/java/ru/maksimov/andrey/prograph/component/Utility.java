package ru.maksimov.andrey.prograph.component;

import java.awt.Color;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {

	public static String tailCut(String name) {
		return name.replaceFirst("[.][^.]+$", "");
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

	public static Color getColor(int intColor) {
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
