<route> <routestep>
            <routeid>1</routeid>
            <routeuser>user</routeuser>
            <routewhen>always</routewhen>
            <routecond> </routecond>
            </routestep><routestep>
            <routeid>2</routeid>
            <routeuser>Manager</routeuser>
            <routewhen>always</routewhen>
            <routecond> </routecond>
            </routestep><routestep>
            <routeid>3</routeid>
            <routeuser>DivHead</routeuser>
            <routewhen>onlyif</routewhen>
            <routecond><conditional>
  <condvar>amount</condvar>
  <condtest>greaterthan</condtest>
  <condval>1000</condval>
  </conditional></routecond>
            </routestep><routestep>
            <routeid>4</routeid>
            <routeuser>Accounting</routeuser>
            <routewhen>always</routewhen>
            <routecond> </routecond>
            </routestep></route>
	  