#include <stdlib.h>
#include <sys/types.h>
#include <pwd.h>

main(int argc, char** argv, char** env)
{ 
  struct passwd *pw = getpwnam("pia");

  setenv("USER", "pia", 1);
  setenv("LOGNAME", "pia", 1);		/* doesn't work with /bin/bash */
  setenv("HOME", pw->pw_dir, 1);

  /* Could do this with setregid, setreuid */
  setgid(pw->pw_gid);			/* Have to do this first. */
  if (setuid(pw->pw_uid)) {
    printf("sorry\n");
    exit(-1);
  }
  argv[0] = "-";
  /* pw->pw_shell doesn't work if it's set to "false" to prevent logins */
  execve ("/bin/bash", argv, env);	
}
