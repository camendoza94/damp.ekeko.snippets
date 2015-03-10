/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is Forte for Java, Community Edition. The Initial
 * Developer of the Original Code is Sun Microsystems, Inc. Portions
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.editor.ext;

/**
* Java completion package
*
* @author Miloslav Metelka
* @version 1.00
*/

public interface JCPackage extends Comparable {

    /** Get full name of this package */
    public String getName();

    /** Get last part of the name - the string after
    * the last dot.
    */
    public String getLastName();

    /** Get classes contained in this package */
    public JCClass[] getClasses();

    /** Set the list of the classes that this package contains */
    public void setClasses(JCClass[] classes);

    /** Get the count of dots in the name of the package */
    public int getDotCount();

}

/*
 * Log
 *  2    Gandalf   1.1         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  1    Gandalf   1.0         9/15/99  Miloslav Metelka 
 * $
 */

