package nl.ou.testar.ReinforcementLearning.Utils;

import nl.ou.testar.ReinforcementLearning.RewardFunctions.LRKeyrootsHelper;
import nl.ou.testar.ReinforcementLearning.RewardFunctions.WidgetStub;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.text.html.HTML;
import java.util.Deque;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class LRKeyrootsUtilTest {

    private LRKeyrootsHelper lrKeyrootsHelper;

    @Before
    public void setup() {
        lrKeyrootsHelper = new LRKeyrootsHelper();
    }

    @After
    public void cleanUp() {
        lrKeyrootsHelper = new LRKeyrootsHelper();
    }

    @Test
    public void getLRKeyroots_widgetHasNoChildNode() {
        // given
        final Widget widget = new WidgetStub();

        // when
        final Deque<Widget> lrKeyroots = lrKeyrootsHelper.getLRKeyroots(widget);

        // then
        assertTrue(lrKeyroots.contains(widget));
    }

    @Test
    public void getLRKeyroots_widgetHasOneChildNode() {
        // given
        final Widget widget1 = new WidgetStub();
        final WidgetStub widget0 = new WidgetStub();
        widget0.addChild(widget1);

        // when
        final Deque<Widget> lrKeyroots = lrKeyrootsHelper.getLRKeyroots(widget0);

        // then
        assertTrue(lrKeyroots.contains(widget0));
        assertFalse(lrKeyroots.contains(widget1));
    }

    @Test
    public void getLRKeyroots_widgetHasTwoChildNodes() {
        // given
        final Widget widget1 = new WidgetStub();
        widget1.set(Tags.ConcreteID, "AConcreteID");
        final Widget widget2 = new WidgetStub();
        widget2.set(Tags.ConcreteID, "concreteID");
        final WidgetStub widget0 = new WidgetStub();
        widget0.addChild(widget1);
        widget0.addChild(widget2);

        // when
        final Deque<Widget> lrKeyroots = lrKeyrootsHelper.getLRKeyroots(widget0);

        // then
        assertTrue(lrKeyroots.contains(widget0));
        assertFalse(lrKeyroots.toString(), lrKeyroots.contains(widget1));
        assertTrue(lrKeyroots.contains(widget2));
    }
}
