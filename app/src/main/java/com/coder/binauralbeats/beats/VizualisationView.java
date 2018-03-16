package com.coder.binauralbeats.beats;
public interface VizualisationView {

	public void startVisualization(Visualization v, float length);
	public void stopVisualization();
	public void setProgress(float pos);
	public void setFrequency(float freq) ;
	
}
