package nl.ou.testar.subroutine;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import org.fruit.UnProc;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

/**
 * Input table for Compound Action settings.
 *
 */
public class CompoundActionPanel extends InputDataPanel {

  private static final long serialVersionUID = 1L;

  public CompoundActionPanel() {
    super();
    
    setSTRING_A("Widget role:");
    setSTRING_B("Widget title: ");
    setSTRING_C("Input text: ");
    setTOOL_A(SubroutineTexts.getRoleTTT());
    setTOOL_B(SubroutineTexts.getTitleTTT());
    setTOOL_C(SubroutineTexts.getInputTTT());
  }
    
  /**
   * Populate Timing Fields from Settings structure.
   *
   * @param settings  The settings to load
   */
  @Override
  public void populateFrom(final Settings settings) {
    protocolClass = settings.get(ConfigTags.ProtocolClass);
    myClassPath = settings.get(ConfigTags.MyClassPath);
    checkClassProtocol();
    if (classBoolean) {
      // if exist, read data from input file
      setDataInputFile(new File(settings.get(ConfigTags.CompoundActionData)));
      input_data = readDataInputfile(); 
      maxIndexInputData = input_data.size();
      addSubroutine();
      if (maxIndexInputData > 0) {
        currentIndexInputData = 1; // start with first record
        showInputData(currentIndexInputData);
      }
      adjustRanges();
    }
  }
   
  @Override
  protected void checkClassProtocol() {
    classBoolean = false;
    URLClassLoader loader = null;
    try {

      URL[] classPath = new URL[myClassPath.size()];
      for (int i = 0; i < myClassPath.size(); i++) {
        classPath[i] = new File(myClassPath.get(i)).toURI().toURL();
      }

      // URL[] classPath = {new File("bin/oracle/").toURI().toURL()};
      loader = new URLClassLoader(classPath);

      @SuppressWarnings ("unchecked")
      UnProc<Settings> protocol = (UnProc<Settings>) loader
          .loadClass(protocolClass.replace("/", "."))
          .getConstructor().newInstance();
      if (CompoundActionProtocol.class.isAssignableFrom(protocol.getClass())) {
        classBoolean = true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (loader != null) {
        try {
          loader.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}