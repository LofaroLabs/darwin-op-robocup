#ifndef __TRACKING_H__
#define __TRACKING_H__

#include <iostream>
#include <cv.h>
#include <highgui.h>
#include <cvblob.h>
#include <math.h>

#define CAM_NUMBER 2

#define ORIGINAL_FRAME "Original Image"
#define TRACKING_FRAME "Segmented Image"

#define DEFAULT_H 141
#define DEFAULT_S 100
#define DEFAULT_V 225

class BlobTracking {

public: 
	BlobTracking(bool display=true, int h_thresh=DEFAULT_H, int s_thresh=DEFAULT_S, int v_thresh=DEFAULT_V);
	~BlobTracking(); 
	int getBiggestBlob() {
		return maxBlob->area;
	}
	int getXCoord() {
		CvPoint2D64f center = cvCentroid(maxBlob);
		float x = center.x; 
		if (x == NAN || x==INFINITY) x = 0;
		
		return (int)x; 
	}
	
	int getYCoord() {
		CvPoint2D64f center =cvCentroid(maxBlob);
		float y = center.y; 
		if (y == NAN || y==INFINITY) y = 0;
		return (int)y; 
	}

	int getXmin() { return maxBlob->minx; }
	int getXmax() { return maxBlob->maxx; }
	int getYmin() { return maxBlob->miny; }
	int getYmax() { return maxBlob->maxy; }

	void display(); 
	void update(); 
	bool isVisible(); 
	
	
	
private: 
	cvb::CvBlob* maxBlob;
	cvb::CvBlobs blobs; 
	CvCapture *capture; 
	IplConvKernel *morphKernel; 
	CvSize imgSize; 
	IplImage *frame, *segmented; 
	void updateMaxBlob(); 
	
	bool still_tracking; 
	int step, channels, monoStep, monoChannels;
	IplImage *hsvFrame, *planeH, *planeS, *planeV; 
	int h_threshold, s_threshold, v_threshold;
};

#endif

