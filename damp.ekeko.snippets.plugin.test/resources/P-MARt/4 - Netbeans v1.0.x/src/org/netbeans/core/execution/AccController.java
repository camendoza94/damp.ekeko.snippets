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

import java.security.ProtectionDomain;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.ArrayList;
import java.util.Iterator;
import java.lang.reflect.Field;

/** Tries to get an IOProtectionDomain from an AccessControlContext.
*
* @author Ales Novak
*/
class AccController {

    /** array of ProtectionDomains */
    static Field context;

    static Field getContextField() throws Exception {
        if (context == null) {
            Field ctx = AccessControlContext.class.getDeclaredField("context"); // NOI18N
            ctx.setAccessible(true);
            context = ctx;
        }
        return context;
    }


    static ProtectionDomain[] getDomains(AccessControlContext acc) throws Exception {
        Object o = getContextField().get(acc);
        if (o.getClass() == Object[].class) { // 1.2.1 fix
            Object[] array = (Object[]) o;
            ProtectionDomain[] domains = new ProtectionDomain[array.length];
            for (int i = 0; i < array.length; i++) {
                domains[i] = (ProtectionDomain) array[i];
            }
            return domains;
        }
        return (ProtectionDomain[]) o;
    }

    /** @return an IOPermissionCollection or <tt>null</tt> if not found */
    static IOPermissionCollection getIOPermissionCollection() {
        try {
            ProtectionDomain[] pds = getDomains(AccessController.getContext());
            PermissionCollection pc;
            for (int i = 0; i < pds.length; i++) {
                pc = pds[i].getPermissions();
                if (pc instanceof IOPermissionCollection) {
                    return (IOPermissionCollection) pc;
                }
            }
            return null;
        } catch (final Exception e) {
            if (Boolean.getBoolean("netbeans.debug.exceptions")) { // NOI18N
                javax.swing.SwingUtilities.invokeLater(new Runnable () {
                                                           public void run () {
                                                               e.printStackTrace();
                                                           }
                                                       });
            }
            return null;
        }
    }
}

/*
 * Log
 *  9    src-jtulach1.8         1/12/00  Ales Novak      i18n
 *  8    src-jtulach1.7         1/4/00   Ales Novak      1.2.1 fix
 *  7    src-jtulach1.6         12/29/99 Jaroslav Tulach Exception in invoke 
 *       later, so build starts.
 *  6    src-jtulach1.5         12/28/99 Jaroslav Tulach Now the build can start,
 *       but I do not know where is the real problem. 
 *  5    src-jtulach1.4         12/22/99 Ales Novak      #5093
 *  4    src-jtulach1.3         12/22/99 Ales Novak      #5061
 *  3    src-jtulach1.2         10/22/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  2    src-jtulach1.1         3/31/99  Ales Novak      
 *  1    src-jtulach1.0         3/24/99  Ales Novak      
 * $
 */
