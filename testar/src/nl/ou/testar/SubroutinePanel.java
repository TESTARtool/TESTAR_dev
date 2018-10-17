package nl.ou.testar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.fruit.UnProc;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import nl.ou.testar.tgherkin.protocol.SubroutineProtocol;

import javax.swing.JLabel;
import static org.fruit.monkey.dialog.ToolTipTexts.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * Input table for subroutines settings.
 *
 */
public class SubroutinePanel extends JPanel {
	
	private static final long serialVersionUID = -5878966626046293031L;
	
	private static final String SUBROUTINE_DATA = "Subroutine data";

	private static final String N_TEXT = "Record number";
	private static final String URL_TEXT = "If URL contains text: ";
	private static final String FILE_TEXT = "use Tgherkin file: ";
	private static final String EXIT_TEXT = "exit URL";

	private static final int START_X = 20;
	private static final int START_Y = 80;
	private static final int TITLE_Y = 20;

	private static final int LABEL_WIDTH = 120;
	private static final int LABEL_HEIGHT = 30;
	private static final int TEXT_WIDTH = 300;
	private static final int N_WIDTH = 40;
	private static final int BUTTON_WIDTH = 60;
	private static final int BUTTON_WIDTH2 = 70;

	private static final int GAP_X = 5;
	private static final int GAP_X_L = GAP_X + LABEL_WIDTH;
	private static final int GAP_X_B = GAP_X + BUTTON_WIDTH2;
	private static final int GAP_Y = 10;
	private static final int GAP_Y_H = GAP_Y + LABEL_HEIGHT;

	private JTextField jTextFieldN;
	private JTextField jTextFieldUrl;
	private JTextField jTextFieldFilename;
	private JComboBox<String> jComboBoxFilename;
	private JTextField jTextFieldExit;
	
	private JButton btnPrevious = new JButton("<<");
	private JButton btnNext = new JButton(">>");
	private JButton btnNew = new JButton("New");
	private JButton btnDelete = new JButton("Delete");
	private JButton btnEdit = new JButton("Edit");
	private JButton btnCancel = new JButton("Cancel");
	private JButton btnSave = new JButton("Save");

	private HashMap<Integer,String[]> subroutineData = new HashMap<>();
	private int currentIndexSubroutineData = 0;
	private int maxIndexSubroutineData = 0;
	
	private String protocolClass;
	private File subroutineFile;
	private boolean subroutineProtocol;
	private boolean active;

	// used with class loader
	private List<String> myClassPath;
	// line number within Subroutine Panel
	private int lineNumber = 0;
	
	public SubroutinePanel() {
		setLayout(null);
		addSubroutine();
	}

	private void showButtons(boolean nnew, boolean ddelete, boolean eedit, boolean ccancel, boolean ssave) {
		btnNew.setEnabled(nnew);
		btnDelete.setEnabled(ddelete);
		btnEdit.setEnabled(eedit);
		btnEdit.setVisible(eedit);
		btnCancel.setEnabled(ccancel);
		btnCancel.setVisible(ccancel);
		btnSave.setEnabled(ssave);
		btnSave.setVisible(ssave);
	}
	
	private void addSubroutine() {
		
		JLabel jLabelIntro = new JLabel(SUBROUTINE_DATA);
		jLabelIntro.setBounds(
				START_X, 
				TITLE_Y, 
				TEXT_WIDTH, 
				LABEL_HEIGHT);
		add(jLabelIntro);

		// Record number selection
		JLabel jLabelN = new JLabel(N_TEXT);
		jLabelN.setBounds(
				START_X, 
				START_Y + lineNumber * GAP_Y_H, 
				LABEL_WIDTH, 
				LABEL_HEIGHT);
		add(jLabelN);

		btnNext.setBounds(
				START_X + GAP_X_L + BUTTON_WIDTH + GAP_X + N_WIDTH + GAP_X, 
				START_Y + lineNumber * GAP_Y_H, 
				BUTTON_WIDTH,
				LABEL_HEIGHT);
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				actionNextBtn();
			}
		});
		add(btnNext);

		jTextFieldN = new JTextField();
		jTextFieldN.setBounds(
				START_X + GAP_X_L + BUTTON_WIDTH + GAP_X, 
				START_Y + lineNumber * GAP_Y_H, N_WIDTH, 
				LABEL_HEIGHT);
		add(jTextFieldN);

		btnPrevious.setBounds(
				START_X + GAP_X_L, 
				START_Y + lineNumber * GAP_Y_H, 
				BUTTON_WIDTH, 
				LABEL_HEIGHT);
		btnPrevious.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					actionPreviousBtn();
				}
			});
		add(btnPrevious);

		lineNumber++;
		
		// Url component
		JLabel jLabelURL = new JLabel(URL_TEXT);
		jLabelURL.setBounds(
				START_X, 
				START_Y + lineNumber * GAP_Y_H, 
				LABEL_WIDTH, 
				LABEL_HEIGHT);
		jLabelURL.setToolTipText(urlTTT);
		add(jLabelURL);

		jTextFieldUrl = new JTextField();
		jTextFieldUrl.setBounds(
				START_X + GAP_X_L, 
				START_Y + lineNumber * GAP_Y_H, 
				TEXT_WIDTH, 
				LABEL_HEIGHT);
		jTextFieldUrl.setEditable(false);
		add(jTextFieldUrl);
		
		lineNumber++;
		
		// Tgherkin file
		JLabel jLabelFilename = new JLabel(FILE_TEXT);
		jLabelFilename.setBounds(
				START_X, 
				START_Y + lineNumber * GAP_Y_H, 
				LABEL_WIDTH, 
				LABEL_HEIGHT);
		add(jLabelFilename);

		jTextFieldFilename = new JTextField();
		jTextFieldFilename.setBounds(
				START_X + GAP_X_L, 
				START_Y + lineNumber * GAP_Y_H, 
				TEXT_WIDTH, 
				LABEL_HEIGHT);
		jTextFieldFilename.setVisible(true);
		jTextFieldFilename.setEditable(false);
		add(jTextFieldFilename);

		// select available Tgherkin file
		jComboBoxFilename = new JComboBox<String>();
		jComboBoxFilename.setBounds(
				START_X + GAP_X_L, 
				START_Y + lineNumber * GAP_Y_H, 
				TEXT_WIDTH, 
				LABEL_HEIGHT);
		jComboBoxFilename.setVisible(false);
		add(jComboBoxFilename);
		
		lineNumber++;
		
		// Exit URL
		JLabel jLabelExit = new JLabel(EXIT_TEXT);
		jLabelExit.setBounds(
				START_X, 
				START_Y + lineNumber * GAP_Y_H,
				LABEL_WIDTH, 
				LABEL_HEIGHT);
		add(jLabelExit);

		jTextFieldExit = new JTextField();
		jTextFieldExit.setBounds(
				START_X + GAP_X_L, 
				START_Y + lineNumber * GAP_Y_H, 
				TEXT_WIDTH, 
				LABEL_HEIGHT);
		jTextFieldExit.setVisible(true);
		jTextFieldExit.setEditable(false);
		add(jTextFieldExit);
		
		lineNumber++;		
				
		//Action buttons
		btnNew.setBounds(
				START_X + GAP_X_L, 
				START_Y + lineNumber * GAP_Y_H, 
				BUTTON_WIDTH2, 
				LABEL_HEIGHT);
		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				actionNewBtn();
			}
		});
		add(btnNew);

		btnDelete.setBounds(
				START_X + GAP_X_L + GAP_X_B, 
				START_Y + lineNumber * GAP_Y_H,
				BUTTON_WIDTH2, 
				LABEL_HEIGHT);
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				actionDeleteBtn();
			}
		});
		add(btnDelete);

		btnEdit.setBounds(
				START_X + GAP_X_L + 2 * GAP_X_B, 
				START_Y + lineNumber * GAP_Y_H, 
				BUTTON_WIDTH2, 
				LABEL_HEIGHT);
		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				actionEditBtn();
			}
		});
		add(btnEdit);
		
		btnCancel.setBounds(
				START_X + GAP_X_L + 2 * GAP_X_B, 
				START_Y + lineNumber * GAP_Y_H, 
				BUTTON_WIDTH2, 
				LABEL_HEIGHT);
		
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				actionCancelBtn();
			}
		});
		add(btnCancel);
	
		btnSave.setBounds(
				START_X + GAP_X_L + 3 * GAP_X_B, 
				START_Y + lineNumber * GAP_Y_H, 
				BUTTON_WIDTH2, 
				LABEL_HEIGHT);
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				actionSaveBtn();
			}
		});
		btnSave.setEnabled(false);
		add(btnSave);
	}
	
	// Button actions
	private void actionPreviousBtn() {
		currentIndexSubroutineData--;
    	jTextFieldFilename.setEnabled(true);
    	jTextFieldFilename.setVisible(true);
    	jComboBoxFilename.setVisible(false);
		showSubroutineData(currentIndexSubroutineData);
		adjustRanges();
	}

	private void actionNextBtn() {
		currentIndexSubroutineData++;
		showSubroutineData(currentIndexSubroutineData);
		adjustRanges();
	}
	
    private void actionNewBtn() {
    	currentIndexSubroutineData = subroutineData.size()+1;
    	jTextFieldN.setText(currentIndexSubroutineData + "");
    	jTextFieldUrl.setText("");
    	jTextFieldUrl.setEditable(true);
    	
    	jTextFieldFilename.setEnabled(false);
    	jTextFieldFilename.setVisible(false);
    	jComboBoxFilename.setVisible(true);
    	
    	jTextFieldExit.setText("");
    	jTextFieldExit.setEditable(true);
    	
		adjustRanges();
		showButtons(false, false, false, true, true);
	}
	
	private void actionDeleteBtn() {
		String[] dummy = new String[2];

		if (currentIndexSubroutineData == maxIndexSubroutineData) {
			if (currentIndexSubroutineData > 1) {
				dummy = subroutineData.get(currentIndexSubroutineData - 1);
				jTextFieldN.setText(currentIndexSubroutineData-1 + "");
			} else {
				jTextFieldN.setText("");
			}
			subroutineData.remove(currentIndexSubroutineData);
			jTextFieldUrl.setText(dummy[0]);
			jTextFieldFilename.setText(dummy[1]);
			jTextFieldExit.setText(dummy[2]);
			currentIndexSubroutineData--;
			maxIndexSubroutineData--;

		} else {

			for (Integer index : subroutineData.keySet()) {
				if (currentIndexSubroutineData <= index && index < maxIndexSubroutineData) {

					dummy = subroutineData.get(index + 1);
					subroutineData.put(index, dummy);
					if (index == currentIndexSubroutineData) {
						jTextFieldUrl.setText(dummy[0]);
						jTextFieldFilename.setText(dummy[1]);
						jTextFieldExit.setText(dummy[2]);
					}
				}
				if (index == maxIndexSubroutineData) {
					subroutineData.remove(index);
					maxIndexSubroutineData--;
				}
			}
		}
		adjustRanges();
		writeSubroutineData();
	}
	
	private void actionEditBtn() {
		jTextFieldUrl.setEditable(true);
		jTextFieldFilename.setVisible(false);
		jComboBoxFilename.setVisible(true);
		String selected = subroutineData.get(currentIndexSubroutineData)[1];
		jComboBoxFilename.setSelectedItem(selected); 
		
		showButtons(false, false, false, true, true);
	}

	private void actionCancelBtn() {
		if (currentIndexSubroutineData > maxIndexSubroutineData) {
			currentIndexSubroutineData--;
		}
		showSubroutineData(currentIndexSubroutineData);
		
		jTextFieldUrl.setEditable(false);
		jTextFieldFilename.setVisible(true);
		jComboBoxFilename.setVisible(false);
		
		adjustRanges();
		showButtons(true, true, true, false, false);
	}

    private void actionSaveBtn() {
    	String tgherkinDocument = (String) jComboBoxFilename.getSelectedItem();
    	
    	String[] invoer = {jTextFieldUrl.getText(),tgherkinDocument,jTextFieldExit.getText()};
    	subroutineData.put(currentIndexSubroutineData, invoer);
    	maxIndexSubroutineData = subroutineData.size();
    	writeSubroutineData();
    	
		jTextFieldUrl.setEditable(false);
		jTextFieldFilename.setEditable(false);
		jTextFieldFilename.setVisible(true);
		jComboBoxFilename.setVisible(false);
		jTextFieldExit.setEditable(false);
		
		adjustRanges();
		showButtons(true, true, true, false, false);
	}
	
	private void showSubroutineData(int index) {
		String url = subroutineData.get(index)[0];
		String tgherkinDocument = subroutineData.get(index)[1];
		String exitUrl = subroutineData.get(index)[2];
		jTextFieldN.setText(index + "");
		jTextFieldUrl.setText(url);
		jTextFieldFilename.setText(tgherkinDocument);
		jTextFieldExit.setText(exitUrl);
	}
	
	private void adjustRanges() {
		maxIndexSubroutineData = subroutineData.size();
		
		if (currentIndexSubroutineData == maxIndexSubroutineData) {
			btnNext.setEnabled(false);
		} else {
			btnNext.setEnabled(true);
		}
		if (currentIndexSubroutineData <= 1) {
			btnPrevious.setEnabled(false);
		} else {
			btnPrevious.setEnabled(true);
		}
	}
	
	/**
	 * Populate Timing Fields from Settings structure.
	 *
	 * @param settings  The settings to load.
	 */
	public void populateFrom(final Settings settings) {
		protocolClass = settings.get(ConfigTags.ProtocolClass);
		myClassPath = settings.get(ConfigTags.MyClassPath);

		subroutineFile = new File(settings.get(ConfigTags.SubroutineData));
		listTgherkinDocuments();
		checkSubroutineProtocol();
		if (subroutineProtocol) {
			readSubroutineData(); // if exist, read data from input file
			maxIndexSubroutineData = subroutineData.size();
			if (maxIndexSubroutineData > 0) {
				currentIndexSubroutineData++; // start with first record
				showSubroutineData(currentIndexSubroutineData);
			}
			adjustRanges();
		}	
	}
	
	/**
	 * List available Tgherkin documents in protocol specific settings folder.
	 */
	private void listTgherkinDocuments() {
		String[] tgherkinDocuments = new File("./resources/settings/" + protocolClass.split("/")[0] + "/")
				.list(new FilenameFilter() {
					@Override
					public boolean accept(File current, String name) {
						return name.toLowerCase().endsWith(".tgherkin");
					}
				});
		Arrays.sort(tgherkinDocuments);
		jComboBoxFilename.setModel(new DefaultComboBoxModel<String>(tgherkinDocuments));
	}
	
	/**
	 * Read data from subroutine input File. 
	 * Name of input file is stored in ConfigTag SubroutineData (see settings)
	 * Data format (.csv) number|url|tgherkinfile (subroutine)|exit url
	 * Contents of file will be stored in subroutineData
	 */
	private void readSubroutineData() {
		int index = 0;
	    try (Scanner in = new Scanner(subroutineFile)) {
	      while (in.hasNextLine()) {
	    	String instring = in.nextLine();
	    	int n = instring.indexOf("|");
	        String a = instring.substring(0,n);
	        String b = instring.substring(n+1);
	        instring = b;
	        n = instring.indexOf("|");
	        b = instring.substring(0,n);
	        String c = instring.substring(n+1);
	        if (a != null && b != null && c != null) {
	        	index++;
	        	String[] r = {a, b, c};
	        	subroutineData.put(index, r);
	        }
	      }
	      maxIndexSubroutineData = index ;
	    } catch (FileNotFoundException e) {
	    }
	}

	/**
	 * Write data from subroutine to input File. 
	 * Name of input file is stored in ConfigTag SubroutineData (see settings)
	 * Data format (.csv) number|url|subroutineAction|exit url
	 * Contents of file will be stored in subroutineData
	 */
	private void writeSubroutineData() {

		try {
			String fileName = subroutineFile.getAbsolutePath();
			File file = new File(fileName);
			file.delete();
			
			FileWriter writer = new FileWriter(fileName, true);
			for (Integer index: subroutineData.keySet()) {
				String[] invoer = subroutineData.get(index);

				String line = invoer[0] + "|" + invoer[1];
				writer.write(line);
				writer.write(System.lineSeparator());
			}
			if (writer != null) {
				writer.flush();
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void checkSubroutineProtocol() {
		subroutineProtocol = false;
		URLClassLoader loader = null;
		try {
			URL[] classPath = new URL[myClassPath.size()];
			for(int i = 0; i < myClassPath.size(); i++) {
				classPath[i] = new File(myClassPath.get(i)).toURI().toURL();
			}
			// URL[] classPath = {new File("bin/oracle/").toURI().toURL()};
			loader = new URLClassLoader(classPath);
			
			@SuppressWarnings("unchecked")
			UnProc<Settings> protocol = (UnProc<Settings>) loader.loadClass(protocolClass.replace("/", "."))
					.getConstructor().newInstance();
			if (SubroutineProtocol.class.isAssignableFrom(protocol.getClass())) {
				subroutineProtocol = true;
			}

	    } catch(Exception e) {
			e.printStackTrace();
		} finally {
			if (loader != null) {
				try { 
					loader.close(); 
				} catch (IOException e) {}
			}	
		}
	}	

	/**
	 * Retrieve information from the Timing GUI.
	 *
	 * @param settings  reference to the object where the settings will be stored.
	 */
	public void extractInformation(final Settings settings) {
	}
	
	/**
	 * Retrieve subroutine protocol indicator.
	 * @return subroutine protocol indicator
	 */
	public boolean isSubroutineProtocol() {
		return subroutineProtocol;
	}

	/**
	 * Retrieve active indicator.
	 * @return active indicator
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Set active indicator.
	 * @param active the active indicator	  
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
}
