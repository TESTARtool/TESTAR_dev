package org.testar.monkey.alayer.yolo;

import org.opencv.dnn.Dnn;

public class YoloOnnxModel extends YoloDnnModel {

	public YoloOnnxModel(String onnx) {
		// Load the trained model
		loadDnnNet(Dnn.readNetFromONNX(onnx));
	}

}
