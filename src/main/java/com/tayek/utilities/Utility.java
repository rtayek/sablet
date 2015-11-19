package com.tayek.utilities;
public class Utility {
    public static String method() {
        return Thread.currentThread().getStackTrace()[2].getMethodName()+"()";
    }
    public static Integer toInteger(String argument) {
        Integer n=null;
        try {
            n=Integer.valueOf(argument);
        } catch(NumberFormatException e) {
            System.out.println(argument+" is not a valid tabletId");
        }
        return n;
    }
}
