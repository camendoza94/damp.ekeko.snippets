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

package org.openide.text;

import java.util.*;
import java.io.*;

import org.openide.util.RequestProcessor;

/** Class that holds line information about one document.
* Defines operations that can be executed on the objects, the implementation
* can change when we find that it is too slow.
*
* @author Jaroslav Tulach
*/
final class LineStruct extends Object {
    /** max number of lines to work with */
    private static final int MAX = Integer.MAX_VALUE / 2;

    /** Holding the original and current number of lines.
    */
    private static final class Info extends Object {
        /** constants for distintion of the type of info */
        public static final int AREA_ORIGINAL = 0;
        public static final int AREA_INSERT = 1;
        public static final int AREA_REMOVE = -1;

        /** original number */
        public int original;
        /** current number */
        public int current;

        public Info (int o, int c) {
            original = o;
            current = c;
        }

        /** Finds the type.
        */
        public int type () {
            if (current == original) return AREA_ORIGINAL;
            if (current == 0) return AREA_REMOVE;
            if (original == 0) return AREA_INSERT;
            throw new InternalError("Original: " + original + " current: " + current); // NOI18N
        }

        /** Performs insert on this Info object.
        * @param pos position to insert to
        * @param count how much objects to insert
        * @param it iterator that just returned this object
        * @return how much lines to insert after this object
        */
        public int insert (int pos, int count, ListIterator it) {
            switch (type ()) {
            case AREA_INSERT:
                // insert area, add to it all
                current += count;
                return 0;
            case AREA_ORIGINAL:
                if (pos == current) {
                    // if the insert position is at the end,
                    // then let all the characters be added by next
                    // item
                    return count;
                }
                if (pos == 0) {
                    // prepend the insert area before the current
                    // Info in the chain
                    Info ni = new Info (original, original);
                    original = 0;
                    current = count;
                    it.add (ni);
                    // everything has been prepended
                    return 0;
                }
                // we have to devided the interval to two parts
                // and insert insert block between them
                Info ni = new Info (original - pos, original - pos);
                // the area from 0 to pos
                original = current = pos;
                // insert the insert area
                it.add (new Info (0, count));
                // the rest of the area
                it.add (ni);
                return 0;
            case AREA_REMOVE:
                // supposing that pos == 0
                if (pos != 0) {
                    throw new IllegalStateException ("Pos: " + pos); // NOI18N
                }

                // check the previous Info if it cannot be merged
                Info prev = (Info)it.previous (); // current item

                // prev.type () == AREA_REMOVE => prev is used
                // only if the it.hasPrevious is true and prev
                // is changed again

                if (it.hasPrevious ()) {
                    prev = (Info)it.previous (); // previous
                    it.next (); // previous
                }

                it.next (); // current

                // inserted lines can be put instead of the orignal
                // ones
                if (count < original) {
                    if (prev.type () == AREA_ORIGINAL) {
                        prev.original += count;
                        prev.current += count;

                        // modify this remove object
                        original -= count;
                    } else {
                        ni = new Info (original - count, 0);
                        // turn this to regular part
                        original = current = count;
                        // insert the new delete part
                        it.add (ni);
                    }
                    // everything processed
                    return 0;
                } else {
                    if (prev.type () == AREA_ORIGINAL) {
                        prev.current += original;
                        prev.original += original;
                        it.remove ();
                        return count - original;
                    } else {
                        // turn whole delete part to regular one
                        current = original;
                        // the rest of characters to proceed
                        return count - current;
                    }
                }
            default:
                throw new IllegalStateException ("Type: " + type ()); // NOI18N
            }
        }

        /** A method that handles the delete operation.
        * @param pos position in the Info block where delete started
        * @param info 
        *   info.original the amount of lines to be deleted
        *   info.current the amount of lines that should be later marked as deleted
        * @param it the iterator that previously returned this instance
        * @return 
        *   info.original the amount of lines to be yet deleted
        *   info.current the amount of lines that needs to be later marked as deleted
        *     this will be put before the 
        */
        public Info delete (int pos, Info info, ListIterator it) {
            switch (type ()) {
            case AREA_ORIGINAL:
                if (pos != 0) {
                    // specials
                    int size = current - pos;
                    current = original = pos;
                    if (size >= info.original) {
                        // delete is whole only in this block
                        Info ni = new Info (size, size);
                        it.add (ni);
                        info.current += info.original;
                        info.original = 0;
                        return info;
                    } else {
                        // something is resting after this block
                        info.original -= size;
                        info.current += size;
                        return info;
                    }
                } else {
                    // deleting from first position
                    if (current >= info.original) {
                        // something is resting from me (at the end)

                        // number of lines to mark as deleted
                        info.current += info.original;

                        // number of lines in this block is decreased
                        current -= info.original;
                        original = current;


                        // number of lines to be yet deleted
                        info.original = 0;

                        return info;
                    } else {
                        // I am completelly deleted
                        it.remove ();

                        // number of lines to mark as deleted
                        info.current += current;
                        info.original -= current;
                        return info;
                    }
                }
            case AREA_INSERT:
                if (pos != 0) {
                    // specials
                    int size = current - pos;
                    if (size >= info.original) {
                        // delete is whole only in this block
                        current -= info.original;

                        info.original = 0;

                        return info;
                    } else {
                        // something is resting after this block
                        current = pos;

                        info.original -= size;
                        return info;
                    }
                } else {
                    // deleting from first position
                    if (current >= info.original) {
                        // something is resting from me (at the end)

                        // number of lines in this block is decreased
                        current -= info.original;

                        // number of lines to be yet deleted
                        info.original = 0;

                        it.remove ();

                        return info;
                    } else {
                        // I am completelly deleted
                        it.remove ();

                        // how much lines to be deleted yet
                        info.original -= current;
                        return info;
                    }
                }
            case AREA_REMOVE:
                // only derease the number of lines that needs to be deleted
                // because this area can absorb some
                original += info.current;
                info.current = 0;
                return info;
            default:
                throw new InternalError("Type: " + type ()); // NOI18N
            }
        }
    }

    /** processor for all requests */
    private static final RequestProcessor PROCESSOR = new RequestProcessor ();

    /** list of Info objects that represents the whole document 
     * @associates Info*/
    private LinkedList list;

    /** Constructor.
    */
    public LineStruct () {
        list = new LinkedList ();
        list.add (new Info (MAX, MAX));
    }

    /** Converts original numbering to the new one.
    * @param line the line number in the original
    * @return line number in the new numbering
    */
    public int originalToCurrent (int line) {
        // class to compute in the request processor thread
        class Compute extends Object implements Runnable {
            public int result;

            public Compute (int i) {
                result = i;
            }

            public void run () {
                result = originalToCurrentImpl (result);
            }
        }

        Compute c = new Compute (line);

        // post the computation and wait till it is finished
        PROCESSOR.post (c).waitFinished ();

        // return result
        return c.result;
    }

    /** Inserts line(s) at given position.
    * @param line the line number in current numbering
    * @param count number of lines inserted
    */
    public void insertLines (final int line, final int count) {
        PROCESSOR.post (new Runnable () {
                            public void run () {
                                insertLinesImpl (line, count);
                            }
                        });
    }

    /** Method that deletes some lines in the current state of
    * the document.
    *
    * @param line the line number in current numbering
    * @param 
    */
    public void deleteLines (final int line, final int count) {
        PROCESSOR.post (new Runnable () {
                            public void run () {
                                deleteLinesImpl (line, count);
                            }
                        });
    }

    /** Converts original numbering to the new one.
    * @param line the line number in the original
    * @return line number in the new numbering
    */
    private int originalToCurrentImpl (int line) {
        Iterator it = list.iterator ();
        int cur = 0;
        for (;;) {
            Info i = (Info)it.next ();
            if (i.original > line) {
                // ok we found the segment that contained this line
                return line > i.current ? cur + i.current : cur + line;
            }
            cur += i.current;
            line -= i.original;
        }
    }

    /** Inserts line(s) at given position.
    * @param line the line number in current numbering
    * @param count number of lines inserted
    */
    private void insertLinesImpl (int line, int count) {
        ListIterator it = list.listIterator ();
        for (;;) {
            Info i = (Info)it.next ();
            if (i.current >= line) {
                for (;;) {
                    count = i.insert (line, count, it);
                    if (count == 0) {
                        return;
                    }
                    i = (Info)it.next ();
                    line = 0;
                }
            }
            line -= i.current;
        }
    }

    /** Method that deletes some lines in the current state of
    * the document.
    *
    * @param line the line number in current numbering
    * @param 
    */
    private void deleteLinesImpl (int line, int count) {
        ListIterator it = list.listIterator ();
        for (;;) {
            Info i = (Info)it.next ();
            if (i.current >= line) {
                // information to hold both the number of lines to delete (original)
                // and the number of lines to mark as delete at the end (current)
                Info stat = new Info (count, 0);
                for (;;) {
                    stat = i.delete (line, stat, it);

                    if (stat.original == 0) {
                        break;
                    }

                    i = (Info)it.next ();
                    line = 0;
                }

                // insert the amount of lines to mark deleted before current position
                if (stat.current > 0) {
                    Info prev = (Info)it.previous ();
                    boolean hasPrev = it.hasPrevious ();

                    if (hasPrev) {
                        prev = (Info)it.previous ();
                    }

                    if (prev.current == 0) {
                        prev.original += stat.current;
                    } else {
                        if (hasPrev) {
                            it.next ();
                        }
                        it.add (new Info (stat.current, 0));
                    }
                }
                return;
            }
            line -= i.current;
        }
    }

    /** Prints the struct.
    *
    public void print () {
      PROCESSOR.post (new Runnable () {
        public void run () {
          printImpl ();
        }
      });
}

    /** Prints the lines in the current stream
    *
    private void printImpl () {
      Iterator it = list.iterator ();
      System.out.println("vvvvvvvvvvv");
      while (it.hasNext ()) {
        Info i = (Info)it.next ();
        switch (i.type ()) {
          case Info.AREA_INSERT: System.out.print("Ins: "); break;
          case Info.AREA_REMOVE: System.out.print("Rem: "); break;
          case Info.AREA_ORIGINAL: System.out.print("Org: "); break;
        }
        System.out.println(i.current + " - " + i.original);
      }
      System.out.println("^^^^^^^^^^^");
}


    public static void main (String[] args) throws Exception {
      LineStruct ls = new LineStruct ();
      
      BufferedReader r = new BufferedReader (new FileReader ("z:/X"));
      
      try {
        for (;;) {
          String s = r.readLine ();

          if (s == null) break;
          StringTokenizer st = new StringTokenizer (s);
          String pl = st.nextToken ();
          String s1 = st.nextToken ();
          String s2 = st.nextToken ();

          int i1 = Integer.valueOf (s1).intValue ();
          int i2 = Integer.valueOf (s2).intValue ();

          if (pl.equals ("+")) {
            ls.insertLinesImpl (i1, i2);
          } else {
            if (pl.equals ("?")) {
              int rr = ls.originalToCurrentImpl (i1);
              System.out.println("Orig: " + i1 + " now: " + rr);
              continue;
            } else {
              ls.deleteLinesImpl (i1, i2);
            }
          }

          ls.printImpl ();
        }
      } catch (NoSuchElementException ex) {
        System.out.println("No such element");
        ex.printStackTrace();
      }
} */

}

/*
* Log
*  6    Gandalf   1.5         1/13/00  Ian Formanek    NOI18N
*  5    Gandalf   1.4         10/22/99 Ian Formanek    NO SEMANTIC CHANGE - Sun 
*       Microsystems Copyright in File Comment
*  4    Gandalf   1.3         10/10/99 Petr Hamernik   console debug messages 
*       removed.
*  3    Gandalf   1.2         8/11/99  Jaroslav Tulach Survives when the 
*       document becomes empty.
*  2    Gandalf   1.1         7/30/99  Jaroslav Tulach getOriginal & getCurrent 
*       in Line
*  1    Gandalf   1.0         7/27/99  Jaroslav Tulach 
* $
*/