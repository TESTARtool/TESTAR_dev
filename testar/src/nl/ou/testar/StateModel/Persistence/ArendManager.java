package nl.ou.testar.StateModel.Persistence;

import java.util.ArrayList;
import java.util.HashMap;

import com.orientechnologies.orient.core.sql.executor.OResultSet;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import org.fruit.alayer.Action;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;

import nl.ou.testar.StateModel.AbstractState;
import nl.ou.testar.StateModel.Persistence.OrientDB.Entity.EntityManager;
import nl.ou.testar.StateModel.Util.EventHelper;

public class ArendManager {


    public EventHelper helper;
    public EntityManager manager;
    public ArendManager(EventHelper eventHelper, EntityManager entityManager) {
        this.helper = eventHelper;
        this.manager = entityManager;
        System.out.println("ArendManager is geinitialiseerd");
    }

    public void UpdateAbstractActionInProgress(Action a, String node)
    {
        var e = manager.getConnection();
        var session = e.getDatabaseSession();
        // sql = create vertex BeingExecuted set node = 'node'
        //String sql = "create vertex BeingExecuted set node = '"+node+"'";
        //System.out.println(sql);

        String sql = "update edge UnvisitedAbstractAction set in = (SELECT FROM BeingExecuted WHERE node='"+node+"') where actionId='"+a.get(Tags.AbstractIDCustom)+"'";
        System.out.println("Execute" +sql);
        manager.getConnection().getDatabaseSession().query(sql);

        

        // update edge set the edge to beingexecuted
        // sql = "update edge UnvisitedAbstractAction set in = (SELECT FROM BeingExecuted WHERE node='mynode') where actionId='AACh6z2i1f277813369'"


    }

    public ArrayList<Action> GetUnvisitedAbstractActions(AbstractState currentState)
    {
        ArrayList<Action> lijst = new ArrayList<Action>();
        return lijst;

    }

    public OResultSet ExecuteCommand(String actie)
    {
        System.out.println("ExecuteCommand "+actie);
        
        return manager.getConnection().getDatabaseSession().command(actie);
    }
    public OResultSet ExecuteQuery(String actie)
    {
        System.out.println("ExecuteQuery "+actie);
        
        return manager.getConnection().getDatabaseSession().query(actie);
    }
    
}
