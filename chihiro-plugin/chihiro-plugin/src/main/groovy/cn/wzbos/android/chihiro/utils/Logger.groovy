package cn.wzbos.android.chihiro.utils

/**
 * log utils
 * Created by wuzongbo on 2020/09/18.
 */
class Logger {
    enum StyleColor {
        black(30), red(31), green(32), yellow(33),
        blue(34), magenta(35), cyan(36), white(37)

        StyleColor(int value) {
            this.color = value
        }
        private final int color

        int getValue() {
            color
        }
    }

    static String STX = '[Chihiro]'

    static getStyleString(StyleColor color, text) {
        return new String((char) 27) + "[${color.value}m${text}" + new String((char) 27) + "[0m"
    }

    static boolean isDebug

    static void d(String text) {
        if (isDebug)
            println STX + " " + text
    }

    static void i(String text) {
        println getStyleString(StyleColor.green, STX + " " + text)
    }

    static void e(String text) {
        println getStyleString(StyleColor.red, STX + " " + text)
    }

    static void w(String text) {
        println getStyleString(StyleColor.yellow, STX + " " + text)
    }
}