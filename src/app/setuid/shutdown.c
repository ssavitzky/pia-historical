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
