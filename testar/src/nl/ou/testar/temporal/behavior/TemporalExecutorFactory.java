package nl.ou.testar.temporal.behavior;
import org.fruit.monkey.Settings;

public class TemporalExecutorFactory {
    public static TemporalExecutor getTemporalExecutor(Settings settings){

        return new TemporalExecutor(settings);
    }

}
