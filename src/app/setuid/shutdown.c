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

char *args[] = {
  "/sbin/shutdown",
  "-h",
  "now",
  0
};


main(int argc, char** argv, char** env)
{ 
  struct passwd *pw = getpwnam("root");

  setenv("USER", pw->pw_name, 1);
  setenv("LOGNAME", pw->pw_name, 1);
  setenv("HOME", pw->pw_dir, 1);

  if (setuid(pw->pw_uid)) {
    printf("sorry\n");
    exit(-1);
  }
  setgid(pw->pw_gid);	
  execve ("/sbin/shutdown", args, env);	
}
