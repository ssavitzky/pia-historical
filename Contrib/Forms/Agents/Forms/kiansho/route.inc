<route> <routestep>
            <routeid>1</routeid>
            <routeuser>user</routeuser>
            <routewhen>always</routewhen>
            <routecond> </routecond>
            </routestep><routestep>
            <routeid>2</routeid>
            <routeuser>manager</routeuser>
            <routewhen>always</routewhen>
            <routecond><conditional>
  <condvar>estcost</condvar>
  <condtest>greaterthan</condtest>
  <condval>5000</condval>
  </conditional></routecond>
            </routestep><routestep>
            <routeid>3</routeid>
            <routeuser>Finance</routeuser>
            <routewhen>onlyif</routewhen>
            <routecond><conditional>
  <condvar>estcost</condvar>
  <condtest>greaterthan</condtest>
  <condval>1000</condval>
  </conditional></routecond>
            </routestep><routestep>
            <routeid>4</routeid>
            <routeuser>President</routeuser>
            <routewhen>onlyif</routewhen>
            <routecond><conditional>
  <condvar>estcost</condvar>
  <condtest>greaterthan</condtest>
  <condval>5000</condval>
  </conditional></routecond>
            </routestep><routestep>
            <routeid>5</routeid>
            <routeuser>Purchasing</routeuser>
            <routewhen>always</routewhen>
            <routecond> </routecond>
            </routestep></route>
	  