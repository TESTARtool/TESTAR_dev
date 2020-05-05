package nl.ou.testar.temporal.control;
import org.fruit.monkey.Settings;

public class TemporalControllerFactory {
    public static TemporalController getTemporalController(Settings settings){
        return new TemporalController(settings);
    }
}
