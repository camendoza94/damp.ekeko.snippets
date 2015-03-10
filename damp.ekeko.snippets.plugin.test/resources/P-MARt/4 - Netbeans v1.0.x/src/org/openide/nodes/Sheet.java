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

package org.openide.nodes;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeEvent;
import java.util.*;

import org.openide.util.NbBundle;

/** Support for creation of property sets. Allows easy
* addition, modification, and deletion of properties. Also
* permits listening on changes of contained properties.
*
* @author Jaroslav Tulach, Dafe Simonek
*/
public final class Sheet extends Object {
    /** list of sets (Sheet.Set) 
     * @associates Set*/
    private ArrayList sets;
    /** array of sets */
    private Node.PropertySet[] array;
    /** support for changes */
    private PropertyChangeSupport supp = new PropertyChangeSupport (this);
    /** change listener that is attached to each set added to this object */
    private PropertyChangeListener propL = new PropertyChangeListener () {
                                               public void propertyChange (PropertyChangeEvent ev) {
                                                   supp.firePropertyChange (null, null, null);
                                               }
                                           };
    /** Name for regular Bean property set. */
    public static final String PROPERTIES = "properties"; // NOI18N
    /** Name for expert Bean property set. */
    public static final String EXPERT = "expert"; // NOI18N

    /** Default constructor.
    */
    public Sheet() {
        this (new ArrayList (2));
    }

    /** Copy constrcutor.
    * @param ar array to use
    */
    private Sheet(ArrayList ar) {
        sets = ar;
    }

    /** Obtain the array of property sets.
    * @return the array
    */
    public final Node.PropertySet[] toArray () {
        Node.PropertySet[] l = array;
        if (l != null) return l;

        synchronized (this) {
            if (array != null) return array;

            array = new Node.PropertySet [sets.size ()];
            sets.toArray (array);
            return array;
        }
    }


    /** Create a deep copy of the sheet. Listeners are not copied.
    * @return the cloned object */
    public synchronized Sheet cloneSheet () {
        int len = sets.size ();
        ArrayList l = new ArrayList (len);
        for (int i = 0; i < len; i++) {
            l.add (((Set)sets.get (i)).cloneSet ());
        }
        return new Sheet (l);
    }

    /** Find the property set with a given name.
    * @param name name of the set
    * @return the property set, or <code>null</code> if no such set exists
    */
    public synchronized Set get (String name) {
        int indx = findIndex (name);
        return indx == -1 ? null : (Set)sets.get (indx);
    }

    /** Add a property set. If the set does not yet exist in the sheet,
    * inserts a new set with the implied name. Otherwise the old set is replaced
    * by the new one.
    *
    * @param set to add
    * @return the previous set with the same name, or <code>null</code> if this is a fresh insertion
    */
    public synchronized Set put (Set set) {
        int indx = findIndex (set.getName ());
        Set removed;
        if (indx == -1) {
            sets.add (set);
            removed = null;
        } else {
            removed = (Set)sets.set (indx, set);
        }

        // listeners
        set.addPropertyChangeListener (propL);
        if (removed != null) {
            set.removePropertyChangeListener (propL);
        }
        refresh ();

        return removed;
    }

    /** Remove a property set from the sheet.
    * @param set name of set to remove
    * @return removed set, or <code>null</code> if the set could not be found
    */
    public synchronized Set remove (String set) {
        int indx = findIndex (set);
        if (indx != -1) {
            Set s = (Set)sets.remove (indx);
            s.removePropertyChangeListener (propL);
            refresh ();
            return s;
        } else {
            return null;
        }
    }

    /** Convenience method to create new sheet with only one empty set, named {@link #PROPERTIES}.
    * Display name and hint are settable via the appropriate bundle.
    *
    * @return a new sheet with default property set
    */
    public static Sheet createDefault () {
        Sheet newSheet = new Sheet();
        // create default property set
        newSheet.put(createPropertiesSet ());
        return newSheet;
    }

    /** Convenience method to create new sheet set named {@link #PROPERTIES}.
    *
    * @return a new properties sheet set
    */
    public static Sheet.Set createPropertiesSet () {
        Sheet.Set ps = new Sheet.Set ();
        ps.setName(PROPERTIES);
        ps.setDisplayName(Node.getString("Properties"));
        ps.setShortDescription(Node.getString("HINT_Properties"));
        return ps;
    }

    /** Convenience method to create new sheet set named {@link #EXPERT}.
    *
    * @return a new expert properties sheet set
    */
    public static Sheet.Set createExpertSet () {
        Sheet.Set ps = new Sheet.Set ();
        ps.setName(EXPERT);
        ps.setDisplayName(Node.getString("Expert"));
        ps.setShortDescription(Node.getString("HINT_Expert"));
        return ps;
    }


    /** Add a change listener.
    * @param l the listener */
    public void addPropertyChangeListener (PropertyChangeListener l) {
        supp.addPropertyChangeListener (l);
    }

    /** Remove a change listener.
    * @param l the listener */
    public void removePropertyChangeListener (PropertyChangeListener l) {
        supp.removePropertyChangeListener (l);
    }

    /** Finds index for property set for given name.
    * @param name of the property
    * @return the index or -1 if not found
    */
    private int findIndex (String name) {
        int s = sets.size ();
        for (int i = 0; i < s; i++) {
            Node.PropertySet p = (Node.PropertySet)sets.get (i);
            if (p.getName ().equals (name)) {
                return i;
            }
        }
        return -1;
    }

    /** Refreshes and fire info about the set
    */
    private void refresh () {
        array = null;
        supp.firePropertyChange (null, null, null);
    }

    /********* Inner classes **********/

    /** A set of Bean properties.
    */
    public static final class Set extends Node.PropertySet {
        /** list of properties (Node.Property) 
         * @associates Property*/
        private ArrayList props;
        /** array of properties */
        private Node.Property[] array;
        /** change listeners listening on this set */
        private PropertyChangeSupport supp = new PropertyChangeSupport (this);

        /** Default constructor.
        */
        public Set () {
            this (new ArrayList ());
        }

        /** @param al array list to use for this property set
        */
        private Set (ArrayList al) {
            props = al;
        }

        /** Clone the property set.
        * @return the clone
        */
        public synchronized Set cloneSet () {
            return new Set ((ArrayList)props.clone ());
        }

        /** Get a property by name.
        * @param name name of the property
        * @return the first property in the list that has this name, <code>null</code> if not found
        */
        public Node.Property get (String name) {
            int indx = findIndex (name);
            return indx == -1 ? null : (Node.Property)props.get (indx);
        }

        /** Get all properties in this set.
        * @return the properties
        */
        public Node.Property[] getProperties () {
            Node.Property[] l = array;
            if (l != null) return l;

            synchronized (this) {
                if (array != null) return array;

                array = new Node.Property [props.size ()];
                props.toArray (array);
                return array;
            }
        }

        /** Add a property to this set, replacing any old one with the same name.
        * @param p the property to add
        * @return the property with the same name that was replaced, or <code>null</code> for a fresh insertion
        */
        public synchronized Node.Property put (Node.Property p) {
            int indx = findIndex (p.getName ());
            Node.Property removed;
            if (indx != -1) {
                // replaces the original one
                removed = (Node.Property)props.set (indx, p);
            } else {
                // adds this to the end
                props.add (p);
                removed = null;
            }
            // clears computed array and fires into about change of properties
            refresh ();
            return removed;
        }

        /** Add several properties to this set, replacing old ones with the same names.
        *
        * @param ar properties to add
        */
        public synchronized void put (Node.Property[] ar) {
            for (int i = 0; i < ar.length; i++) {
                Node.Property p = ar[i];
                p = ar[i];
                int indx = findIndex (p.getName ());
                if (indx != -1) {
                    // replaces the original one
                    props.set (indx, p);
                } else {
                    // adds this to the end
                    props.add (p);
                }
            }
            // clears computed array and fires into about change of properties
            refresh ();
        }

        /** Remove a property from the set.
        * @param name name of the property to remove
        * @return the removed property, or <code>null</code> if it was not there to begin with
        */
        public synchronized Node.Property remove (String name) {
            int indx = findIndex (name);
            if (indx != -1) {
                try {
                    return (Node.Property)props.remove (indx);
                } finally {
                    // clears computed array and fires into about change of properties
                    refresh ();
                }
            } else {
                return null;
            }
        }

        /** Add a property change listener.
        * @param l the listener to add */
        public void addPropertyChangeListener (PropertyChangeListener l) {
            supp.addPropertyChangeListener (l);
        }

        /** Remove a property change listener.
        * @param l the listener to remove */
        public void removePropertyChangeListener (PropertyChangeListener l) {
            supp.removePropertyChangeListener (l);
        }

        /** Finds index for property with specified name.
        * @param name of the property
        * @return the index or -1 if not found
        */
        private int findIndex (String name) {
            int s = props.size ();
            for (int i = 0; i < s; i++) {
                Node.Property p = (Node.Property)props.get (i);
                if (p.getName ().equals (name)) {
                    return i;
                }
            }
            return -1;
        }

        /** Notifies change of properties.
        */
        private void refresh () {
            array = null;
            supp.firePropertyChange (null, null, null);
        }
    } // end of Set

}

/*
* Log
*  6    Gandalf   1.5         1/12/00  Jesse Glick     NOI18N
*  5    Gandalf   1.4         10/22/99 Ian Formanek    NO SEMANTIC CHANGE - Sun 
*       Microsystems Copyright in File Comment
*  4    Gandalf   1.3         6/8/99   Ian Formanek    ---- Package Change To 
*       org.openide ----
*  3    Gandalf   1.2         5/8/99   Ian Formanek    Added convenience methods
*       createPropertiesSet and createExpertSet
*  2    Gandalf   1.1         3/17/99  Jesse Glick     [JavaDoc]
*  1    Gandalf   1.0         1/5/99   Ian Formanek    
* $
*/
