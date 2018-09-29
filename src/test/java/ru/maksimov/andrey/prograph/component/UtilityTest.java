package ru.maksimov.andrey.prograph.component;

import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class UtilityTest {
    @Test
    @Ignore
    public void hex2Dec() {
        List<Integer> result = new ArrayList<>();
        String[] hexArray = new String[]{"#3366cc","#dc3912","#ff9900","#109618","#990099","#0099c6","#dd4477","#66aa00","#b82e2e","#316395","#994499","#22aa99","#aaaa11","#6633cc","#e67300","#8b0707","#651067","#329262","#5574a6","#3b3eac","#b77322","#16d620","#b91383","#f4359e","#9c5935","#a9c413","#2a778d","#668d1c","#bea413","#0c5922","#743411"};
        for (String str : hexArray) {
            String hex = str.substring(1);
            if (hex.length() == 6) {
                result.add(Integer.parseInt(hex, 16));
            } else {
                throw new IllegalArgumentException(str);
            }
        }

        System.out.println(result);
    }
}