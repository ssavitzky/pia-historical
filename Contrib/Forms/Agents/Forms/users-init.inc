<!-- reading the users.dat file into agent.users entity 
 key is logon name, value is user record -->
 <set name=stuff><read file=users.dat process></set>
 <set name=users><get name=stuff  findall=user></set>
 <set agent name=users><dl> </dl></set>
 <repeat list=&users; entity=user>
 <set name=uid><get index=user.userid.Text></set>
 <set agent name=users key=&uid;><get name=user></set>
 </repeat>

