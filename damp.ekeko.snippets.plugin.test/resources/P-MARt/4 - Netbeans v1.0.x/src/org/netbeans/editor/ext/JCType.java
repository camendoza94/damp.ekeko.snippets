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

import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.List;
import java.util.Comparator;
import java.util.Iterator;

/**
* Java completion type
*
* @author Miloslav Metelka
* @version 1.00
*/

public interface JCType extends Comparable {

    /** Class */
    public JCClass getClazz();

    /** Array depth */
    public int getArrayDepth();

    /** Get the string containing either the name or full name
    * plus possible square bracket pairs
    * @param useFullName Use full name (including the package) instead of the name
    *   without the package
    */
    public String format(boolean useFullName);

}


/*
 * Log
 *  4    Gandalf   1.3         11/8/99  Miloslav Metelka 
 *  3    Gandalf   1.2         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  2    Gandalf   1.1         10/10/99 Miloslav Metelka 
 *  1    Gandalf   1.0         9/15/99  Miloslav Metelka 
 * $
 */

