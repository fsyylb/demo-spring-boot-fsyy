package com.fsyy.fsyywebdemo.monitor.event;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

import static java.lang.invoke.MethodType.methodType;

public final class ThreadHints
{
    private static final MethodHandle ON_SPIN_WAIT_METHOD_HANDLE;

    static
    {
        final MethodHandles.Lookup lookup = MethodHandles.lookup();

        MethodHandle methodHandle = null;
        try
        {
            methodHandle = lookup.findStatic(Thread.class, "onSpinWait", methodType(void.class));
        }
        catch (final Exception ignore)
        {
        }

        ON_SPIN_WAIT_METHOD_HANDLE = methodHandle;
    }

    private ThreadHints()
    {
    }

    /**
     * Indicates that the caller is momentarily unable to progress, until the
     * occurrence of one or more actions on the part of other activities.  By
     * invoking this method within each iteration of a spin-wait loop construct,
     * the calling thread indicates to the runtime that it is busy-waiting. The runtime
     * may take action to improve the performance of invoking spin-wait loop constructions.
     */
    public static void onSpinWait()
    {
        // Call java.lang.Thread.onSpinWait() on Java SE versions that support it. Do nothing otherwise.
        // This should optimize away to either nothing or to an inlining of java.lang.Thread.onSpinWait()
        if (null != ON_SPIN_WAIT_METHOD_HANDLE)
        {
            try
            {
                ON_SPIN_WAIT_METHOD_HANDLE.invokeExact();
            }
            catch (final Throwable ignore)
            {
            }
        }
    }
}