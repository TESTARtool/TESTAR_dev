package nl.ou.testar.ReinforcementLearning.Utils;


import nl.ou.testar.ReinforcementLearning.RewardFunctions.WidgetStub;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TreeGenerator
{
    private final int maxChilds;
    public int total;
    public int width;
    public int height;
    private final Random rnd = new Random();

    public TreeGenerator(int maxChilds, int total)
    {
        this.maxChilds = maxChilds;
        this.total = total;
        this.width = 0;
        this.height = 0;
    }

    public WidgetStub CreateTree(int maxDepth, int childNum)
    {
        height = Math.min(height, maxDepth);
        final WidgetStub node = new WidgetStub();
        node.set(Tags.ConcreteID, String.format("%d-%d", maxDepth, childNum));
        node.set(Tags.AbstractIDCustom, String.format("%d-%d", maxDepth, childNum));
        if (maxDepth > 0) {
            int childsCount = rnd.nextInt(Math.min(maxChilds, total));
            final List<Integer> range = IntStream.rangeClosed(1, childsCount)
                    .boxed()
                    .collect(Collectors.toList());
            Collections.shuffle(range);
            total-=childsCount;
            width = Math.max(width, childsCount);
            for (int i = 0; i < childsCount; ++i) {
                node.addChild(CreateTree(maxDepth - 1, range.get(i)));
            }
        }
        return node;
    }

}