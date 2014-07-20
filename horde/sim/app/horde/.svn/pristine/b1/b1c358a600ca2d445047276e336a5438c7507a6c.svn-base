#ifndef __TRACKING_H__
#define __TRACKING_H__

#include <iostream>
#include <cv.h>
#include <highgui.h>
#include <cvblob.h>
#include <math.h>
#include <string>

#define CAM_NUMBER 2

using namespace cv; 


#define ORIGINAL_FRAME "Original Image"
#define TRACKING_FRAME "Segmented Image"

#define PINK_H 141
#define PINK_S 100
#define PINK_V 100


#define GREEN_H 50
#define GREEN_S 100
#define GREEN_V 75


#define ORANGE_H 0
#define ORANGE_S 200
#define ORANGE_V 150


#define CYAN_H 75
#define CYAN_S 150
#define CYAN_V 160

class BlobTracking
{

public:
	BlobTracking(bool display = true);
	~BlobTracking();

	std::string getData();

	void display();
	void update();
	bool isVisible();

private:
	cvb::CvBlob maxPinkBlob, maxGreenBlob, maxOrangeBlob, maxCyanBlob;
	cvb::CvBlobs blobs;
	CvCapture *capture;
	IplConvKernel *morphKernel;
	CvSize imgSize;
	IplImage *frame, *segmented;
	void updateMaxBlob();
	void trackGreen();
	void trackOrange();
	void trackCyan();
	void trackPink();

	bool still_tracking;
	int step, channels, monoStep, monoChannels;
	IplImage *hsvFrame, *planeH, *planeS, *planeV;

};

#endif

