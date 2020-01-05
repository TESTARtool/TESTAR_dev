package nl.ou.testar.ReinforcementLearning.Policies;

import com.google.common.collect.Multimap;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;

/**
 * Interface for a policy
 */
public interface Policy {

    /**
     *
     * @return
     */
    @Nonnull
    public Collection<Action> applyPolicy(@Nonnull final Multimap<Double, Action> actionQValues);

}
