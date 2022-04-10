import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONTokener;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.actions.AnnotatingActionCompiler;
import org.testar.monkey.alayer.actions.CompoundAction;
import org.testar.monkey.alayer.actions.KeyDown;
import org.testar.monkey.alayer.actions.StdActionCompiler;
import org.testar.monkey.alayer.actions.Type;
import org.testar.monkey.alayer.devices.KBKeys;
import org.testar.monkey.alayer.exceptions.SystemStartException;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.ConfigTags;
import org.testar.monkey.Settings;
import org.testar.protocols.CodeAnalysisWebdriverProtocol;

/** Protocol for code analysis with CKAN SUT */

public class Protocol_webdriver_ckan1 extends CodeAnalysisWebdriverProtocol {

    protected String applicationUsername, applicationPassword;
    protected boolean loggedIn=false;

    	/**
	 * Called once during the life time of TESTAR
	 * This method can be used to perform initial setup work
	 *
	 * @param settings the current TESTAR settings as specified by the user.
	 */
	@Override
	protected void initialize(Settings settings) {
        System.out.println("CKAN protocol calls super.init");
 		super.initialize(settings);

        this.applicationUsername = settings.get(ConfigTags.ApplicationUsername);
		this.applicationPassword = settings.get(ConfigTags.ApplicationPassword);

	}

    @Override
	protected SUT startSystem() throws SystemStartException {
        this.loggedIn=false;
        return super.startSystem();
    }

    @Override
	protected void beginSequence(SUT system, State state) {
        super.beginSequence(system,state);
        if ( ! this.loggedIn ) {
            loginSUT(system,state);
            this.loggedIn=true;
        }
    }

    protected void loginSUT(SUT system, State state) {
		waitLeftClickAndTypeIntoWidgetWithMatchingTag("name","login", this.applicationUsername, state, system, 1, 0.5);
        Map<String, String> passwordFieldMap  = new HashMap<String, String>() {{
            put("WebId", "field-password");
            put("WebType", "password");
        }};
        waitLeftClickAndTypeIntoWidgetWithMatchingTags(passwordFieldMap, this.applicationPassword, state, system, 1, 0.5);
        Map<String, String> loginButtonMap  = new HashMap<String, String>() {{
            put("WebTagName", "button");
            put("WebName","Login");
        }};
        waitAndLeftClickWidgetWithMatchingTags(loginButtonMap, state, system, 1, 0.5);

    }


    protected void processSUTDataAfterAction(JSONTokener tokener) {
        JSONArray root = new JSONArray(tokener);

        Vector<Map<String,String>> output = new Vector<>();

        for (int i = 0; i < root.length(); i++) {
            JSONArray inner = root.getJSONArray(i);
            String type = inner.getString(0);
            String value = inner.getString(1);
            System.out.println("Extracted string " + type + " / " + value);
            Map<String, String> innerMap = new HashMap<>();
            innerMap.put("type", type);
            innerMap.put("value", value);
            output.add(innerMap);
        }

        // TODO: store extracted string data in the state model
    }




}