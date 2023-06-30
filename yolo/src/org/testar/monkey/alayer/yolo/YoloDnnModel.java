package org.testar.monkey.alayer.yolo;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.Rect2d;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class YoloDnnModel {

	// Load OpenCV DLLs libraries
	static{
		nu.pattern.OpenCV.loadShared();
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	private Net dnnNet;

	public void loadDnnNet(Net dnnNet) {
		this.dnnNet = dnnNet;
	}

	public void detectObjectOnImage(String image) throws FileNotFoundException {
		// load our input image
		Mat img = Imgcodecs.imread(image, Imgcodecs.IMREAD_COLOR);

		System.out.println("-------- layerNames --------");
		List<String> layerNames = dnnNet.getLayerNames();
		layerNames.forEach(System.out::println);

		System.out.println("-------- outputLayers --------");
		List<String> outputLayers = dnnNet.getUnconnectedOutLayersNames();
		outputLayers.forEach(System.out::println);

		HashMap<String, List> result = new HashMap<>();
		result.put("boxes", new ArrayList<Rect2d>());
		result.put("confidences", new ArrayList<Float>());
		result.put("class_ids", new ArrayList<Integer>());

		Mat blob_from_image = Dnn.blobFromImage(img, 1/255.0, new Size(640, 640), new Scalar(0), true, false);
		dnnNet.setInput(blob_from_image);

		List<Mat> outputs = new ArrayList<Mat>();

		dnnNet.forward(outputs, outputLayers);

		System.out.println("outputs: " + outputs.size());
		for(Mat output : outputs) {
			//  loop over each of the detections. Each row is a candidate detection,
			System.out.println("Output.rows(): " + output.rows() + ", Output.cols(): " + output.cols());
			output.size(1);
			for (int i = 0; i < output.rows(); i++) {
				Mat row = output.row(i);
				List<Float> detect = new MatOfFloat(row).toList();
				List<Float> score = detect.subList(5, output.cols());
				int class_id = argmax(score); // index maximalnog elementa liste
				float conf = score.get(class_id);
				if (conf >= 0.5) {
					int center_x = (int) (detect.get(0) * img.cols());
					int center_y = (int) (detect.get(1) * img.rows());
					int width = (int) (detect.get(2) * img.cols());
					int height = (int) (detect.get(3) * img.rows());
					int x = (center_x - width / 2);
					int y = (center_y - height / 2);
					Rect2d box = new Rect2d(x, y, width, height);
					result.get("boxes").add(box);
					result.get("confidences").add(conf);
					result.get("class_ids").add(class_id);
				}
			}
		}

		ArrayList<Rect2d> boxes = (ArrayList<Rect2d>)result.get("boxes");
		ArrayList<Float> confidences = (ArrayList<Float>) result.get("confidences");
		ArrayList<Integer> class_ids = (ArrayList<Integer>)result.get("class_ids"); 

		System.out.println("boxes: " + boxes.size());
		System.out.println("confidences: " + confidences.size());
		System.out.println("class_ids: " + class_ids.size());

		for(Rect2d box : boxes) {
			System.out.println("box: " + box);
		}

		//-- Finally, go over indices in order to draw bounding boxes on the image:
		img = drawBoxesOnTheImage(img,
				boxes,
				class_ids);
		HighGui.imshow("Test", img );    
		HighGui.waitKey(10000); 
	}

	/**
	 * Returns index of maximum element in the list
	 */
	private  int argmax(List<Float> array) {
		float max = array.get(0);
		int re = 0;
		for (int i = 1; i < array.size(); i++) {
			if (array.get(i) > max) {
				max = array.get(i);
				re = i;
			}
		}
		return re;
	}

	private Mat drawBoxesOnTheImage(Mat img, 
			ArrayList<Rect2d> boxes,
			ArrayList<Integer> class_ids) {
		for (int i = 0; i < boxes.size(); i++) {
			Rect2d box = boxes.get(i);
			org.opencv.core.Point x_y = new  org.opencv.core.Point(box.x, box.y);
			org.opencv.core.Point w_h = new  org.opencv.core.Point(box.x + box.width, box.y + box.height);
			org.opencv.core.Point text_point = new  org.opencv.core.Point(box.x, box.y - 5);
			Imgproc.rectangle(img, w_h, x_y, new Scalar(100,100, 0,255), 1);
			Imgproc.putText(img, "object", text_point, 1, 1, new Scalar(100,100, 0,255), 2);
		}  
		return img;
	}

}
