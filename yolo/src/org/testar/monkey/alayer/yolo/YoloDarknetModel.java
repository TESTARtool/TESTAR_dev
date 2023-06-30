package org.testar.monkey.alayer.yolo;

import org.opencv.dnn.Dnn;

public class YoloDarknetModel extends YoloDnnModel {

	public YoloDarknetModel(String weights, String cfg) {
		// Load the trained model
		loadDnnNet(Dnn.readNetFromDarknet(weights, cfg));
	}

}
