/*
 * @(#)QSortAlgorithm.java	1.6 96/12/06
 *
 * Copyright (c) 1994-1996 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

/**
 *
 * Copyright (C) 1997-1998, Ricoh Silicon Valley, Inc.  - rsv.ricoh.com
 * All Rights Reserved
 *
 * Class: RH_LexEntrySort: sort the lexicon entries for a document
 * Author: Jamey Graham (jamey@rsv.ricoh.com)
 * Date: 4.10.98
 *
 */
package ricoh.rh;

import java.util.*;

/**
 * A quick sort demonstration algorithm
 * SortAlgorithm.java
 *
 * @author James Gosling
 * @author Kevin A. Smith
 * @version 	@(#)QSortAlgorithm.java	1.3, 29 Feb 1996
 *
 * @modified 09-16-97 for Reader's Helper Concepts, to sort them directly
 * @author Jamey Graham
 * @description sorts concepts by value so that the first concept is the one with the highest value 
 */
public class RH_MatchVectorSort // extends SortAlgorithm 
{

    /**
     * A version of pause() that makes it easier to ensure that we pause
     * exactly the right number of times.
     */
    private boolean pauseTrue(int lo, int hi) throws Exception {
       //super.pause(lo, hi);
      return true;
    }

   /** This is a generic version of C.A.R Hoare's Quick Sort 
    * algorithm.  This will handle arrays that are already
    * sorted, and arrays with duplicate keys.<BR>
    *
    * If you think of a one dimensional array as going from
    * the lowest index on the left to the highest index on the right
    * then the parameters to this function are lowest index or
    * left and highest index or right.  The first time you call
    * this function it will be with the parameters 0, a.length - 1.
    *
    * @param a       an integer array
    * @param lo0     left boundary of array partition
    * @param hi0     right boundary of array partition
    */
   void QuickSort(Vector[] entries, int lo0, int hi0) throws Exception  // RH Mod
   {
     int lo = lo0;
     int hi = hi0;
     double mid; // RH Mod

      if ( hi0 > lo0)  {

         /* Arbitrarily establishing partition element as the midpoint of
          * the array.
          */
	Double tmp = (Double)entries[ ( lo0 + hi0 ) / 2 ].elementAt(1); // RH Mod
	mid = tmp.doubleValue();

         // loop through the array until indices cross
         while( lo <= hi )
         {
            /* find the first element that is greater than or equal to 
             * the partition element starting from the left Index.
             */
	     tmp =(Double)entries[lo].elementAt(1);
	     while( ( lo < hi0 ) && pauseTrue(lo0, hi0) && ( tmp.doubleValue() < mid )) { // RH Mod
		 ++lo;
		 tmp =(Double)entries[lo].elementAt(1);
	     }

            /* find an element that is smaller than or equal to 
             * the partition element starting from the right Index.
             */
	     tmp =(Double)entries[hi].elementAt(1);
	     while( ( hi > lo0 ) && pauseTrue(lo0, hi0) && ( tmp.doubleValue() > mid )) { // RH Mod
		 --hi;
		 tmp =(Double)entries[hi].elementAt(1);
	     }

            // if the indexes have not crossed, swap
            if( lo <= hi ) 
            {
               swap(entries, lo, hi); // RH Mod
               ++lo;
               --hi;
            }
         }

         /* If the right index has not reached the left side of array
          * must now sort the left partition.
          */
         if( lo0 < hi )
            QuickSort( entries, lo0, hi ); // RH Mod

         /* If the left index has not reached the right side of array
          * must now sort the right partition.
          */
         if( lo < hi0 )
            QuickSort( entries, lo, hi0 ); // RH Mod

      }
   }

   private void swap(Vector[] a, int i, int j) // RH Mod
   {
       Vector T; // RH Mod
      T = a[i]; 
      a[i] = a[j];
      a[j] = T;

   }

   public void sort(Vector[] a) throws Exception // RH Mod
   {
      QuickSort(a, 0, a.length - 1);
   }
}

