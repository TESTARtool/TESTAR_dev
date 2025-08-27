package org.testar.monkey.alayer.actions;

import org.testar.monkey.alayer.*;
import org.testar.monkey.alayer.exceptions.ActionFailedException;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.monkey.alayer.webdriver.WdElement;
import org.testar.monkey.alayer.webdriver.WdWidget;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WdFillFormAction extends TaggableBase implements Action {
    private static final long serialVersionUID = 1722785941555641691L;

    //TODO if this works well, put it into config file:
    private boolean useOnlyWebNameForXmlFileName = true;
    private Action formFillingAction;
    private boolean hidden = false;
    private String formFileFolder;
    private static final Logger logger = LogManager.getLogger();

    /**
     * Function for automatically generating template XML file for the user to specify input values for a form
     * If the XML file for the form exists, it reads the input values and creates clickAndType actions for them
     *
     * Issues/improvements:
     * - naming of the XML file - if(useOnlyWebNameForXmlFileName) and web name is available, the URL is ignored
     *      - otherwise URL is used in filename
     *      - if web name and web id is missing, then we use widget path in filename
     * - only handles text fields of the form, not drop-down menus or other type of widgets
     * - uses WebName of the form widget for telling the user which field the input is used for,
     * could be better to use for example <name=address> or <id=54>
     * - not tested what happens if the form does not fit to the screen and scrolling is needed to press submit
     * - generated templates are created into the folder of the protocol in use
     * - if the web page has 2 forms, should handle both of them
     * - pretty print XML seems to break the XML - we should generate the XML in pretty format to read with notepad
     *
     * @param ac
     * @param widget
     */
    public WdFillFormAction(StdActionCompiler ac, Widget widget, String formFileFolder) {
        logger.debug("Form filling action created");
        this.set(Tags.Role, WdActionRoles.FormFillingAction);
        this.formFileFolder = formFileFolder;
        // The desc of the form filling action will be updated with filename later:
        this.set(Tags.Desc, "Fill a form based on XML file.");
        formFillingAction = fillForm(ac, widget);
        this.mapOriginWidget(widget);
    }

    public boolean isHiddenForm() {
        return hidden;
    }

    @Override
    public void run(SUT system, State state, double duration) throws ActionFailedException {
        formFillingAction.run(system, state,duration);
    }

    @Override
    public String toShortString() {
        return this.get(Tags.Desc, "Fill a form based on XML file.");
    }

    @Override
    public String toParametersString() {
        return toShortString();
    }

    @Override
    public String toString(Role... discardParameters) {
        return toShortString();
    }

    /**
     *
     *
     * @param ac
     * @param widget
     */
    private Action fillForm(StdActionCompiler ac, Widget widget) {
        String uriPath = "";
        try {
            uriPath = WdDriver.getCurrentUrl();
            uriPath = uriPath.substring(uriPath.indexOf("//")+2);
        } catch (Exception e) {
            System.out.println("ERROR: Exception obtaining URI, using empty path");
        }
        logger.debug("uriPath="+uriPath);
        if(uriPath.contains(";")){
            logger.debug("Removing everything after ;");
            uriPath = uriPath.substring(0, uriPath.indexOf(";"));
            logger.debug("uriPath="+uriPath);
        }
        if(uriPath.contains("?")){
            logger.debug("Removing everything after ?");
            uriPath = uriPath.substring(0, uriPath.indexOf("?"));
            logger.debug("uriPath="+uriPath);
        }

        // WebName is sometimes empty, then the generated XML file has URL and web id or widget path as its name
        String formName = widget.get(WdTags.WebName, "");
        String path = uriPath;
        if(formName.length()>0){
            if(useOnlyWebNameForXmlFileName){
                path = formName;
                logger.debug("Only form name used for path="+path);
            }else{
                path = uriPath + "_" + formName;
                logger.debug("URL and form name used for path="+path);
            }
        }else if(widget.get(WdTags.WebId, "").length()>0){
            path = uriPath + "_" + widget.get(WdTags.WebId, "");
            logger.debug("Form name empty, using URL and web id used for path="+path);
            // System.out.println("DEBUG: Derive FillForm Action : look for file " + path);
        }else{
            // Form name and ID are empty, using TESTAR widget path for the filename
            // How to find an element that does not have name or ID? We want xPath from Selenium
            // TODO xPath for form element without attributes, instead of TESTAR widget path
            // WdDriver.getRemoteWebDriver().findElement()
            // 2 forms without name or id would be using the same XML filename even if one of the them has more fields
            // Therefore, we add TESTAR widget path into the filename:
            path = uriPath + "_" + widget.get(Tags.Path, "");
            logger.debug("Form name and ID are empty, using URL and TESTAR widget path used for path="+path);
        }
        path = path.replaceAll("[\\/?:*\"|><]", "_") + ".xml";
        String file_path = "settings\\" + this.formFileFolder + "\\" + path;
        logger.debug("file_path="+file_path);
        //Updating action description:
        this.set(Tags.Desc, "Fill a form based on XML file: "+file_path);
        logger.debug("Form action Desc="+this.get(Tags.Desc, ""));
        File f = new File(file_path);
        Map<String, String> fields = new HashMap<>();
        Boolean storeFile = true;
        // Check whether the XML file for this form exists already
        // If it exists, we read the XML file, and do not create a new one - storeFile = false
        if (f.exists()) {
            storeFile = false;
            logger.debug("Derive FillForm Action : File exists, reading the data from the file");
            fields = readFormFile(file_path);
        }

        CompoundAction.Builder formBuilder = new CompoundAction.Builder();
        int numberOfActions = buildForm(formBuilder, (WdWidget)widget, fields, storeFile, ac);

        if (storeFile && numberOfActions > 0) {
            storeToFile(file_path, fields);
            // By default create the performSubmit option as true and derive the submit action
            if (!formName.isEmpty()) {
                // If we found a form with a name property, use this property to execute a script submit action
                formBuilder.add(new WdSubmitAction(formName), 2);
                logger.debug("Storing the file and creating WdSubmitAction with form name");
            } else {
                // If the form does not contains a name property, derive a GUI click action
                // in the first submit widget of the form
                Widget input = findSubmitButtonOfForm(widget);
                if(input!=null){
                    formBuilder.add(ac.leftClickAt(input), 2);
                    logger.debug("Storing the file and creating leftClickAt on the found submit button of the form");
                }else{
                    logger.error("Could not find submit button of the form, so the action does not click submit.");
                }
            }
        } else if (fields != null && fields.containsKey("performSubmit")) {
            String submit = fields.get("performSubmit");
            if (submit.contains("true") && !formName.isEmpty()) {
                // If we found a form with a name property, use this property to execute a script submit action
                formBuilder.add(new WdSubmitAction(formName), 2);
                logger.debug("File existed already, creating WdSubmitAction with form name");
            } else if(submit.contains("true")) {
                // If the form does not contains a name property, derive a GUI click action
                // in the first submit widget of the form
                Widget input = findSubmitButtonOfForm(widget);
                if(input!=null){
                    formBuilder.add(ac.leftClickAt(input), 2);
                    logger.debug("File existed already, creating leftClickAt on the found submit button of the form");
                }else{
                    logger.error("Could not find submit button of the form, so the action does not click submit.");
                }
            }
        } // else is handled with NOP action

        // Creating a NOP action as a default and overwriting it with formFillingAction
        // If the form is empty or we cannot create actions on it, we return NOP action instead.
        // The NOP action should be filtered when calling this function
        Action formAction = new NOP();
        if (numberOfActions > 0) {
            logger.debug("Actions found, creating a form action");
            formAction = formBuilder.build();
        } else {
            logger.debug("Form is hidden, returning NOP action");
            // If the form does not contain fields, we mark as a hidden form
            hidden = true;
        }
        logger.debug("Setting OriginWidget for the form action");
        formAction.mapOriginWidget(widget);
        return formAction;
    }

    private static Boolean isSubmitButton(Widget submit_widget){
        Role[] roles = new Role[]{WdRoles.WdINPUT, WdRoles.WdBUTTON};
        return Role.isOneOf(submit_widget.get(Tags.Role, Roles.Widget), roles) && submit_widget.get(WdTags.WebType,"").equalsIgnoreCase("submit");
    }

    private Widget findSubmitButtonOfForm(Widget form) {
        Widget child = null;
        for(int i = 0; i < form.childCount(); i++) {
            if(isSubmitButton(form.child(i))) {
                return form.child(i);
            } else {
                child = findSubmitButtonOfForm(form.child(i));
                if(child != null) {
                    return child;
                }
            }
        }
        return child;
    }

    //TODO This is duplicated function from WebdriverProtocol.java - should be combined

    /**
     * This is duplicated function from WebdriverProtocol.java - should be combined
     *
     * @param widget
     * @return
     */
    private boolean isTypeable(Widget widget) {
        Role role = widget.get(Tags.Role, Roles.Widget);
        if (Role.isOneOf(role, WdRoles.nativeTypeableRoles())) {
            // Input type are special...
            if (role.equals(WdRoles.WdINPUT)) {
                String type = ((WdWidget) widget).element.type;
                return WdRoles.typeableInputTypes().contains(type.toLowerCase());
            }
            return true;
        }

        return false;
    }

    private int buildForm(CompoundAction.Builder formBuilder, WdWidget widget, Map<String, String> fields, boolean storeFile, StdActionCompiler ac) {
        int numberOfActions = 0;
        // First handling the root element:
        WdElement element = widget.element;
        String defaultValue = "write-random-genenerated-value";
        if (isTypeable(widget)) {
            if (storeFile) {
                if(element.name.length()>0){
                    fields.put(element.name, defaultValue);
                }else if(element.id.length()>0){
                    fields.put(element.id, defaultValue);
                    element.name = element.id;
                }else{
                    // System.out.println("DEBUG: name and id are empty!");
                }
            }
            if (fields.containsKey(element.name) && fields.get(element.name) != null) {
                formBuilder.add(ac.clickTypeInto(widget, fields.get(element.name), true), 2);
                numberOfActions += 2;
            }
        }
        //Then iterating the child elements:
        for (int i = 0; i < widget.childCount(); i++) {
            WdWidget w = widget.child(i);
            element = w.element;

            if (isTypeable(w)) {
                if (storeFile) {
                    if(element.name.length()>0){
                        fields.put(element.name, defaultValue);
                    }else if(element.id.length()>0){
                        fields.put(element.id, defaultValue);
                        element.name = element.id;
                    }else{
                        // System.out.println("DEBUG: name and id are empty!");
                    }
                }
                if (fields.containsKey(element.name) && fields.get(element.name) != null) {
                    formBuilder.add(ac.clickTypeInto(widget, fields.get(element.name), true), 2);
                    numberOfActions += 2;
                }
            } else {
                numberOfActions += buildForm(formBuilder, widget.child(i), fields, storeFile, ac);
            }
        }
        return numberOfActions;
    }

    private Map<String, String> readFormFile(String fileName) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(fileName));
            document.getDocumentElement().normalize();
            Element root = document.getDocumentElement();
            NodeList formDataList = root.getChildNodes();
            HashMap<String, String> result = new HashMap<>();

            NodeList dataNode = selectRandomDataInput(formDataList);

            for (int i = 0; i < dataNode.getLength(); i++) {
                Node item = dataNode.item(i);
                Element node = (Element) item;
                String value = node.getTextContent();
                result.put(node.getNodeName(), value);
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private NodeList selectRandomDataInput(NodeList formDataList) {
        int total = 0;
        // Iterate through the data elements to calculate the sum of the weight values
        for (int top = 0; top < formDataList.getLength(); top++) {
            Node dataNode = formDataList.item(top);
            NodeList dataNodeList = dataNode.getChildNodes();
            for (int content = 0; content < dataNodeList.getLength(); content++) {
                Node item = dataNodeList.item(content);
                Element node = (Element) item;
                if(node.getNodeName().equals("weight")) {
                    total += Integer.valueOf(node.getTextContent());
                }
            }
        }

        // Randomly select a number between 0 and the total sum of weight values
        // To randomly select a data node with values
        int random = new Random().nextInt((total)+1);

        int sum = 0;
        // Then iterate through the data elements to find the randomly selected data node
        for (int top = 0; top < formDataList.getLength(); top++) {
            Node dataNode = formDataList.item(top);
            NodeList dataNodeList = dataNode.getChildNodes();
            for (int content = 0; content < dataNodeList.getLength(); content++) {
                Node item = dataNodeList.item(content);
                Element node = (Element) item;
                if(node.getNodeName().equals("weight")) {
                    sum += Integer.valueOf(node.getTextContent());
                }
                if(sum >= random) {
                    // Returning the node values of the data node
                    return node.getParentNode().getChildNodes();
                }
            }
        }

        // If something wrong, return the values of the first data node
        return formDataList.item(0).getChildNodes();
    }

    private void storeToFile(String fileName, Map<String, String> fields) {
        String result = "<form>";
        // Call two times because we hardcode two data input
        result = writeFormData(result, fields);
        result = writeFormData(result, fields);
        result += "</form>";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(result);
            logger.debug("Form file created, file name: "+fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // System.out.println("Derive FillForm Action : storeToFile: " + result);
    }

    private String writeFormData(String result, Map<String, String> fields) {
        result += "<data>";
        result += "<weight>50</weight><performSubmit>true</performSubmit>";

        for (Map.Entry<String, String> entry : fields.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            result += "<" + key + ">" + value + "</" + key + ">";
        }

        result += "</data>";
        return result;
    }
}
