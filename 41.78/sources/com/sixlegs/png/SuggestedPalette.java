package com.sixlegs.png;


public interface SuggestedPalette {

	String getName();

	int getSampleCount();

	int getSampleDepth();

	void getSample(int int1, short[] shortArray);

	int getFrequency(int int1);
}
