package main.java;

/**
 * 
 * @author David Roy
 * Used to manage labels in 
 * the image classifier (LabelImage)
 *
 */
public class Label {
	private String label;
	private float probability;
	
	public Label(String label, float probability) {
		this.label = label;
		this.probability = probability;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public float getProbability() {
		return this.probability;
	}
}
