package com.torusworks.android.visualizers;

import com.torusworks.skroller.model.TorusVisualizer;

import android.media.audiofx.Visualizer;

public class AudioOutVisualizer implements TorusVisualizer {

	private Visualizer mVisualizer;	
	private int[] formattedVizData;
	private byte[] vizBuffer;
	
	public AudioOutVisualizer(){
		mVisualizer = new Visualizer(0);
		int captureSize = Visualizer.getCaptureSizeRange()[1];
		mVisualizer.setCaptureSize(captureSize);
		mVisualizer.setEnabled(true);
		formattedVizData = new int[captureSize];
		vizBuffer = new byte[captureSize];

		
	}
	
	protected int[] getFormattedData(byte[] rawVizData) {
		for (int i = 0; i < formattedVizData.length; i++) {
			// convert from unsigned 8 bit to signed 16 bit
			int tmp = ((int) rawVizData[i] & 0xFF) - 128;
			formattedVizData[i] = tmp;
		}
		return formattedVizData;
	}	
	
	/* (non-Javadoc)
	 * @see com.torusworks.skroller.model.TorusVisualizer#getRms()
	 */
	@Override
	public double getRms() {
		double rms = 0.1f;
		try {
			// capture sound
			mVisualizer.getWaveForm(vizBuffer);
	
			getFormattedData(vizBuffer);
			if (formattedVizData.length > 0) {
				for (int i = 0; i < formattedVizData.length; i++) {
					int val = formattedVizData[i];
					rms += val * val;
				}
				rms = Math.sqrt(rms / formattedVizData.length);
			}
		} catch (Exception e) {
			
		}
		return rms;
		
	}
	
	/* (non-Javadoc)
	 * @see com.torusworks.skroller.model.TorusVisualizer#release()
	 */
	@Override
	public void release() {
		mVisualizer.release();
	}
	
}
