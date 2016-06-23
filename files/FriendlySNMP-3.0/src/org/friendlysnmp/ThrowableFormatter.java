/*
 * File: ThrowableFormatter.java
 * 
 * Copyright (C) 2014 FriendlySNMP.org; All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *  
 * $Id: ThrowableFormatter.java,v 1.15 2014/01/11 02:19:23 mg Exp $
 */
package org.friendlysnmp;

/**
 * Helper class to format exceptions.
 */
public abstract class ThrowableFormatter {

    public static final int FULL_STACK = -1;
    
    /**
     * Formats exception with custom message, thread name 
     * and full stack trace.
     * 
     * @param sMsg custom message
     * @param t thread with exception
     * @param e exception
     * @return formatted exception
     */
    public final static String format(String sMsg, Throwable e, Thread t) {
        return format(sMsg, e, FULL_STACK, t);
    }
    
    /**
     * Formats exception with custom message, current thread name 
     * and full stack trace.
     * 
     * @param sMsg custom message
     * @param e exception
     * @return formatted exception
     */
    public final static String format(String sMsg, Throwable e) {
        return format(sMsg, e, FULL_STACK);
    }
    
    /**
     * Formats exception with custom message, current thread name 
     * and specified stack trace depth.
     * 
     * @param sMsg custom message
     * @param e exception
     * @param nStackDepth trace stack depth:
     *    {@link #FULL_STACK} - to format with full trace stack 
     *     0 - to format with "exception class + message"
     * @return formatted exception
     */
    public final static String format(String sMsg, Throwable e, int nStackDepth) {
        return format(sMsg, e, nStackDepth, Thread.currentThread());
    }
    
    /**
     * Formats exception with custom message, thread name (if not null)
     * and specified stack depth.
     * 
     * @param sMsg custom message
     * @param t thread with exception
     * @param e exception
     * @param nStackDepth trace stack depth:
     *    {@link #FULL_STACK} - to format with full trace stack 
     *     0 - to format with "exception class + message"
     * @return formatted exception
     */
    public final static String format(String sMsg, Throwable e, int nStackDepth, Thread t) {
        StringBuilder sb = new StringBuilder();
        if (sMsg != null  &&  sMsg.trim().length() > 0) {
            sb.append(sMsg).append('\n');
        }
        if (t != null) {
            sb.append("Thread: ").append(t.getName()).append('\n');
        }
        if (e != null) {
            sb.append(format(e, nStackDepth));
        }
        return sb.toString();
    }
    
    /**
     * Formats exception with full stack trace without custom message and without thread name.
     * 
     * @param t exception
     * @return formatted exception
     */
    public final static String format(Throwable t) {
        return format(t, FULL_STACK);
    }

    /**
     * Formats exception without custom message and without thread name.
     * 
     * @param t exception
     * @param nStackDepth trace stack depth:
     *    {@link #FULL_STACK} - to format with full trace stack 
     *     0 - to format with "exception class + message"
     * @return formatted exception
     */
    public final static String format(Throwable t, int nStackDepth) {
        StringBuilder sb = new StringBuilder();
        int n = 1;
        for (Throwable e = t;  e != null;  e = e.getCause()) {
            // Each e.getCause() will print something like this:
            //    [3] com.acme.web.xxx.XxxException: Cannot ... 
            //        at com.acme.web.xxx.Xxxx(Xxxx.java:1322)
            //
            if (n > 1) {
                sb.append('\n');
            }
            sb.append('[');
            sb.append(n);
            sb.append("] ");
            sb.append(e.getClass().getName());
            sb.append(": ");
            String sMsg = e.getMessage();
            if (sMsg != null) {
                if (sMsg.endsWith("\n")) {
                    sMsg = sMsg.substring(0, sMsg.length() - 1);
                }
                sb.append(sMsg);
            }
            StackTraceElement[] a_stack = e.getStackTrace();
            for (int i = 0;  i < a_stack.length;  i++) {
                if (nStackDepth >= 0  &&  i >= nStackDepth) {
                    break;
                }
                sb.append("\n    at ");
                sb.append(a_stack[i].toString());
            }
            n++;
        }
        return sb.toString(); 
    }
    
    /**
     * Formats exception in friendly format (messages only).
     * 
     * @param t exception
     * @return formatted exception
     */
    public final static String formatFriendly(Throwable t) {
        StringBuilder sb = new StringBuilder();
        for (Throwable e = t;  e != null;  e = e.getCause()) {
            if (sb.length() > 0) {
                sb.append("\n  caused by: ");
            }
            String sMsg = e.getMessage();
            if (sMsg == null  ||  sMsg.length() == 0) {
                sMsg = e.getClass().getSimpleName();
            }
            sb.append(sMsg);
        }
        return sb.toString(); 
    }
    
    /**
     * Converts a string to html format by replacing newline symbols 
     * to &lt;br&gt; and space symbols to &lt;&amp;nbsp;&gt;.
     *
     * @param s input string.
     * @return output string in html format.
     */
    public static String toHtml(String s) {
        StringBuilder sb = new StringBuilder(s.length() * 2);
        for (int i = 0;  i < s.length();  i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '\n':
                    sb.append("<br>");
                    break;
                case ' ':
                    sb.append("&nbsp;");
                    break;
                default:
                    sb.append(ch);
                    break;
            }
        }
        return sb.toString();
    }
    
} // class ThrowableFormatter
