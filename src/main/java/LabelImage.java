
/* Copyright 2016 The TensorFlow Authors. All Rights Reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package main.java;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import org.tensorflow.DataType;
import org.tensorflow.Graph;
import org.tensorflow.Output;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.TensorFlow;
import org.tensorflow.types.UInt8;

/**
 * Sample use of the TensorFlow Java API to label images using a pre-trained
 * model.
 * 
 * Modified by David Roy Originally from TensorFlow authors
 * Modifications
 * -Added a filter on the classification method to 
 * prevent irrelevant results from being returned.
 * -Added the topLabel system
 * see below
 */
public class LabelImage {

	static String modelDir = "src/inception5h";
	static byte[] graphDef = readAllBytesOrExit(Paths.get(modelDir, "tensorflow_inception_graph.pb"));
	static List<String> labels = readAllLinesOrExit(Paths.get(modelDir, "imagenet_comp_graph_label_strings.txt"));
	
	//labels that are relevant to NYC traffic
	static String[] topLabels = { "cab", "school bus", "limousine", "jeep", "convertible", "racer", "beach wagon",
			"sports car", "moped", "fire engine", "garbage truck", "pickup", "tow truck", "trailer truck", "moving van",
			"police van", "streetcar", "wreck", "taxi", "trolleybus", "minibus", "recreational vehicle", "tractor",
			"car wheel", "street sign", "parallel bars", "traffic light"};
	static List<String> labelsList = Arrays.asList(topLabels);

	/**
	 * Modified by David Roy Originally from TensorFlow authors
	 * 
	 * @param image
	 * @return
	 * @throws IOException
	 */
	public String classify(BufferedImage bufferedImage) throws IOException {
		
		/*
		 * @author David
		 * save the image as a file
		 */
		File outputfile = new File("src/image.jpg");
		ImageIO.write(bufferedImage, "jpg", outputfile);

		String imageFile = "src/image.jpg";

		String result = "";

		byte[] imageBytes = readAllBytesOrExit(Paths.get(imageFile));

		try (Tensor<Float> image = constructAndExecuteGraphToNormalizeImage(imageBytes)) {
			float[] labelProbabilities = executeInceptionGraph(graphDef, image);
			
			/*
			 * @author David
			 * query for the top 4 matching labels
			 * based on image
			 */
			List<Label> topLabels = null;
			Label topLabel = null;

			// flag if these labels are in topLabels
			Label movingVan = null;
			Label schoolBus = null;
			boolean empty = true;

			/*
			 * @author David
			 * iterate through the topLabels 
			 * list to search for labels of
			 * interest
			 */
			while (empty) {
				topLabels = getTopLabels(labelProbabilities);
				
				for (Label l : topLabels) {
					if (l.getLabel().equalsIgnoreCase("moving van")) {
						movingVan = l;
					}

					if (l.getLabel().equalsIgnoreCase("school bus")) {
						schoolBus = l;
					}

				}
				empty = topLabels.size() == 0;
			}

			topLabel = topLabels.get(0);
			
			/*
			 * @author David
			 */
			// if the model sees a yellow vehicle, it may label it a school bus
			// moving van is also a common tendency
			if (movingVan != null && schoolBus != null) {
				topLabel = new Label("taxi cab", movingVan.getProbability() * schoolBus.getProbability());
			}
			
			if (topLabel.getLabel().equals("parallel bars")) {
				topLabel = new Label("crosswalk", topLabel.getProbability());
			}

			
			result = String.format("BEST MATCH: %s (%.2f%% likely)", topLabel.getLabel(),
					topLabel.getProbability() * 100f);
		}

		return result;
	}

	// @author Tensorflow
	private static Tensor<Float> constructAndExecuteGraphToNormalizeImage(byte[] imageBytes) {
		try (Graph g = new Graph()) {
			GraphBuilder b = new GraphBuilder(g);
			// Some constants specific to the pre-trained model at:
			// https://storage.googleapis.com/download.tensorflow.org/models/inception5h.zip
			//
			// - The model was trained with images scaled to 224x224 pixels.
			// - The colors, represented as R, G, B in 1-byte each were converted to
			// float using (value - Mean)/Scale.
			final int H = 224;
			final int W = 224;
			final float mean = 117f;
			final float scale = 1f;

			// Since the graph is being constructed once per execution here, we can use a
			// constant for the
			// input image. If the graph were to be re-used for multiple input images, a
			// placeholder would
			// have been more appropriate.
			final Output<String> input = b.constant("input", imageBytes);
			final Output<Float> output = b
					.div(b.sub(
							b.resizeBilinear(b.expandDims(b.cast(b.decodeJpeg(input, 3), Float.class),
									b.constant("make_batch", 0)), b.constant("size", new int[] { H, W })),
							b.constant("mean", mean)), b.constant("scale", scale));
			try (Session s = new Session(g)) {
				// Generally, there may be multiple output tensors, all of them must be closed
				// to prevent resource leaks.
				return s.runner().fetch(output.op().name()).run().get(0).expect(Float.class);
			}
		}
	}

	// @author Tensorflow
	private static float[] executeInceptionGraph(byte[] graphDef, Tensor<Float> image) {
		try (Graph g = new Graph()) {
			g.importGraphDef(graphDef);
			try (Session s = new Session(g);
					// Generally, there may be multiple output tensors, all of them must be closed
					// to prevent resource leaks.

					Tensor<Float> result = s.runner().feed("input", image).fetch("output").run().get(0)
							.expect(Float.class)) {
				final long[] rshape = result.shape();

				if (result.numDimensions() != 2 || rshape[0] != 1) {
					throw new RuntimeException(String.format(
							"Expected model to produce a [1 N] shaped tensor where N is the number of labels, instead it produced one with shape %s",
							Arrays.toString(rshape)));
				}
				int nlabels = (int) rshape[1];
				return result.copyTo(new float[1][nlabels])[0];

			}
		}
	}

	// @author Tensorflow
	private static int maxIndex(float[] probabilities) {
		int best = 0;
		for (int i = 1; i < probabilities.length; ++i) {
			if (probabilities[i] > probabilities[best]) {
				best = i;
			}
		}
		return best;
	}
	

	/**
	 * @author David Roy
	 * 
	 * @param probabilities
	 * @return List of top 4 matching labels
	 */
	private static List<Label> getTopLabels(float[] probabilities) {

		List<Label> topLabels = new ArrayList<Label>();

		// add the top 4 probabilities to topLabels
		while (topLabels.size() < 4) {
			int best = maxIndex(probabilities);
			if (labelsList.contains(labels.get(best))) {
				if (labels.get(best).equals("moving van") && (probabilities[best] * 100f) < 10) {
					//bypass
					//do not add "moving van" unless probability is greater than 10%
				} else {
					topLabels.add(new Label(labels.get(best), probabilities[best]));
				}
				
			}			
			// set the "best" probability to 0, so that the next best
			// item can be chosen next
			probabilities[best] = 0;
		}

		return topLabels;
	}

	// @author Tensorflow
	private static byte[] readAllBytesOrExit(Path path) {
		try {
			return Files.readAllBytes(path);
		} catch (IOException e) {
			System.err.println("Failed to read [" + path + "]: " + e.getMessage());
			System.exit(1);
		}
		return null;
	}

	// @author Tensorflow
	private static List<String> readAllLinesOrExit(Path path) {
		try {
			return Files.readAllLines(path, Charset.forName("UTF-8"));
		} catch (IOException e) {
			System.err.println("Failed to read [" + path + "]: " + e.getMessage());
			System.exit(0);
		}
		return null;
	}

	// @author Tensorflow
	// In the fullness of time, equivalents of the methods of this class should be
	// auto-generated from
	// the OpDefs linked into libtensorflow_jni.so. That would match what is done in
	// other languages
	// like Python, C++ and Go.
	static class GraphBuilder {
		GraphBuilder(Graph g) {
			this.g = g;
		}

		Output<Float> div(Output<Float> x, Output<Float> y) {
			return binaryOp("Div", x, y);
		}

		<T> Output<T> sub(Output<T> x, Output<T> y) {
			return binaryOp("Sub", x, y);
		}

		<T> Output<Float> resizeBilinear(Output<T> images, Output<Integer> size) {
			return binaryOp3("ResizeBilinear", images, size);
		}

		<T> Output<T> expandDims(Output<T> input, Output<Integer> dim) {
			return binaryOp3("ExpandDims", input, dim);
		}

		<T, U> Output<U> cast(Output<T> value, Class<U> type) {
			DataType dtype = DataType.fromClass(type);
			return g.opBuilder("Cast", "Cast").addInput(value).setAttr("DstT", dtype).build().<U>output(0);
		}

		Output<UInt8> decodeJpeg(Output<String> contents, long channels) {
			return g.opBuilder("DecodeJpeg", "DecodeJpeg").addInput(contents).setAttr("channels", channels).build()
					.<UInt8>output(0);
		}

		<T> Output<T> constant(String name, Object value, Class<T> type) {
			try (Tensor<T> t = Tensor.<T>create(value, type)) {
				return g.opBuilder("Const", name).setAttr("dtype", DataType.fromClass(type)).setAttr("value", t).build()
						.<T>output(0);
			}
		}

		Output<String> constant(String name, byte[] value) {
			return this.constant(name, value, String.class);
		}

		Output<Integer> constant(String name, int value) {
			return this.constant(name, value, Integer.class);
		}

		Output<Integer> constant(String name, int[] value) {
			return this.constant(name, value, Integer.class);
		}

		Output<Float> constant(String name, float value) {
			return this.constant(name, value, Float.class);
		}

		private <T> Output<T> binaryOp(String type, Output<T> in1, Output<T> in2) {
			return g.opBuilder(type, type).addInput(in1).addInput(in2).build().<T>output(0);
		}

		private <T, U, V> Output<T> binaryOp3(String type, Output<U> in1, Output<V> in2) {
			return g.opBuilder(type, type).addInput(in1).addInput(in2).build().<T>output(0);
		}

		private Graph g;
	}
}
