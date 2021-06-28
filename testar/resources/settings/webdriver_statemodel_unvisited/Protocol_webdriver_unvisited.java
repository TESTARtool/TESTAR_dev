
/**
 * 
 * Copyright (c) 2018, 2019 Open Universiteit - www.ou.nl
 * Copyright (c) 2019 Universitat Politecnica de Valencia - www.upv.es
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

import es.upv.staq.testar.NativeLinker;
import es.upv.staq.testar.protocols.ClickFilterLayerProtocol;

import org.fruit.Pair;
import org.fruit.alayer.*;
import org.fruit.alayer.actions.*;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.alayer.webdriver.*;
import org.fruit.alayer.webdriver.enums.WdRoles;
import org.fruit.alayer.webdriver.enums.WdTags;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import org.testar.protocols.DesktopProtocol;
import org.testar.protocols.WebdriverProtocol;
import org.fruit.alayer.webdriver.WdProtocolUtil;
import nl.ou.testar.StateModel.Persistence.*;
import nl.ou.testar.StateModel.*;
import nl.ou.testar.StateModel.Persistence.OrientDB.*;
import java.util.*;
import java.lang.Thread;
import java.net.*;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;

import static org.fruit.alayer.Tags.Blocked;
import static org.fruit.alayer.Tags.Enabled;
import static org.fruit.alayer.webdriver.Constants.scrollArrowSize;
import static org.fruit.alayer.webdriver.Constants.scrollThick;
import nl.ou.testar.StateModel.Persistence.*;
import org.fruit.alayer.actions.CompoundAction.*;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.*;
import com.orientechnologies.orient.core.sql.executor.*;
import com.orientechnologies.orient.core.record.*;

public class Protocol_webdriver_unvisited extends WebdriverProtocol {
	// Classes that are deemed clickable by the web framework

	ArendManager odb;
	ModelManager m;
	String nodeName = "";
	OrientDBManager odb1;
	String selectedAction = null;

	public Protocol_webdriver_unvisited() {
		System.out.println("Constructor van statemodel");

	}

	private static List<String> clickableClasses = Arrays.asList("v-menubar-menuitem", "v-menubar-menuitem-caption");

	// Disallow links and pages with these extensions
	// Set to null to ignore this feature
	private static List<String> deniedExtensions = Arrays.asList("pdf", "jpg", "png","wsdl","pfx");

	// Define a whitelist of allowed domains for links and pages
	// An empty list will be filled with the domain from the sut connector
	// Set to null to ignore this feature
	private static List<String> domainsAllowed = Arrays.asList("para.testar.org");

	// If true, follow links opened in new tabs
	// If false, stay with the original (ignore links opened in new tabs)
	private static boolean followLinks = true;

	// URL + form name, username input id + value, password input id + value
	// Set login to null to disable this feature
	private static Pair<String, String> login = null;
	private static Pair<String, String> username = Pair.from("username", "");
	private static Pair<String, String> password = Pair.from("password", "");

	// List of atributes to identify and close policy popups
	// Set to null to disable this feature
	private static Map<String, String> policyAttributes = new HashMap<String, String>() {
		{
			put("id", "sncmp-banner-btn-agree");
		}
	};


	/*
	 * private void getLogin() { Form form = new Form(); form.add("x", "foo");
	 * form.add("y", "bar");
	 * 
	 * ClientResource resource = new
	 * ClientResource("http://localhost:8080/someresource");
	 * 
	 * Response response = resource.post(form.getWebRepresentation());
	 * 
	 * if (response.getStatus().isSuccess()) { System.out.println("Success! " +
	 * response.getStatus()); System.out.println(response.getEntity().getText()); }
	 * else { System.out.println("ERROR! " + response.getStatus());
	 * System.out.println(response.getEntity().getText()); } }
	 */

	/**
	 * Called once during the life time of TESTAR This method can be used to perform
	 * initial setup work
	 *
	 * @param settings the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings) {
		NativeLinker.addWdDriverOS();
		super.initialize(settings);
		ensureDomainsAllowed();

		// Propagate followLinks setting
		WdDriver.followLinks = followLinks;

		// Override ProtocolUtil to allow WebDriver screenshots
		// protocolUtil = new WdProtocolUtil();
		m = (ModelManager) stateModelManager;
		if (m.persistenceManager != null) {
			System.out.println("PersistenceManager != null type = " + m.persistenceManager.getClass());
			odb = (OrientDBManager) m.persistenceManager;
			odb1 = (OrientDBManager) m.persistenceManager;
			Random r = new Random();
			nodeName = System.getenv("HOSTNAME") + "_" + r.nextInt(10000);
			System.out.println("nodeName = " + nodeName);
			odb.ExecuteCommand("create vertex BeingExecuted set node = '" + nodeName + "'");

		}

	}

	/**
	 * This method is called when TESTAR starts the System Under Test (SUT). The
	 * method should take care of 1) starting the SUT (you can use TESTAR's settings
	 * obtainable from <code>settings()</code> to find out what executable to run)
	 * 2) bringing the system into a specific start state which is identical on each
	 * start (e.g. one has to delete or restore the SUT's configuratio files etc.)
	 * 3) waiting until the system is fully loaded and ready to be tested (with
	 * large systems, you might have to wait several seconds until they have
	 * finished loading)
	 *
	 * @return a started SUT, ready to be tested.
	 */
	@Override
	protected SUT startSystem() throws SystemStartException {
		return super.startSystem();
	}

	/**
	 * This method is used by TESTAR to determine the set of currently available
	 * actions. You can use the SUT's current state, analyze the widgets and their
	 * properties to create a set of sensible actions, such as: "Click every Button
	 * which is enabled" etc. The return value is supposed to be non-null. If the
	 * returned set is empty, TESTAR will stop generation of the current action and
	 * continue with the next one.
	 *
	 * @param system the SUT
	 * @param state  the SUT's current state
	 * @return a set of actions
	 */

	/*
	 * public CompoundAction fillForm(HashMap<org.fruit.alayer.Widget, String>
	 * values) { StdActionCompiler ac = new AnnotatingActionCompiler();
	 * 
	 * CompoundAction a = CompoundAction.Builder(); values.forEach((k,v)->
	 * a.add(ac.clickTypeInto(k, v, true)));
	 * 
	 * return a; }
	 */
	@Override
	protected Set<Action> deriveActions(SUT system, State state) throws ActionBuildException {
		// Kill unwanted processes, force SUT to foreground
        System.out.println("Protocol file: deriveActions: Arend acties");
		Set<Action> actions = super.deriveActions(system, state);

		// create an action compiler, which helps us create actions
		// such as clicks, drag&drop, typing ...
		StdActionCompiler ac = new AnnotatingActionCompiler();

		// Check if forced actions are needed to stay within allowed domains
        if (WdDriver.getCurrentUrl().contains("wsdl") || WdDriver.getCurrentUrl().contains("wadl"))
        {
            return new HashSet<>(Collections.singletonList(new WdHistoryBackAction()));      
            
        }
		Set<Action> forcedActions = detectForcedActions(state, ac);
		if (forcedActions != null && forcedActions.size() > 0) {
            System.out.println("Executing forced action");
			return forcedActions;
		}
		
		if (m.currentAbstractState != null) {
			AbstractState abstr = m.currentAbstractState;
			System.out.println("Protocol file(184):" + abstr.getClass() + "  state id = " + abstr.getStateId());
		}
		// iterate through all widgets
		for (org.fruit.alayer.Widget widget : state) {
			// only consider enabled and non-tabu widgets

            //System.out.println("DeriveAction widget = "+widget+" class = "+widget.getClass());
            WdWidget wd = (WdWidget)widget;
            WdElement element = wd.element;
             //System.out.println("DeriveAction widget = "+widget+" class = "+widget.getClass()+" wd = "+wd+" elem= "+element.tagName);
            if (isForm(widget))
            {
                System.out.println("Form gevonden");
                fillForm(actions, ac, state, wd,null);
                
            }
			if (!widget.get(Enabled, true) || blackListed(widget)) {
				continue;
			}

			// slides can happen, even though the widget might be blocked
			addSlidingActions(actions, ac, scrollArrowSize, scrollThick, widget, state);

			// If the element is blocked, Testar can't click on or type in the widget
			if (widget.get(Blocked, false)) {
				continue;
			}

			// type into text boxes
			if (isAtBrowserCanvas(widget) && isTypeable(widget) && (whiteListed(widget) || isUnfiltered(widget))) {
				actions.add(ac.clickTypeInto(widget, this.getRandomText(widget), true));
			}

			// left clicks, but ignore links outside domain
			if (isAtBrowserCanvas(widget) && isClickable(widget) && (whiteListed(widget) || isUnfiltered(widget))) {
				if (!isLinkDenied(widget)) {
					actions.add(ac.leftClickAt(widget));
				}
			}
		}
        System.out.println("Done with DeriveActions");

		return actions;
	}


   public HashMap<String,String> readFormFile(String fileName)
   {
       try {
       DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File( fileName ));
        document.getDocumentElement().normalize();
        Element root = document.getDocumentElement();
        System.out.println("readFormFile");
        NodeList items =  root.getChildNodes(); 
        HashMap<String, String> result = new HashMap<String,String>();
        for (int i=0; i<items.getLength(); i++)
        {
            
            Node item = items.item(i);
            Element node = (Element) item;
            String value = node.getTextContent();            
           // System.out.println(node.getNodeName() +"=" +value);
            result.put(node.getNodeName(), value);
        }
        System.out.println("Einde formfile");
        return result;
       }
       catch (Exception e)
       {}
       return null;
       
   }
   
   public void storeToFile(String fileName, HashMap<String, String> data)
   {
       String result = "<form><performSubmit>true</performSubmit>";
       
       for (Map.Entry<String, String> entry : data.entrySet())
       {
           String key = entry.getKey();
           String value = entry.getValue();
           
           result += "<"+key+">"+value+"</"+key+">";
       }
       result += "</form>";
       try {
       BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(result);
    
       writer.close();
       }
       catch (Exception e)
       {}
      // System.out.println(result);
       
   }
   
   boolean inForm = false;
   @Override
  protected boolean blackListed(org.fruit.alayer.Widget w){
       if (inForm) return false;
       
    	return super.blackListed(w);
    }
   

    public int buildForm(CompoundAction.Builder caB, WdWidget widget, HashMap<String, String> fields, boolean storeFile)
	{
       int sum = 0;
       WdElement element = widget.element;
	   String defaultValue = "write-random-genenerated-value";
       if (isTypeable(widget))
                {
					
                    
         //           System.out.println("veld "+element.name+"  gevonden");
                    if (storeFile)
                    {
                        
                        fields.put(element.name,defaultValue);
                    }
                    if (fields.containsKey(element.name) && fields.get(element.name) != null) {
                        caB.add(new WdAttributeAction(element.name,"value",fields.get(element.name)),2);
                        sum += 2;
                    }
                    
                }
        String baseElem = element.tagName;
       // System.out.println("check children: huidig element "+element.tagName+" aantal childs: "+widget.childCount());
		for (int i = 0; i< widget.childCount(); i++) {
			WdWidget w = widget.child(i);
            //System.out.println("child "+i+"  van element "+baseElem);
            //WdElement 
            element = w.element;			
            
           //     System.out.println("buildForm :"+ element.tagName +" "+element.name);
                if (isTypeable(w))
                {
             //       System.out.println("veld "+element.name+"  gevonden");
                    if (storeFile)
                    {
                        
                        fields.put(element.name,defaultValue);
                    }
                    if (fields.containsKey(element.name) && fields.get(element.name) != null) {
                        
                        caB.add(new WdAttributeAction(element.name,"value",fields.get(element.name)),2);
                        sum += 2;
                       // System.out.println("element.name in de fields; voeg toe aan caB som+2 som = "+sum);
                    }
                    
                } else {
               //     System.out.println("Element "+element.tagName+"  is niet typeable");
                    
					 sum += buildForm(caB, widget.child(i), fields, storeFile);
				}
           
		}
System.out.println("klaar met baseElem "+baseElem+"  sum = "+sum);
       return sum;

	}
	public void fillForm(Set<Action> actions, StdActionCompiler ac, State state, WdWidget widget, HashMap<String, String> fields) {
    //System.out.println("Url = "+WdDriver.getCurrentUrl());
    inForm=true;
    if (fields == null)
    {
        fields = new HashMap<String, String>();
    }
    URI uri = null;
    try {
     uri = new URI(WdDriver.getCurrentUrl());
    }
    catch (Exception e)
    {}
	String formId = widget.getAttribute("name");
    if (formId == null)
    {
        formId = "";
    }
    String path = (uri.getPath()+"/"+formId).replace("/","_")+".xml";
    System.out.println("Look for file "+path);
    File f = new File(path);
    Boolean storeFile = true;
    if (f.exists())
    {
        storeFile = false;
        fields = readFormFile(path);
        System.out.println("Bestand bestaat, lees de data uit bestand");
    }
		CompoundAction.Builder caB = new CompoundAction.Builder();
		int sum = buildForm(caB, widget, fields, storeFile);
		
		if (fields.containsKey("performSubmit"))
		{
			boolean submit = Boolean.getBoolean(fields.get("performSubmit"));
			if (submit && formId != "") {
              
				caB.add(new WdSubmitAction(formId),2);
			}
		}
if (storeFile)
{
    storeToFile(path, fields);
}
      if (sum > 0){
		CompoundAction ca = caB.build();

        actions.add(ca);
      }
      inForm=false;
      System.out.println("fillForm klaar");
		//return ca;
	}


    boolean _moreActions = true;
	 @Override
	 protected boolean moreActions(State state) {
		return _moreActions;
	 }

	 @Override
	 protected boolean moreSequences()
	 {
		 return _moreActions;
	 }

	  

	@Override
	protected boolean isClickable(org.fruit.alayer.Widget widget) {
		Role role = widget.get(Tags.Role, Roles.Widget);
		if (Role.isOneOf(role, NativeLinker.getNativeClickableRoles())) {
			// Input type are special...
			if (role.equals(WdRoles.WdINPUT)) {
				String type = ((WdWidget) widget).element.type;
				return WdRoles.clickableInputTypes().contains(type);
			}
			return true;
		}

		WdElement element = ((WdWidget) widget).element;
		if (element.isClickable) {
			return true;
		}

		Set<String> clickSet = new HashSet<>(clickableClasses);
		clickSet.retainAll(element.cssClasses);
		return clickSet.size() > 0;
	}

	boolean isForm(org.fruit.alayer.Widget widget)
	{
		Role r = widget.get(Tags.Role, Roles.Widget);
		if (Role.isOneOf(r, new Role[] { WdRoles.WdFORM}))
		{
			return r.equals(WdRoles.WdFORM);
		}
		return false;
	}

	@Override
	protected boolean isTypeable(org.fruit.alayer.Widget widget) {
		Role role = widget.get(Tags.Role, Roles.Widget);
		if (Role.isOneOf(role, NativeLinker.getNativeTypeableRoles())) {
			// Input type are special...
			if (role.equals(WdRoles.WdINPUT)) {
				
				String type = ((WdWidget) widget).element.type.toLowerCase();
				//System.out.println("isTypeable:" + widget+"  type = "+type+"  result = "+WdRoles.typeableInputTypes().contains(type));
				return WdRoles.typeableInputTypes().contains(type);
			}
			//System.out.println("true");
			return true;
		}
		//System.out.println("false");

		return false;
	}
	/**
	 * Select one of the available actions using an action selection algorithm (for
	 * example random action selection)
	 *
	 * @param state   the SUT's current state
	 * @param actions the set of derived actions
	 * @return the selected action (non-null!)
	 */

	private AbstractState currentState;

	// private Action selectedAction; // destination action to perform
	private Boolean finishedAction = false;

	public ArrayList<String> GetUnvisitedActionsFromDatabase(String currentAbstractState) {
		System.out.println("GetUnvisitedActionsFromDatabase");
		ArrayList<String> result = new ArrayList<String>();
		String sql = "SELECT expand(path) FROM (  SELECT shortestPath($from, $to) AS path   LET     $from = (SELECT FROM abstractstate WHERE stateId='"
				+ currentAbstractState + "'),     $to = (SELECT FROM BlackHole)   UNWIND path)";
		OResultSet rs = odb.ExecuteQuery(sql);

		while (rs.hasNext()) {
			OResult item = rs.next();
			if (item.isVertex()) {
				System.out.println("Item is a vertex");
				Optional<OVertex> optionalVertex = item.getVertex();

				OVertex nodeVertex = optionalVertex.get();
				for (OEdge edge : nodeVertex.getEdges(ODirection.OUT, "UnvisitedAbstractAction")) {
					result.add(edge.getProperty("actionId"));
					System.out.println("Edge " + edge + " gevonden ID = " + edge.getProperty("actionId"));
				}

			}

			System.out.println("friend: " + item);
		}
		rs.close();
		System.out.println("Klaar met ophalen acties");
		return result;
	}

	public void UpdateAbstractActionInProgress(String actionId) {
		System.out.println("actie  AbstractID = " + actionId);

		String sql = "update edge UnvisitedAbstractAction set in = (SELECT FROM BeingExecuted WHERE node='" + nodeName
				+ "') where actionId='" + actionId + "'";
		System.out.println("Execute" + sql);

		odb.ExecuteCommand(sql);

	}

	public HashMap<String, Action> ConvertActionSetToDictionary(Set<Action> actions) {
		System.out.println("Convert Set<Action> to HashMap containing actionIds as keys");
		HashMap<String, Action> actionMap = new HashMap<String, Action>();
		ArrayList<Action> actionList = new ArrayList<Action>(actions);
		for (Action a : actionList) {
			System.out.println("Add action " + a.get(Tags.AbstractIDCustom) + "  to actionMap");
			actionMap.put(a.get(Tags.AbstractIDCustom), a);
		}
		System.out.println("ActionMap initialized");
		return actionMap;
	}

	public String selectRandomAction(ArrayList<String> actions) {

		long graphTime = System.currentTimeMillis();
		Random rnd = new Random(graphTime);

		String ac = actions.get(rnd.nextInt(actions.size()));
		UpdateAbstractActionInProgress(ac);
		System.out.println("Update action " + ac + " from BlackHole to BeingExecuted");
		return ac;
	}

    int cyclesWaitBeforeNewAction = 0;

	String lastState = "";
	int sameState = 0;

	private String getNewSelectedAction(State state, Set<Action> actions) {
		String result = null;
		Boolean ok = false;
		do {			
			try {
				ArrayList<String> availableActionsFromDb = GetUnvisitedActionsFromDatabase(
						state.get(Tags.AbstractIDCustom));
				
				System.out.println(
						"Number of shortest path actions available in database: " + availableActionsFromDb.size());
				if (availableActionsFromDb.size() == 0) {

					// Visited all actions already from this point to blackHole
					// Go up one state. Also possible there are no actions at all
					// possibly all actions are executed by other nodes.
					if (actions.size() >= 1) // derived actions contains somethi
					{
                        // A new action can be selected
                        cyclesWaitBeforeNewAction = 0;
						if (state.get(Tags.AbstractIDCustom).equals(lastState))
						{
							sameState++;

						}
						lastState = state.get(Tags.AbstractIDCustom);
						ok = true;
						// there actions but none are available for this node
						return null;
					} else {
                        // No new action can be selected and database does not contain anything
						//System.out.println("I hope this doesn't trigger");
                        cyclesWaitBeforeNewAction ++;

						WdDriver.executeScript("window.history.back();");
						Thread.sleep(1000);
						
						ok = true;
						
					}
				} else {
					System.out.println("Use availableactions from statemodel");
					for (String a : availableActionsFromDb) {
						System.out.println("Actie with id " + a);
					}
					System.out.println("Done printing available actions");

					result = selectRandomAction(availableActionsFromDb);
					ok = true;
					return result;
					

				}
			} catch (Exception e) {
				int sleepTime = new Random(System.currentTimeMillis()).nextInt(5000);
				System.out.println("Exception while getting action; Wait " + sleepTime + " ms");

				ok = false;
				try {
				Thread.sleep(sleepTime);
				}
				catch (Exception th){

				}
			}
		} while (!ok);
		return result;
	}

	public Action traversePath(State state, Set<Action> actions)
	{
    System.out.println("traversePath");
		return super.selectAction(state, actions);
	
	}

	@Override
	protected Action selectAction(State state, Set<Action> actions){

		//currentState = m.currentAbstractState; // initialize currentState
		
		//Call the preSelectAction method from the AbstractProtocol so that, if necessary,
		//unwanted processes are killed and SUT is put into foreground.
		Action retAction = preSelectAction(state, actions);
		
		if (retAction != null) {
			return retAction;
		}
		
		// First check wheter the selectedAction is still not visited or visited by this node
		
				
		if (selectedAction == null) {

			selectedAction = getNewSelectedAction(state, actions);
			// After execution of getNewSelectedAction it is still possible no action is selected (empty database for example)

			
			if (selectedAction == null)
			{
                if (cyclesWaitBeforeNewAction < 3 && sameState < 3) {
					System.out.println("selectAction: cyclesWaitBeforeNewAction = "+cyclesWaitBeforeNewAction+" sameState = "+sameState+" Wait 3000+Rnd(2000) ms then select action (hoping for newly discovered actions");
                    try { Thread.sleep(3000+new Random().nextInt(2000)); } catch (Exception e){}
                    // Wait 5 seconds before new action can be selected (even if it's a known one
				   // return super.selectAction(state, actions);
                } else {
                    _moreActions = false;
                }
			}	
		}
				
			
		
		// Prepare SQL statement to retrieve path to the action
		// Check whether we have an selectedAction we want to perform (and if it's possible to execute it now.
		
		if (selectedAction != null) {
			System.out.println("selectedAction != null");
			HashMap<String, Action> actionMap = ConvertActionSetToDictionary(actions);
			// select path towards the selected action
			  if (actionMap.containsKey(selectedAction)) {
				  System.out.println("Needed action is currently available, so select this action");
				  // Perform desired action since it's available from this point
				  Action a =  actionMap.get(selectedAction); // Get the ConcreteAction matching the selectedAction
				  selectedAction = null; // Reset selectedAction so next time a new one will be choosen.
				  return a;				  
			  } else {
				  if (sameState >3 )
				  {
					  System.out.println("Already more than three times in same state; Put back in blackhole");
					  _moreActions = false;
				  }
				  System.out.println("Needed action is unavailable, select from path to be followed; for now easy way out random select");
				  Action a = traversePath(state, actions);
				  return a;
				  // Lookup database to perform the next action in the path (possible better to be cached)
				  // Perform action to get closer to the selectedAction
			  }
		} 		
		
		if (retAction != null) return retAction;
		System.out.println("Return a fallback action");
			
		retAction = super.selectAction(state, actions);
		try {
		UpdateAbstractActionInProgress(retAction.get(Tags.AbstractIDCustom));
		}
		catch (Exception e)
		{}
		return retAction;
			
		
	}

}
