package nl.ou.testar.StateModel.automation;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import es.upv.staq.testar.StateManagementTags;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Config;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.Connection;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Manager {

    private Config config;

    private Connection connection;

    public Manager(Settings settings) {
        config = new Config();
        config.setConnectionType(settings.get(ConfigTags.DataStoreType));
        config.setServer(settings.get(ConfigTags.DataStoreServer));
        config.setDatabase("testar_stats");
        config.setUser(settings.get(ConfigTags.DataStoreUser));
        config.setPassword(settings.get(ConfigTags.DataStorePassword));
        config.setResetDataStore(settings.get(ConfigTags.ResetDataStore));
        config.setDatabaseDirectory(settings.get(ConfigTags.DataStoreDirectory));
        String connectionString = config.getConnectionType() + ":" + (config.getConnectionType().equals("remote") ?
                config.getServer() : config.getDatabaseDirectory()) + "/";
        OrientDB orientDB = new OrientDB(connectionString, OrientDBConfig.defaultConfig());
        connection = new Connection(orientDB, config);
    }

    public void fillDataStoreWithAttributes() {
        try (ODatabaseSession db = connection.getDatabaseSession()) {
            String query = "INSERT INTO DeterminismAttributes SET abstraction_attributes = :attributes , uid = :uid";
            HashSet<String> attributeSet = new HashSet<>();
            StateManagementTags.getAllTags().forEach(tag -> {
                if (StateManagementTags.getTagGroup(tag) == StateManagementTags.Group.General) {
                    attributeSet.add(StateManagementTags.getSettingsStringFromTag(tag));
                    Map<String, Object> params = new HashMap<>();
                    params.put("attributes", attributeSet);
                    params.put("uid", StateManagementTags.getSettingsStringFromTag(tag));
                    db.command(query, params);
                    attributeSet.clear();
                }
            });
        }
    }

    public Set<Set<String>> getAttributeLists() {
        try (ODatabaseSession db = connection.getDatabaseSession()) {
            String query = "select from DeterminismAttributes WHERE runs IS NULL ORDER BY uid";
            OResultSet resultSet = db.query(query);
            Set<Set<String>> attributes = new HashSet<>();
            while (resultSet.hasNext()) {
                OResult result = resultSet.next();
                Set<String> atts  = result.getProperty("abstraction_attributes");
                attributes.add(atts);
            }
            return attributes;
        }
    }


}
