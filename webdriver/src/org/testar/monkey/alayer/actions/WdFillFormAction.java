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
    private StdActionCompiler ac;
    private Widget widget;
    private Action formFillingAction;
    private boolean hidden = false;
    private String formFileFolder;
    private static final Logger logger = LogManager.getLogger();

    /**
     * Function for automatically generating template XML file for the user to specify input values for a form
     * If the XML file for the form exists, it reads the input values and creates clickAndType actions for them
     *
     * Issues/improvements:
     * - naming of the XML file - now using full URL with domain also, sometimes the form properties are empty
     * - only handles text fields of the form, not drop-down menus or other type of widgets
     * - uses WebName of the form widget for telling the user which field the input is used for,
     * could be better to use for example <name=address> or <id=54>
     * - not tested what happens if the form does not fit to the screen and scrolling is needed to press submit
     * - change the path of the generated templates to the folder of the protocol in use
     * - if the web page has 2 forms, this only handles the first one
     * - deal with the pretty print XML or generate the XML in pretty format
     *
     * @param ac
     * @param widget
     */
    public WdFillFormAction(StdActionCompiler ac, Widget widget, String formFileFolder) {
        logger.debug("debug Form filling action created");
        logger.error("error Form filling action created");
        logger.info("info Form filling action created");
        logger.trace("trace Form filling action created");
        logger.fatal("fatal Form filling action created");
        logger.warn("warn Form filling action created");

        this.ac=ac;
        this.widget=widget;
        this.set(Tags.Role, WdActionRoles.FormFillingAction);
        this.formFileFolder = formFileFolder;
        //TODO unique name for form filling action
        this.set(Tags.Desc, "Fill a form based on XML file.");
        formFillingAction = fillForm(ac, widget);
        this.set(Tags.OriginWidget, widget);
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
        return "Fill a form based on XML file.";
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

        // WebName is sometimes empty, then the generated XML file has only the URL as its name
        String formName = widget.get(WdTags.WebName, "");
        String path = uriPath;
        if(formName.length()>0){
            path = uriPath + "_" + formName;
            // System.out.println("DEBUG: Derive FillForm Action : look for file " + path);
        }else if(widget.get(WdTags.WebId, "").length()>0){
            path = uriPath + "_" + widget.get(WdTags.WebId, "");
            // System.out.println("DEBUG: Derive FillForm Action : look for file " + path);
        }else{
            // System.out.println("DEBUG: Form name and ID are empty, using TESTAR widget path for the filename");
            // How to find an element that does not have name or ID? We want xPath from Selenium
            // TODO xPath for form element without attributes, instead of TESTAR widget path
            // WdDriver.getRemoteWebDriver().findElement()
            // 2 forms without name or id would be using the same XML filename even if one of the them has more fields
            // Therefore, we add TESTAR widget path into the filename:
            path = uriPath + "_" + widget.get(Tags.Path, "");
        }
        path = path.replace("/", "_").replace("?", "_") + ".xml";
        String file_path = "settings/" + this.formFileFolder + "/" + path;
        //Updating action description:
        this.set(Tags.Desc, "Fill a form based on XML file: "+file_path);
        File f = new File(file_path);
        Map<String, String> fields = new HashMap<>();
        Boolean storeFile = true;
        // Check whether the XML file for this form exists already
        // If it exists, we read the XML file, and do not create a new one - storeFile = false
        if (f.exists()) {
            storeFile = false;
            fields = readFormFile(file_path);
            System.out.println("Derive FillForm Action : File exists, read the data from file");
        }

        CompoundAction.Builder formBuilder = new CompoundAction.Builder();
        int numberOfActions = buildForm(formBuilder, (WdWidget)widget, fields, storeFile, ac);

        if (storeFile && numberOfActions > 0) {
            storeToFile(path, fields);
            // By default create the performSubmit option as true and derive the submit action
            if (!formName.isEmpty()) {
                // If we found a form with a name property, use this property to execute a script submit action
                formBuilder.add(new WdSubmitAction(formName), 2);
            } else {
                // If the form does not contains a name property, derive a GUI click action
                // in the first submit widget of the form
                Widget input = findSubmitButtonOfForm(widget);
                formBuilder.add(ac.leftClickAt(input), 2);
            }
        } else if (fields != null && fields.containsKey("performSubmit")) {
            String submit = fields.get("performSubmit");
            if (submit.contains("true") && !formName.isEmpty()) {
                // If we found a form with a name property, use this property to execute a script submit action
                formBuilder.add(new WdSubmitAction(formName), 2);
            } else if(submit.contains("true")) {
                // If the form does not contains a name property, derive a GUI click action
                // in the first submit widget of the form
                Widget input = findSubmitButtonOfForm(widget);
                formBuilder.add(ac.leftClickAt(input), 2);
            }
        }

        // Creating a NOP action as a default and overwriting it with formFillingAction
        // If the form is empty or we cannot create actions on it, we return NOP action instead.
        // The NOP action should be filtered when calling this function
        Action formAction = new NOP();
        if (numberOfActions > 0) {
            formAction = formBuilder.build();
        } else {
            // If the form does not contain fields, we mark as a hidden form
            hidden = true;
        }

        formAction.set(Tags.OriginWidget, widget);
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Derive FillForm Action : storeToFile: " + result);
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
