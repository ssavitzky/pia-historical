/**** run_pia
** 	$Id$
**
**	Run ~pia/bin/pia as user pia.  Must be setuid ROOT.
**	Install with: chown root run_pia; chmod u+s run_pia
**
**	If installed owned by user "pia", perl somehow knows that it's
**	running setuid, and complains.  Deep magic.
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
#include <sys/types.h>
#include <pwd.h>
#include <stdio.h>

char *args[] = {
#if 1
  "/bin/sh", "./pia/bin/pia", "-c",
  "./pia/bin/pia &> ./.pia/log",
#else
  "/usr/bin/perl", "run_pia", 
  "./pia/lib/perl/pia.pl",
  "-l", "./.pia/log",
#endif
  0
};


main(int argc, char** argv, char** env)
{ 
  struct passwd *pw = getpwnam("pia");

  setenv("USER", pw->pw_name, 1);
  setenv("LOGNAME", pw->pw_name, 1);	
  setenv("HOME", pw->pw_dir, 1);
  setenv("PIA_DIR", "/home/pia/pia",1);

  args[1]=argv[0];			/* show correct name */
  setgid(pw->pw_gid);	
  if (setuid(pw->pw_uid)) {
    fprintf(stderr, "sorry\n");
    exit(-1);
  }
  chdir(pw->pw_dir);
  execv (args[0], args+1);	
}
