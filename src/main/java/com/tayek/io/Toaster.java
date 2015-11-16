package com.tayek.io;
public interface Toaster { 
    void toast(String string);
    public static class ToasterImpl implements Toaster {
        @Override public void toast(String string) {
            System.out.println("toast: "+string);
        }
    }
}
