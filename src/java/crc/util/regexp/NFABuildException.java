/*
 * NFABuildException.java
 *
 * Copyright (C) 1997 Shugo Maeda
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
 */

package crc.util.regexp;

/**
 * Signals an exception has occured when building NFA.
 */
public class NFABuildException extends Exception {

    /**
     * Constructs a NFABuildException with no detail message.
     */
    public NFABuildException() {
	super();
    }

    /**
     * Constructs a NFABuildException with the specified detail message. 
     */
    public NFABuildException(String s) {
	super(s);
    }
}
