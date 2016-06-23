/*
 * File: UncaughtExceptionHandler.java
 * 
 * Copyright (C) 2014 FriendlySNMP.org; All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301  USA
 *
 * $Id: UncaughtExceptionHandler.java,v 1.18 2014/01/11 02:19:23 mg Exp $
 */
package org.friendlysnmp;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.friendlysnmp.event.UncaughtExceptionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Check the JavaDoc for method handleException() in class 
 * java.awt.EventDispatchThread. Note, this class has package access and
 * it is not visible in Java API, go to the source code.
 * 
 * See also JavaDoc for Thread.UncaughtExceptionHandler interface.
 * 
 * <ol>
 * <li> Load the class named by the value of that property, using the
 *      current thread's context class loader,
 * <li> Instantiate that class using its zero-argument constructor,
 * <li> Find the resulting handler object's <tt>public void handle</tt>
 *      method, which should take a single argument of type
 *      <tt>Throwable</tt>, and
 * <li> Invoke the handler's <tt>handle</tt> method, passing it the
 *      <tt>thrown</tt> argument that was passed to this method.
 * </ol>
 */
public class UncaughtExceptionHandler {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(UncaughtExceptionHandler.class);

    /**
     * Collection of registered <code>UncaughtExceptionListener</code> objects. 
     */
    private static Set<UncaughtExceptionListener> hsUncaughtListener;
    
    /**
     * Flag allows uncaught exception stack trace to be sent to console.
     */
    private static boolean printToConsoleUncaught;
    
    /**
     * Default ctor does nothing. 
     * It is required to handle uncaught EDT exceptions.
     * The ctor has package access and JavaDoc is not generated for it. 
     */
    UncaughtExceptionHandler() {
    } // UncaughtExceptionHandler()
    
    /**
     * Duplicate init() does nothing. This is allowed to run multiple agents 
     * in the same application. Only the first agent does actual initialization.
     * 
     * @return creates empty collection of listeners 
     */
    static Set<UncaughtExceptionListener> init(boolean consoleUncaught) {
        // Multiple agent are started in the same JVM.
        if (hsUncaughtListener == null) {
            printToConsoleUncaught = consoleUncaught;
            hsUncaughtListener = new CopyOnWriteArraySet<UncaughtExceptionListener>();
            
            // Event Dispatch Thread
            UncaughtExceptionHandler eh = new UncaughtExceptionHandler();
            System.setProperty("sun.awt.exception.handler", eh.getClass().getName());
            
            // Does not work for EDT if a modal dialog is shown.
            Thread.setDefaultUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler() {
                    public void uncaughtException(Thread t, Throwable e) {
                        handleUncaughtException(t, e);
                    }
            });
        }
        return hsUncaughtListener;
    } // init()
    
    /**
     * Method is called by JVM to handle uncaught exceptions in EDT. 
     * See notes for the class.
     * @param e exception
     */
    public void handle(Throwable e) {
        //System.out.println("Uncaught exception in EDT: " + e);
        handleUncaughtException(Thread.currentThread(), e);
    } // handle()
    
    /**
     * Single entry for all uncaught exceptions: EDT and other threads.
     * @param t thread
     * @param e exception
     */
    private static void handleUncaughtException(Thread t, Throwable e) {
        //System.out.println("Uncaught exception in " + t.getName() + "\n" + e);
        //System.out.println("Class: FriendlyAgent\n");
        logger.error(ThrowableFormatter.format("Uncaught exception", e));
        if (printToConsoleUncaught) {
            System.err.println("FriendlyAgent: " +
                    "uncaught exception in thread " + t.getName());
            e.printStackTrace();
        }
        for (UncaughtExceptionListener l : hsUncaughtListener) {
            l.uncaughtException(t, e);
        }
    } // handleUncaughtException()
    
} // class UncaughtExceptionHandler
