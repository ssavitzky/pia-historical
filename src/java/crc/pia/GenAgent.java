// GenAgent.java
// $Id$
// (c) COPYRIGHT Ricoh California Research Center, 1997.
package crc.pia;
import java.util.Hashtable;
import crc.pia.Agent;
import crc.pia.agent.AgentMachine;
import crc.pia.Transaction;
import crc.pia.Machine;
import crc.pia.Resolver;
import crc.pia.Content;

import crc.ds.Thing;

public class GenAgent extends Thing implements Agent {
  /**
   * Attribute index - name of this agent
   *
   */
  protected String agentname;

  /**
   * Attribute index - type of this agent
   *
   */
  protected String agenttype;

  /**
   * Attribute index - type of this agent
   *
   */
  protected String version;

  /**
   * Attribute index - directories that this agent can write to
   *
   */
  protected Hashtable dirTable;

  /**
   * Attribute index - files that this agent can write to
   *
   */
  protected Hashtable fileTable;


  /**
   * Attribute index - a list of criteria, value pairs
   *
   */
  protected Vector criteria;

  /**
   * Attribute index - virtual Machine to which explicit requests are directed.
   *
   */
  protected agent.Machine virtualMachine;

  /**
   * Initialization; sub classes may override
   */
  public void initialize(){
    String n;
    String t;
    String url;
    Transaction request;

    if( !n.equalsIgnoreCase( t ) ) type( t );

    option("name", name());
    option("type", type());

    url = "/" + n + "/" + "initialize.if";
    request = createRequest("GET", url );
    submit( request );
  }

 
  /**
   * put request on stack for resolution
   * used only in PIA::Agent::initialize and run_init_file.
   */
  public void submit( Transaction request ){

    /*
     put request on stack for resolution
     $request=PIA::Transaction->new($request,$main::this_machine)
	unless ref($request) eq 'PIA::Transaction';
    */
    pia.resolver.unshift( request );
  }

 /**
  * runInitFile
  * Submit each form and get each link in file fileName.
  * Look up fileName as an interform if find is positive.
  * Treat file as a string if find is negative.
  */
  public int runInitFile(String fileName, int find){
    /*
    my $html;
    my $count=0;
    my $request;

    if ($find < 0) {
	$html = IF::Run::parse_html_string($fn);
    } else {
	my $file = $find? $file = $self->find_interform($fn) : $fn;
	return unless -e $file;
	$html = IF::Run::parse_init_file($file);
    }
    for (@{ $html->extract_links(qw(a form)) }) {
	my ($url, $element) = @$_;
	if ($element->tag =~ /form/i) {
	    $method=$element->attr('method');
	    $request=$self->create_request($method,$url,$element);
	} else {
	    ## A name= link would probably cause confusion.
	    $request=$self->create_request('GET',$url,$element);
	}
	my $status=$self->submit($request);
	$count+=1;
    }
    ## $html->delete;
    return $count;
    */
  }
 

  /**
   * @return name of agent
   */
  public String name(){
    return agentname; 
  }

  /**
   * set name of agent
   */
  public void name(String name){
    this.agentname = name;
  }

  /**
   * @return type of agent
   */
  public String type(){
    return agenttype;
  }

  /**
   * set type of agent
   */
  public void type(String type){
    this.agenttype = type;
  }

  /**
   * @return version
   */
  public String version(){
    if(!version){
      StringBuffer v = "PIA/";
      if( !type().equalsIgnoreCase( name() ) )
	v.append( type() + "/" );
      v.append( name() );
      return new String( v );
    }
    else
      return version;
  }

  /**
   * set version
   */
  public void version(String version){
    if( version )
      this.version = version;
  }

  /**
   * set a file attribute
   *
   */
  public void fileAttribute(String key, String value){
    fileTable.put( "_" + key, value ); 
  }

  /**
   * get a file attribute
   */
  public StringBuffer fileAttribute(String key){
    StringBuffer value;

    String v = (String)fileTable.get(key);
    if( !v ){
      v = option( key );
      if ( v.startsWith("~/") ){
	  String home = System.getProperty("user.home");
	  value = new StringBuffer( v.substring(2) );
	  value.insert(0,home + "/");
	  fileTable.put( "_"+key, value );
      }
    }
    else value = new StringBuffer( v );
    
    if( !value ) value = new StringBuffer("");
    return value;
  }

  /**
   * set a directory attribute
   *
   */
  public void dirAttribute(String key, String value){
    dirTable.put( "_" + key, value ); 
  }

  /**
   * retrieve a directory attribute.
   * Performs ~ expansion on the filename
   * Makes sure that it ends in a '/' character.
   */
  public StringBuffer dirAttribute(String key){
    StringBuffer value;

    String v = (String)dirTable.get(key);
    if( !v ){
      v = option( key );
      if ( v ){
	if ( v.startsWith("~/") ){
	  String home = System.getProperty("user.home");
	  value = new StringBuffer( v.substring(2) );
	  value.insert(0,home + "/");
	}
	String tmp = new String( value );
	if ( !tmp.endsWith("/") ) { value = value.append( '/' ); }
	dirTable.put( "_"+key, value );
      }
    }
    else value = new StringBuffer( v );

    if( !value ) value = new StringBuffer("");
    return value;
  }

  /**
   *  returns a directory that we can write data into.
   *  creates one if necessary, starts with agent_directory,
   *  then if_root, USR_ROOT/$name, PIA_ROOT/$name, /tmp/$name
   */
  public StringBuffer agentDirectory(){
    StringBuffer directory = dirAttribute("agent_directory");
    if( directory )
      return directory;
    String[] possibilities = { pia.USR_DIR + "/" + name + "/",
			       pia.USR_DIR + "/" + type + "/",
			       pia.USR_DIR + "/tmp/" + name + "/" };
    String name = name();
    String type = type();
    
    int index;
    StringBuffer direct;
    for(int i = 0; i < possibilities.length(); i++){
      String dir = possibilities[i];

      if( dir.endsWith("/") ){
	  index = dir.lastIndexOf("/");
	  direct = new StringBuffer( dir );
	  direct.setCharAt(index, ' ');
      }
      
      File myFileDir = new File( direct );
      if( myFileDir.exists() || myFileDir.mkdir() ){
	if( myFileDir.isDirectory() && myFileDir.canWrite() ){
	  directory = dirAttribute( "agent_directory", directory );
	  return directory.append('/');
	}
      }
    }

    pia.errMsg( name()+ "could not find appropriate, writable directory");
    return null;
  }

  /**
   * returns a directory that we can write InterForms into
   * creates one if necessary, starts with if_root, then
   * USR_ROOT/$name, PIA_ROOT/$name, /tmp/$name
   */
  public StringBuffer agentIfRoot(){
    StringBuffer root = dirAttribute("if_root");
    String[] possibilities = new String[10];
    if( root )
      possibilities[0] = root;
    
    String name = name();
    String type = type();
    
    possibilities[1] = pia.USR_ROOT + "/" + name + "/";
    possiblilties[2] = pia.USR_ROOT + "/" + type + "/";
    possiblilties[3] = pia.PIA_ROOT + "/" + name + "/";
    possiblilties[4] = pia.PIA_ROOT + "/" + type + "/";
    possiblilties[5] = "/tmp" + name + "/";

    int index;
    StringBuffer direct;
    StringBuffer directory;

    for(int i = 0; i < possibilities.length(); i++){
      String dir = possibilities[i];

      if( dir.endsWith("/") ){
	  index = dir.lastIndexOf("/");
	  direct = new StringBuffer( dir );
	  direct.setCharAt(index, ' ');
      }
      
      File myFileDir = new File( direct );
      if( myFileDir.exists() || myFileDir.mkdir() ){
	if( myFileDir.isDirectory() && myFileDir.canWrite() ){
	  directory = dirAttribute( "agent_directory", directory );
	  return directory.append('/');
	}
      }
    }

    pia.errMsg( name()+ "could not find appropriate, writable directory");
    return null;
  }


  /**
   * returns the base url (as string) for this agent
   * optional path argument just for convenience--
   * returns full url for accessing that file
   */
  public StringBuffer agentUrl(String path){
    StringBuffer url = pia.PIA_URL + name() + "/" + path;
  }

  /**
   * Agents maintain a list of feature names and expected values;
   * the features themselves are maintained by a FEATURES object
   * attached to each transaction.
   */
  public void criteria(Object element){
    if( element ){
      if(! criteria ) criteria = new Vector();
      criteria.addElement( element );
    }
  }

  /**
   * Agents maintain a list of feature names and expected values;
   * the features themselves are maintained by a FEATURES object
   * attached to each transaction.
   */
  public Vector criteria(){
    if(! criteria ) criteria = new Vector();
    return criteria;
  }

  /**
   * Set a match criterion.
   * feature is string naming a feature
   * value is 0,1 (exact match--for don't care, omit the feature)
   * code is a functor object takes transaction as argument returns Boolean
   * @return criteria table
   */
  public Vector matchCriterion(String feature, Boolean value, Object code){
    if( code )
      pia.TFeatures.register(feature, code);
    
    if(value == null)
      value = true;
    criteria( feature );
    criteria( value );
    return criteria();
  }
  
  /**
   * agents are associated with a virtual machine which is an
   * interface for actually getting and sending transactionss.  Posts
   * explicitly to an agent get sent to the agent's machine (then to
   * the agent's interform_request method). Other requests can be
   * handled implicitly by the agent.  If one does not exist,
   * create a pia.agent.Machine
   * @return virtual machine
   */
  public Machine machine(){
    if( !virtualMachine )
      virtualMachine = new agent.Machine( this );
    return virtualMachine;
  }

  /**
   * Setting the virtual machine.  
   */
  public void machine( Machine vmachine){
    agent.Machine machine;
 
    if( !virtualMachine && !vmachine ){
      machine = new agent.Machine( this );
    }
    if( machine )
      virtualMachine = machine;
  }

  /**
   *  They can also be handled by code or InterForm hooks.  
   *
   */
  public void actOn(Transaction ts, Resovler res){
    errMsg(name() + "If you come this far, you have execute the generic agent actOn.");
  }

  /**
   * Handle a transaction matched by an act_on method. 
   * Requests directly _to_ an agent are handled by its Machine;
   * the "handle" method is used only by agents like "cache" that
   * may want to intercept a transaction meant for somewhere else.
   */
  public void handle(Transaction ts, Resovler res){
    errMsg(name() + "If you come this far, you have execute the generic agent Handle.");
  }

  /**
   * Options are strings stored in attributes.  Options may have
   * corresponding features derived from them, which we compute on demand.
   */
  public void option(String key, String value){
    /*
    if (defined $value) {
	$self->attr($key, $value);
	$self->compute($key) if $self->has($key);
    }
    return $self->attr($key);
    */
  }

  /**
   * Return an option's value 
   *
   */
  public String option(String key){
  }

  /**
   * Set options with a hash table
   *
   */
  public void parseOptions(Hashtable hash){
    Enumeration e;
    if( hash ){
      e = hash.keys();
      while( e.hasMoreElements() ){
	Object keyObj = e.nextElement();
	String key = (String)keyObj;
	String value = (String)hash.get( keyObj );
	option( key, value );
      }
    }
  }

  /**
   * Find an interform, using a simple search path and a crude kind
   * of inheritance.  Allow for the fact that the user may be trying
   * to override the interform by putting it in $USR_ROOT/$name/.
   * The search path used is:
   *     option(root)/name : option(root)
   *     USR_ROOT/name : PIA_ROOT/name : USR_ROOT : PIA_ROOT
   */
  public StringBuffer findInterform(String url, boolean noDefault){
    if( !url ) return null;

    StringBuffer form;
    String path = url;
    path.trim();
    int index = -1;

    String myname = name();
    String mytype = type();

    // Find interform name in URL.

    if ( noDefault ){
    }
    else if( (index = path.indexOf(myname+"/") ) != -1 ){ // default to index.if
      form = path.substring( index + (myname+"/").length() );
      if( form.length() == 0 )
	form = "index.if";
    }else if( path.endsWith( myname ) ){  // default to home.if
      form = "home.if";
    }else if( path == "/" || path.length() == 0 ){
      form = "ROOTindex.if";
    }

    String if_path = dirAttribute( "if_path" );
    if( if_path == "" ){
     /*
      * If the path isn't already defined, set it up now.
      *
      *  the path puts any  defined if_root first 
      *   (if_root/$myname, if_root/$mytype, if_root),
      *  then USR_ROOT (USR_ROOT/$myname, USR_ROOT/$mytype)
      *  then PIA_ROOT (PIA_ROOT/$myname, PIA_ROOT/$mytype)
      *  then USR_ROOT, then PIA_ROOT (for inheriting forms)
      */
      StringBuffer home = pia.PIA_ROOT;
      if ( !home.endsWith( "/" ) ){ home.append('/'); }

      StringBuffer root = dirAttribute( "if_root" );
      if ( root != "" ){
	// handle a user-defined root first:

	if ( !root.endsWith( "/" ) )       { root.append('/'); }
	if ( root.endsWith( "/"+myname+"/" )) { 
	  String tmp = new String( root );
	  int index  = tmp.indexOf( "/"+myname+"/" );
	  root       = new StringBuffer( tmp.substring( 0, index+1 ) );
	}
	
	Vector pathVector = new Vector();
	pathVector.addElement( root+myname+"/" );
	if( myname != mytype )
	  pathVector.addElement( root+mytype+"/" );
	pathVector.addElement( root );
	}	
	/*
        * Then see whether the user has overridden the form.
	*    It's possible that one of these will be a duplicate.
	*    That slows us down, but not much.
	*/
	
        root = pia.USR_ROOT;
        if ( !root.endsWith("/") ) { root.append( "/" ); }
        pathVector.addElement( root+myname+"/" );
	if ( myname != mytype )
	  pathVector.addElement( root+mytype+"/" );
	pathVector.addElement( home+myname+"/" );
	if( myname != mytype )
	  pathVector.addElement( home+type+"/" );
	pathVector.addElement( root );
	pathVector.addElement( home );

	dirAttribute("if_path", if_path );
	
	}
	  File f;
	  Enumeration e = pathVector.elements();
	  while( e.hasMoreElements() ){
	    String zpath = (String)e.nextElements();
	    String wholepath = zpath + form;
	    f = new File( wholepath );
	    if( f.exists() ) return wholepath;
	  }

	  return null;
}

  /**
   * Respond to a request directed at one of an agent's interforms.
   * The InterForm's url may be passed separately, since the agent may
   * need to modify the URL in the request.  It can pass either a full
   * URL or a path.
   */
    public String respondToInterform(){
    }

  /**
   * Respond to a request directed at one of an agent's interforms.
   * The InterForm's url may be passed separately, since the agent may
   * need to modify the URL in the request.  It can pass either a full
   * URL or a path.
   */
    public String respondToInterformPut(){
    }


  public GenAgent(String name, String type){
    //$self->{_list} = ['name', 'type', ];

    dirTable = new HashTable();

    if( name ) this.name( name );
    if( type )
      this.type( type );
    else
      this.type( name );
    initialize();
  }
}







