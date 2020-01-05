package nl.ou.testar.temporal.behavior;

import nl.ou.testar.temporal.util.TemporalType;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

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
        tcontrol = new TemporalController( settings);
    }


    public void LTLModelCheck() {
        if (settings.get(ConfigTags.TemporalOffLineEnabled)) {
            // tcontrol.setDefaultAPSelectormanager();
            tcontrol.ModelCheck(TemporalType.LTL,
                    settings.get(ConfigTags.TemporalLTLChecker),
                    settings.get(ConfigTags.TemporalLTLAPSelectorManager),
                    settings.get(ConfigTags.TemporalLTLOracles),
                    settings.get(ConfigTags.TemporalLTLVerbose));
        }
    }
}
