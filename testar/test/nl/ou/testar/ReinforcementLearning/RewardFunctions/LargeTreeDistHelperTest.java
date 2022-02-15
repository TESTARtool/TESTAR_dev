package nl.ou.testar.ReinforcementLearning.RewardFunctions;

import nl.ou.testar.ReinforcementLearning.Utils.TreeGenerator;
import org.apache.commons.collections.map.MultiKeyMap;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;

import java.util.Deque;

import static junit.framework.TestCase.fail;

/**
 * This unit test is based on an example in the article
 * "Simple Fast Algorithms for the Editing Distance Between Trees and Related Problems"
 * by Zhang AND Shasha
 * DOI: 10.1137/0218082
 */
public class LargeTreeDistHelperTest {

    public void treeDist_LargeTree() {
        // given
        try {
            final MultiKeyMap forestDist = new MultiKeyMap();
            final MultiKeyMap treeDist = new MultiKeyMap();
            final LRKeyrootsHelper lrKeyrootsHelper = new LRKeyrootsHelper();
            final StateStub pState = new StateStub();
            final StateStub sState = new StateStub();
            int childs = 800;
            int depth = 5;
            pState.set(Tags.ConcreteID, String.format("%d-%d", childs, childs));
            pState.set(Tags.AbstractIDCustom, String.format("%d-%d", childs, childs));

            sState.set(Tags.ConcreteID, String.format("%d-%d", childs, childs));
            sState.set(Tags.AbstractIDCustom, String.format("%d-%d", childs, childs));

            final TreeGenerator t1 = new TreeGenerator(childs, 800);
            final TreeGenerator t2 = new TreeGenerator(childs, 800);
            final WidgetStub widget1 = t1.CreateTree(depth, 0);
            final WidgetStub widget2 = t2.CreateTree(depth, 0);
            pState.addChild(widget1);
            widget1.setParent(pState);
            sState.addChild(widget2);
            widget2.setParent(sState);
            final Deque<Widget> lrKeyroots1 = lrKeyrootsHelper.getLRKeyroots(pState);
            final Deque<Widget> lrKeyroots2 = lrKeyrootsHelper.getLRKeyroots(sState);
            final TreeDistHelper tdh = new TreeDistHelper();
            System.out.println(String.format("Number of keyroots for tree1: %s", lrKeyroots1.size()));
            System.out.println(String.format("Number of keyroots for tree2: %s", lrKeyroots2.size()));

            // when
            final long startTime = System.currentTimeMillis();
            for (final Widget keyRoot1 : lrKeyroots1) {
                final long startTimeTreedist = System.currentTimeMillis();

                for (final Widget keyRoot2 : lrKeyroots2) {
                    tdh.treeDist(keyRoot1, keyRoot2, forestDist, treeDist);
                    forestDist.clear();
                }
                final long endTimeTreeDist = System.currentTimeMillis();
                System.out.println(String.format("Treedist after node comparison keyRoot1 took %s milli seconds", endTimeTreeDist - startTimeTreedist));
            }
            final long endTime = System.currentTimeMillis();
            System.out.println(String.format("Treedist for all keyroots took %s milli seconds", endTime - startTime));
        }
        catch (final Exception e) {
            System.out.println("An error occurred.");
            System.out.println(e.getMessage());
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}

