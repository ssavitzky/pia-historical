<set name=temp><get name="routeobject.&temp;"></set>
<if><test match="NULL"><get name="routeobject.&temp;"></test><then></then>
<else><option><get name="routeobject.&temp;">
      <read interform file="recurse.inc" process>
</else>
</if>
