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

main(int argc, char** argv, char** env)
{ 
  struct passwd *pw = getpwnam("pia");

  setenv("USER", "pia", 1);
  setenv("LOGNAME", "pia", 1);		/* doesn't work with /bin/bash */
  setenv("HOME", pw->pw_dir, 1);

  /* Could do this with setregid, setreuid, but this works if we're root. */
  setgid(pw->pw_gid);			/* Have to do this first. */
  if (setuid(pw->pw_uid)) {		/* because only root can do it. */
    printf("sorry\n");
    exit(-1);
  }
  argv[0] = "-";			/* and we're not root anymore! */
  /* pw->pw_shell doesn't work if it's set to "false" to prevent logins. */
  execve ("/bin/bash", argv, env);	
}
