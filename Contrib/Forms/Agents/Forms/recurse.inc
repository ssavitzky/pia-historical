<set name=temp><get index="routeobject.&temp;"></set>
<if><get index="routeobject.&temp;"><then>
<if><test match="NULL"><get index="routeobject.&temp;"></test><then></then>
<else><option><get index="routeobject.&temp;">
      <read interform file="recurse.inc" process>
</else>
</if>
</then>
</if>
