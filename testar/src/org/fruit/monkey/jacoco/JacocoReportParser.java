package org.fruit.monkey.jacoco;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class JacocoReportParser {
    private String filePath;

    private List<MethodCoverage> methodsCoverage;

    private static final String PACKAGE_NODE_NAME = "package";
    private static final String CLASS_NODE_NAME = "class";
    private static final String METHOD_NODE_NAME = "method";
    private static final String NAME_ATTRIBUTE_NAME = "name";
    private static final String TYPE_ATTRIBUTE_NAME = "type";
    private static final String INIT_LINE_ATTRIBUTE_NAME= "line";
    private static final String COVERED_ATTRIBUTE_NAME = "covered";
    private static final String MISSED_ATTRIBUTE_NAME = "missed";
    private static final String LINE_TYPE_ATTRIBUTE = "LINE";

    public JacocoReportParser(String filePath) {
        this.filePath = filePath;
    }

    public List<MethodCoverage> parseReport() {
        this.methodsCoverage = new ArrayList<>();

        var documentBuilderFactory = DocumentBuilderFactory.newInstance();

        var reportFile = new File(filePath);

        try (var reportInputStream = new FileInputStream(reportFile)) {
            documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            var documentBuilder = documentBuilderFactory.newDocumentBuilder();

            var document = documentBuilder.parse(reportInputStream);

            parseRoot(document.getDocumentElement());
            return methodsCoverage;

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void parse(Consumer<Node> parsingMethod, Node node) {
        var childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            parsingMethod.accept(childNodes.item(i));
        }
    }

    private void parseRoot(Node rootNode) {
        parse(childNode -> {
            if (PACKAGE_NODE_NAME.equals(childNode.getNodeName())) {
                parsePackage(childNode);
            } else {
                parseRoot(childNode);
            }
        }, rootNode);

    }

    private void parsePackage(Node packageNode) {
        parse(childNode -> {
            if (CLASS_NODE_NAME.equals(childNode.getNodeName())) {
                parseClass(childNode);
            } else if (PACKAGE_NODE_NAME.equals(childNode.getNodeName())) {
                parsePackage(childNode);
            }
        }, packageNode);
    }

    private void parseClass(Node classNode) {
        parse(childNode -> {
            if (CLASS_NODE_NAME.equals(childNode.getNodeName())) {
                parseClass(childNode);
            } else if (METHOD_NODE_NAME.equals(childNode.getNodeName())) {
                parseMethod(childNode, parseClassName(classNode));
            }
        }, classNode);
    }

    private void parseMethod(Node methodNode, String className) {
        var methodName = methodNode.getAttributes().getNamedItem(NAME_ATTRIBUTE_NAME).getNodeValue();
        parse(childNode -> {
            if (childNode.getAttributes() != null &&
                    childNode.getAttributes().getNamedItem(TYPE_ATTRIBUTE_NAME).getNodeValue().equals(LINE_TYPE_ATTRIBUTE)) {
                methodsCoverage.add(new MethodCoverage(className,
                                                       methodName,
                                                       Integer.parseInt(methodNode.getAttributes()
                                                                                  .getNamedItem(INIT_LINE_ATTRIBUTE_NAME)
                                                                                  .getNodeValue()),
                                                       Integer.parseInt(childNode.getAttributes()
                                                                                 .getNamedItem(COVERED_ATTRIBUTE_NAME)
                                                                                 .getNodeValue()),
                                                       Integer.parseInt(childNode.getAttributes()
                                                                                 .getNamedItem(MISSED_ATTRIBUTE_NAME)
                                                                                 .getNodeValue())));
            }
        }, methodNode);
    }

    private List<String> parseMethodParameters(String methodDescription) {
        return null;
    }

    private String parseClassName(Node classNode) {
        return classNode.getAttributes()
                        .getNamedItem(NAME_ATTRIBUTE_NAME)
                        .getNodeValue()
                        .replace("/", ".")
                        .replace("$", ".");
    }
}
