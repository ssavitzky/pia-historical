<if><test negative><difference>&FORM.cost; 1000</difference></test><then>
    <set entity name=routeobject><dl><dt>&FORM.name;<dd>&manager;<dt>&manager;<dd>Purchasing<dt>Purchasing<dd>NULL</dl></set>
<else>
    <if><test negative><difference>&FORM.cost; 5000</difference></test><then>
        <set entity name=routeobject> <dl><dt>&FORM.name;<dd>&manager;<dt>&manager;<dd>Purchasing<dt>Purchasing<dd>kevin<dt>kevin<dd>NULL</dl></set>
    <else>
        <if><test negative><difference>&FORM.cost; 50000</difference></test><then>
        <set entity name=routeobject> <dl><dt>&FORM.name;<dd>&manager;<dt>&manager;<dd>Purchasing<dt>Purchasing<dd>kevin<dt>kevin<dd>hart<dt>hart<dd>NULL</dl></set>
        <else>
            <set entity name=routeobject> <dl><dt>&FORM.name;<dd>NULL</dl></set>
            <blink>Because this document requests more than $50,000 a paper version must be created and faxed to Japan.</blink><p>
        </else>
        </if>
    </else>
    </if>
</else>
</if>
