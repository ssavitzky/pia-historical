<!-- UTILITY TAGS TO HELP WITH PROCESSING URLS -->

<!-- Given a string, quote any characters special to regexp -->
<actor tag=quote-special-chars empty><set local name="string"><get element name="string"></set><foreach entity="specialchar" list="\ $ ^ . * + ? [ ]"><set local name="string"><subst match="\&specialchar;" result="\\\&specialchar;">&string;</subst></set></foreach>&string;</actor>


<!-- Extract just the filename from a URL -->
<actor tag=url-filename empty desc="extract filename from URL"><set name=myurl local><get name=url element></set><subst match=".*/" result="">&myurl;</subst></actor>

<!-- From Photo_Album/tagset.inc -->
<!-- needed a way to convert relative URL's to base URL's -->

<actor tag=full-url empty desc="fully qualifies a URL"><set name=mybase local><get name=base element></set><set name=myurl local><get name=url element></set><if><test match="^http://">&myurl;</test><then>&myurl;</then><else><if><test match ="^/">&myurl;</test><then><set local name="leftover"><subst match="^http://[^/]*" result="">&mybase;</subst></set><set local name="leftover"><quote-special-chars string="&leftover;"></set><set local name="mybase"><subst match="&leftover;" result="">&mybase;</subst></set>&mybase;&myurl;</then><else><subst match="[^/]* " result="">&mybase;</subst>&myurl;</else></if></else></if></actor>

