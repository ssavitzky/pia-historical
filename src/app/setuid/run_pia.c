#include <stdlib.h>
#include <sys/types.h>
#include <pwd.h>

char *args[] = {
  "/usr/local/bin/perl", "-U",
  "/home/pia/bin/pia",
  0
};


main(int argc, char** argv, char** env)
{ 
  struct passwd *pw = getpwnam("pia");

  setenv("USER", pw->pw_name, 1);
  setenv("LOGNAME", pw->pw_name, 1);	/* doesn't work with /bin/bash */
  setenv("HOME", pw->pw_dir, 1);

  if (setuid(pw->pw_uid)) {
    printf("sorry\n");
    exit(-1);
  }
  setgid(pw->pw_gid);	
  chdir(pw->pw_dir);
  execve (args[0], args, env);	
}
