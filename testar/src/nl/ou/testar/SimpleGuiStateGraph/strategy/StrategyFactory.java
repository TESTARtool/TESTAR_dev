package nl.ou.testar.SimpleGuiStateGraph.strategy;

import nl.ou.testar.SimpleGuiStateGraph.strategy.actionTypes.StrategyNode;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class StrategyFactory {
    private Queue<AvailableReturnTypes> queue = new LinkedList<>();

    public StrategyFactory(final String strategy) {
        if (strategy.endsWith(".txt")) {
            makeQueue(getStrategyFromFile(strategy));
        } else {
            System.out.println("Strategy: " + strategy);
            makeQueue(strategy);
        }
    }

    private String getStrategyFromFile(String strategyFile) {
        String strategyFromFile = "";
        try {
            final BufferedReader br = new BufferedReader(new FileReader("settings/" + strategyFile));
            strategyFromFile = br.readLine();
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("File not found. Current directory: " + System.getProperty("user.dir"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Strategy from file: " + strategyFromFile);
        return strategyFromFile;
    }

    public StrategyActionSelector getStrategyActionSelector() {
        return new StrategyActionSelectorImpl(queue);
    }

    private void makeQueue(final String strategy) {
        Arrays.stream(strategy.replace(" ", "")
                .replace("(", "")
                .replace(")", "")
                .split(":"))
                .forEach(s -> queue.add(AvailableReturnTypes.valueOf(s.replace("-", "").toUpperCase())));
    }
}
