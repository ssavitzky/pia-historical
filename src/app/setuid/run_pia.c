/**** run_pia
** 	$Id$
**
**	Run ~pia/bin/pia as user pia.  Must be setuid ROOT.
**	Install with: chown root run_pia; chmod u+s run_pia
**
**	If installed owned by user "pia", perl somehow knows that it's
**	running setuid, and complains.  Deep magic.
*/

#include <stdlib.h>
#include <sys/types.h>
#include <pwd.h>
#include <stdio.h>

char *args[] = {
  /*  "/bin/sh", "run_pia", "-c" */
  /*  "./pia/bin/pia &> ./.pia/log", */

  "/usr/bin/perl", "run_pia", 
  "./pia/lib/perl/pia.pl",
  "-l", "./.pia/log",
  0
};


main(int argc, char** argv, char** env)
{ 
  struct passwd *pw = getpwnam("pia");

  setenv("USER", pw->pw_name, 1);
  setenv("LOGNAME", pw->pw_name, 1);	
  setenv("HOME", pw->pw_dir, 1);

  args[1]=argv[0];			/* show correct name */
  setgid(pw->pw_gid);	
  if (setuid(pw->pw_uid)) {
    fprintf(stderr, "sorry\n");
    exit(-1);
  }
  chdir(pw->pw_dir);
  execve (args[0], args+1, env);	
}
