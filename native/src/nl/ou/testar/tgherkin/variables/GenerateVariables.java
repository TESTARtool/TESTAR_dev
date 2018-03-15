package nl.ou.testar.tgherkin.variables;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;
import org.fruit.alayer.linux.AtSpiTags;
import org.fruit.alayer.windows.UIATags;

import es.upv.staq.testar.NativeLinker;

/**
 * Generate variables for Tgherkin grammar.
 *
 */
public class GenerateVariables {
	
	// target file is created in tgherkin directory
	public static final String fileName = ".." + File.separator + "tgherkin" + File.separator + "src" + File.separator + "antlr" + File.separator + "Variables.g4";
	private static Map<String, Class<?>> tagMap = new HashMap<String, Class<?>>();;
	
	/**
	 * Main method.
	 * @param args given arguments
	 */
	public static void main(String args[]){
		// trigger class initialization, otherwise getNativeTags() returns the empty set
		@SuppressWarnings("unused")
		Object obj = Tags.Abstract_R_ID;
		obj = UIATags.UIAAcceleratorKey;
		obj = AtSpiTags.AtSpiCanScroll;
		for(Tag<?> nativeTag : NativeLinker.getNativeTags()) {
			fillTagMap(nativeTag);
		}
		generateGrammar(fileName);
	}
	
	private static void fillTagMap(Tag<?> tag){
		Class<?> variableType;
		if (tag.type() == Double.class) {
			variableType = Double.class;
		}else {
			if (tag.type() == Long.class) {
				variableType = Double.class;
			}else {
				if (tag.type() == Boolean.class) {
					variableType = Boolean.class;
				}else {
					if (tag.type() == String.class) {
						variableType = String.class;
					}else {
						// all other to String
						variableType = String.class;
					}
				}
			}
		}
		tagMap.put(tag.name(), variableType);
	}

	private static void generateGrammar(String fileName) {
		PrintWriter pWriter = null;
        try {
			FileWriter fWriter = new FileWriter(fileName);
			BufferedWriter bWriter = new BufferedWriter(fWriter);
			pWriter = new PrintWriter(bWriter);
			pWriter.println("lexer grammar Variables;");
			pWriter.println("@header {package nl.ou.testar.tgherkin.gen;}");
			writeVariableType(pWriter, tagMap, Boolean.class, "BOOLEAN_VARIABLE_NAME :");
			writeVariableType(pWriter, tagMap, Double.class, "NUMBER_VARIABLE_NAME :");
			writeVariableType(pWriter, tagMap, String.class, "STRING_VARIABLE_NAME :");
        }    
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (pWriter != null) {
                pWriter.close();
            }
        }
	}
	
	private static void writeVariableType(PrintWriter pWriter, Map<String, Class<?>> tagMap, Class<?> type, String typeName) {
		boolean notFirst = false;
		for (Map.Entry<String, Class<?>> entry : tagMap.entrySet()) {
			if (entry.getValue() == type) {
				if (notFirst) {
					pWriter.println(" | '" + entry.getKey() + "'");
				}else {
					notFirst = true;
					pWriter.println(typeName);
					pWriter.println("   '" + entry.getKey() + "'");
				}
			}
		}	
		if (notFirst) {
			pWriter.println(";");
		}
	}
	
	
}
