package nl.ou.testar.tgherkin.grammar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.fruit.alayer.devices.KBKeys;

/**
 * Generate constants for Tgherkin grammar.
 *
 */
public class GenerateConstants {

	// target file is created in tgherkin directory
	public static final String fileName = ".." + File.separator + "tgherkin" + File.separator + "src" + File.separator + "antlr" + File.separator + "Constants.g4";

	/**
	 * Main method.
	 * @param args given arguments
	 */
	public static void main(String args[]){
		generateGrammarFile();
	}

	public static void generateGrammarFile() {
		PrintWriter pWriter = null;
		try {
			FileWriter fWriter = new FileWriter(fileName);
			BufferedWriter bWriter = new BufferedWriter(fWriter);
			pWriter = new PrintWriter(bWriter);
			boolean notFirst = false;
			for(KBKeys key : KBKeys.values()) {
				if (notFirst) {
					pWriter.println(" | '" + key.name() + "'");
				}else {
					notFirst = true;
					pWriter.println("lexer grammar Constants;");
					pWriter.println("KB_KEY_NAME :");
					pWriter.println("   '" + key.name() + "'");
				}
			}
			if (notFirst) {
				pWriter.println(";");
			}
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

}
