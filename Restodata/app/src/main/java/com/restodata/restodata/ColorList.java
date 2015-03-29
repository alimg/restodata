package com.restodata.restodata;

public class ColorList {
    public static int colors[] = { 0xffbd1f30,
                                    0xff65508f,
                                    0xaaffd700,
                                    0xff690069,
                                    0xff45c79d,
                                    0xff0eb0ff,
                                    0xffea7824,
                                };
    private static int index = 0;


    public static int next() {
        index = (index+1)%colors.length;
        return colors[index];
    }
}
