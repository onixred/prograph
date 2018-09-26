package ru.maksimov.andrey.prograph.component;

import java.awt.Color;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {
    public static final int[] GOOGLE_CHART_COLORS = new int[] {3368652, 14432530, 16750848, 1087000, 10027161,
            39366, 14500983, 6728192, 12070446, 3236757, 10044569, 2271897, 11184657, 6697932,
            15102720, 9111303, 6623335, 3314274, 5600422, 3882668, 12022562, 1496608, 12129155,
            16004510, 10246453, 11125779, 2783117, 6720796, 12493843, 809250, 7615505};

    public static String tailCut(String name) {
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

	public static Color getColor(PropertyType type) {
		if (type.ordinal() < GOOGLE_CHART_COLORS.length) {
		    return new Color(GOOGLE_CHART_COLORS[type.ordinal()]);
        } else {
		    return getColor(type.ordinal());
        }
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
