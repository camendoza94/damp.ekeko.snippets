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

package org.netbeans.core.execution;

import org.openide.execution.ExecutorTask;

/** Informs about process state
*
* @author Ales Novak
* @version 0.10 Mar 04, 1998
*/
class ExecutionEvent extends java.util.EventObject {
    /** generated Serialized Version UID */
    static final long serialVersionUID = -9181112840849353114L;
    /** the process that the event notifies about*/
    private DefaultSysProcess proc;
    /**
    * @param source is a source of the event
    * @param proc is a Process that this event notifies about
    */
    public ExecutionEvent(Object source, DefaultSysProcess proc) {
        super(source);
        this.proc = proc;
    }

    /**
    * @return Process from the event
    */
    public DefaultSysProcess getProcess() {
        return proc;
    }
}

/*
 * Log
 *  4    Gandalf   1.3         10/22/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  3    Gandalf   1.2         10/1/99  Ales Novak      major change of 
 *       execution
 *  2    Gandalf   1.1         6/8/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  1    Gandalf   1.0         1/5/99   Ian Formanek    
 * $
 */
