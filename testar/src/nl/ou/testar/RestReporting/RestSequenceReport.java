package nl.ou.testar.RestReporting;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.NameValuePair;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Verdict;

import nl.ou.testar.SequenceReport;

/**
 * RestSequenceReport
 */
public class RestSequenceReport implements SequenceReport {


    final CloseableHttpClient httpClient = HttpClientBuilder.create().build();
    final URI rootUri;

    public RestSequenceReport(URI rootUri) {
        this.rootUri = rootUri;
    }

    @Override
    public void addState(State state) {
        final HttpPost request = new HttpPost(rootUri);
        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        //TODO: fill request body content
        report.setEntity(new UrlEncodedFormEntity(params));
    }

    @Override
    public void addActions(Set<Action> actions) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addActionsAndUnvisitedActions(Set<Action> actions, Set<String> concreteIdsOfUnvisitedActions) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addSelectedAction(State state, Action action) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addTestVerdict(Verdict verdict) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        
    }
    
}
