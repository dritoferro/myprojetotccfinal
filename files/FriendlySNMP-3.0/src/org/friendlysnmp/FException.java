/*
 * File: FException.java
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
 * $Id: FException.java,v 1.17 2014/01/11 02:19:22 mg Exp $
 */
package org.friendlysnmp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exception wrapper to report SNMP error. 
 * 
 * @version $Revision: 1.17 $
 */
public class FException extends Exception {
    
    /**
     * Logger object.
     */
    private static final Logger logger = LoggerFactory.getLogger(FException.class);
    
    /**
     * Constructor.
     * 
     * @param msg exception message.
     */
    public FException(String msg) {
        super(msg);
        logger.error(msg);
    }

    /**
     * Constructor.
     * 
     * @param format exception message format
     * @param args exception message arguments
     */
    public FException(String format, Object...args) {
        super(String.format(format, args));
        logger.error(getMessage());
    }
    
    /**
     * Constructor.
     * 
     * @param msg exception message
     * @param cause cause exception
     */
    public FException(String msg, Throwable cause) {
        super(msg, cause);
        logger.error(msg + "\n" + ThrowableFormatter.format(cause));
    }

} // class FException
