package org.testar.statemodel.axini;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class GuiModelLoader {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static GuiStateModel loadFromFile(String filePath) throws IOException {
		return objectMapper.readValue(new File(filePath), GuiStateModel.class);
	}

	public static GuiStateModel loadFromJson(String jsonString) throws IOException {
		return objectMapper.readValue(jsonString, GuiStateModel.class);
	}
}