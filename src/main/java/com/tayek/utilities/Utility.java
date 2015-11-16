package com.tayek.utilities;
public class Utility {
    public static String method() {
        return Thread.currentThread().getStackTrace()[2].getMethodName()+"()";
    }
}
