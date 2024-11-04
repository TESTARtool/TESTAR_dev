package org.testar.action.priorization.llm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface LlmConversation {
    static final Logger logger = LogManager.getLogger();

    public void initConversation(String fewshotFile);
    public void addMessage(String role, String content);

    /**
     * Loads a resource from the resources folder as plain text.
     * @param resourceLocation Location of the resource to load.
     * @return The resource as string.
     * @throws Exception When the resource failed to load or does not exist.
     */
    default String getTextResource(String resourceLocation) throws Exception {
        ClassLoader classLoader = LlmConversation.class.getClassLoader();
        // Try to find the file from the resource stream of system file path
        InputStream inputStream = classLoader.getResourceAsStream(resourceLocation) != null 
        		? classLoader.getResourceAsStream(resourceLocation) 
        				: Files.exists(Paths.get(resourceLocation)) 
        				? Files.newInputStream(Paths.get(resourceLocation)) 
        						: null;

        if (inputStream != null) {
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                return stringBuilder.toString();
            } catch (IOException e) {
                logger.log(Level.ERROR, "Unable to read resource " + resourceLocation);
                e.printStackTrace();
            }

        } else {
            logger.log(Level.ERROR, "Unable to load resource " + resourceLocation);
        }

        throw new Exception("Failed to load text resource, double check the resource location.");
    }
}
