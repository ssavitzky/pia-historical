/** random-num
 *  Return a random number from 1 to n
 *  Where n is supplied on the command line
 * 
 * Random number generator is seeded with
 * the current time in seconds
 *
 * Marko Balabanovic
 * Ricoh Silicon Valley
 * 21 Sept 1998
 */

/*****************************************************************************
 * The contents of this file are subject to the Ricoh Source Code Public
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.risource.org/RPL
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * This code was initially developed by Ricoh Silicon Valley, Inc.  Portions
 * created by Ricoh Silicon Valley, Inc. are Copyright (C) 1995-1999.  All
 * Rights Reserved.
 *
 * Contributor(s):
 *
 ***************************************************************************** 
*/



#include <stdlib.h>
#include <time.h>


main(int argc, char *argv[]) {
  double Max;
  Max = atof(argv[1]);
  srand((int)(time(NULL)));
  printf("%d\n",1+(int)(Max*rand()/(RAND_MAX+1.0)));
}
