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


#include <stdlib.h>
#include <time.h>


main(int argc, char *argv[]) {
  double Max;
  Max = atof(argv[1]);
  srand((int)(time(NULL)));
  printf("%d\n",1+(int)(Max*rand()/(RAND_MAX+1.0)));
}
