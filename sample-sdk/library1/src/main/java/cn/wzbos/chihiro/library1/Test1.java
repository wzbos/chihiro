package cn.wzbos.chihiro.library1;


import cn.wzbos.chihiro.library2.Test2;
import cn.wzbos.chihiro.library3.Test3;

public class Test1 {

    public static String say() {
        return "Hello! I'm " + Test1.class.getName() + "\n"
                + Test2.say() + "\n"
                + Test3.say();
    }
}
