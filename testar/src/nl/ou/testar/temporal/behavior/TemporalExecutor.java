package nl.ou.testar.temporal.behavior;

import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Config;
import nl.ou.testar.temporal.util.Helper;
import nl.ou.testar.temporal.util.StreamConsumer;
import nl.ou.testar.temporal.util.TemporalType;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import java.io.File;

import static org.fruit.monkey.Main.outputDir;

public class TemporalExecutor {
    private TemporalController tcontrol;
    private Settings settings;

    private String dataStoreText;
    private String dataStoreServerDNS;
    private String dataStoreDirectory;
    private String dataStoreDBText;
    private String dataStoreUser;
    private String dataStorePassword;
    private String dataStoreType;
    private String ApplicationName;
    private String ApplicationVersion;


    public TemporalExecutor(final Settings settings) {
        this.settings = settings;
        tcontrol = new TemporalController();
        init();
    }
    private void init(){
        outputDir = settings.get(ConfigTags.OutputDir);
        // check if the output directory has a trailing line separator
        if (!outputDir.substring(outputDir.length() - 1).equals(File.separator)) {
            outputDir += File.separator;
        }
        outputDir = outputDir + settings.get(ConfigTags.TemporalDirectory);

        if(settings.get(ConfigTags.TemporalSubDirectories)) {
            String runFolder = Helper.CurrentDateToFolder();
            outputDir = outputDir + File.separator + runFolder;
        }
        new File(outputDir).mkdirs();
        outputDir = outputDir + File.separator;

        // used here, but controlled on StateModelPanel
        dataStoreText = settings.get(ConfigTags.DataStore);
        dataStoreServerDNS = settings.get(ConfigTags.DataStoreServer);
        dataStoreDirectory = settings.get(ConfigTags.DataStoreDirectory);
        dataStoreDBText = settings.get(ConfigTags.DataStoreDB);
        dataStoreUser = settings.get(ConfigTags.DataStoreUser);
        dataStorePassword = settings.get(ConfigTags.DataStorePassword);
        dataStoreType = settings.get(ConfigTags.DataStoreType);


        Config dbconfig = new Config();
        dbconfig.setConnectionType(dataStoreType);
        dbconfig.setServer(dataStoreServerDNS);
        dbconfig.setDatabase(dataStoreDBText);
        dbconfig.setUser(dataStoreUser);
        dbconfig.setPassword(dataStorePassword);
        dbconfig.setDatabaseDirectory(dataStoreDirectory);
        ApplicationName= settings.get(ConfigTags.ApplicationName);
        ApplicationVersion= settings.get(ConfigTags.ApplicationVersion);
        tcontrol = new TemporalController(ApplicationName,ApplicationVersion,dbconfig, outputDir);
    }
    public void LTLModelCheck() {
        if (settings.get(ConfigTags.TemporalEnabled)) {
           // tcontrol.setDefaultAPSelectormanager();
            tcontrol.ModelCheck(TemporalType.LTL,
                    settings.get(ConfigTags.TemporalLTLChecker),
                    settings.get(ConfigTags.TemporalLTLAPSelectorManager),
                    settings.get(ConfigTags.TemporalLTLOracles),
                    settings.get(ConfigTags.TemporalLTLVerbose));
        }
    }
}
