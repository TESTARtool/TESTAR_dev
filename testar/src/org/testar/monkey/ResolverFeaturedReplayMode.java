package org.testar.monkey;

import nl.ou.testar.resolver.OrientDBSerialResolver;
import org.fruit.monkey.orientdb.OrientDBService;

public class ResolverFeaturedReplayMode {

    public void runReplayLoop(DefaultProtocol protocol, OrientDBService orientService) {

        if (protocol.settings.get(ConfigTags.ReportType).equals(Settings.SUT_REPORT_DATABASE)) {

            //MySQLSerialResolver databaseReplayResolver = new MySQLSerialResolver(sqlService, settings);
            OrientDBSerialResolver orientDbSerialResolver = new OrientDBSerialResolver(orientService, protocol.settings, protocol.stateModelManager);

            try {
                orientDbSerialResolver.startReplay(protocol.settings.get(ConfigTags.SQLReporting));
                protocol.assignActionResolver(orientDbSerialResolver);
//                databaseReplayResolver.startReplay(settings.get(ConfigTags.SQLReporting));
//                assignActionResolver(databaseReplayResolver);
                new GenerateMode().runGenerateOuterLoop(protocol, null);
                protocol.resignActionResolver();
            }
            catch (Exception e) {
                System.err.println("Failed to replay from a database");
                e.printStackTrace();
            }
        }
        else {
            System.out.println("-= No database available - falling back =-");
            System.out.println(protocol.settings.get(ConfigTags.ReportType));
            new ReplayMode().runReplayLoop(protocol);
        }
    }
}
