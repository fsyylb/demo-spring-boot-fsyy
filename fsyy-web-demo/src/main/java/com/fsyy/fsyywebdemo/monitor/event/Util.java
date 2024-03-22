package com.fsyy.fsyywebdemo.monitor.event;

import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

public final class Util {
    private static final Unsafe THE_UNSAFE;

    static {
        try
        {
            final PrivilegedExceptionAction<Unsafe> action = new PrivilegedExceptionAction<Unsafe>()
            {
                public Unsafe run() throws Exception
                {
                    Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
                    theUnsafe.setAccessible(true);
                    return (Unsafe) theUnsafe.get(null);
                }
            };

            THE_UNSAFE = AccessController.doPrivileged(action);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Unable to load unsafe", e);
        }
    }

    public static Unsafe getUnsafe() {
        return THE_UNSAFE;
    }

    public static long getTomcatWebServerStartedOffset(){
        try{
            return getUnsafe().objectFieldOffset(TomcatWebServer.class.getDeclaredField("started"));
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static boolean getTomcatWebServerStarted(TomcatWebServer webServer){
        long offset = getTomcatWebServerStartedOffset();
        return getUnsafe().getBoolean(webServer, offset);
    }

}
