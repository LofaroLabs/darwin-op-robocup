#include "tracking.h" 

#include <stdio.h> 
#include <stdlib.h> 

using namespace std;
using namespace cvb;
using namespace cv;

BlobTracking::BlobTracking(bool display)
{
	capture = cvCreateCameraCapture(0);

	if (!cvGrabFrame(capture)) {
		cout << "Error trying to capture image" << endl;
		cout << "Exiting" << endl;
		exit(-1);
	}

	IplImage *img = cvRetrieveFrame(capture);

	if (!img) {
		cout << "NULL image from cvRetrieveFrame " << endl;
		cout << "Exiting" << endl;
		exit(-1);
	}

	imgSize = cvGetSize(img);

	frame = cvCreateImage(imgSize, img->depth, img->nChannels);

	morphKernel = cvCreateStructuringElementEx(5, 5, 1, 1, CV_SHAPE_RECT, NULL);

	step = img->widthStep / sizeof(unsigned char);
	channels = img->nChannels;

	hsvFrame = cvCreateImage(imgSize, 8, 3);
	planeH = cvCreateImage(imgSize, 8, 1);
  	planeS = cvCreateImage(imgSize, 8, 1);
  	planeV = cvCreateImage(imgSize, 8, 1);

	if (display) {
		cout << "Tracking: Starting display" << endl; 
		cvNamedWindow(ORIGINAL_FRAME);
		cvMoveWindow(ORIGINAL_FRAME, 50, 50);
	}
}

BlobTracking::~BlobTracking()
{
	try {
		cvReleaseStructuringElement(&morphKernel);
		cvReleaseCapture(&capture);

		cvDestroyWindow(ORIGINAL_FRAME);
		cvDestroyWindow(TRACKING_FRAME);
	} catch (cv::Exception &e) {
		cout << "Error in ~BlobTracking: " << e.what() << endl;
	}
}

string BlobTracking::getData()
{
	char buffer[256];
	sprintf(buffer, "%d:%d:%d:%d:%d:%d:%d:%d:%d:%d:%d:%d:%d:%d:%d:%d", maxPinkBlob.minx, maxPinkBlob.miny, maxPinkBlob.maxx, maxPinkBlob.maxy,
			maxGreenBlob.minx, maxGreenBlob.miny, maxGreenBlob.maxx, maxGreenBlob.maxy, maxOrangeBlob.minx, maxOrangeBlob.miny, maxOrangeBlob.maxx,
			maxOrangeBlob.maxy, maxCyanBlob.minx, maxCyanBlob.miny, maxCyanBlob.maxx, maxCyanBlob.maxy

	);
	string s(buffer);
	return s;
}

void BlobTracking::display()
{
	cvShowImage(ORIGINAL_FRAME, frame);
}

void BlobTracking::update()
{
	frame = cvQueryFrame(capture);

	if (frame) {
		trackPink();
		trackGreen();
		trackOrange();
		trackCyan();
		still_tracking = true;
	}
	else
		still_tracking = false;
}

bool BlobTracking::isVisible()
{
	return still_tracking;
}

void BlobTracking::trackOrange()
{
	segmented = cvCreateImage(imgSize, 8, 1);

	cvCvtColor(frame, hsvFrame, CV_BGR2HSV);
	cvCvtPixToPlane(hsvFrame, planeH, planeS, planeV, 0);

	IplImage* upper = cvCreateImage(imgSize, 8, 1);
	IplImage* lower = cvCreateImage(imgSize, 8, 1);

	cvThreshold(planeH, upper, ORANGE_H, 255, CV_THRESH_BINARY);
	cvThreshold(planeH, lower, ORANGE_H + 50, 255, CV_THRESH_BINARY_INV);
	cvAnd(upper, lower, planeH);

	cvThreshold(planeS, upper, ORANGE_S, 255, CV_THRESH_BINARY);
	cvThreshold(planeS, lower, ORANGE_S + 50, 255, CV_THRESH_BINARY_INV);
	cvAnd(upper, lower, planeS);

	cvThreshold(planeV, planeV, ORANGE_V, 255, CV_THRESH_BINARY);
	//cvThreshold(planeV, lower, v_threshold+75, 255, CV_THRESH_BINARY_INV);
	//cvAnd(upper, lower, planeV);

	cvReleaseImage(&upper);
	cvReleaseImage(&lower);

	cvAnd(planeH, planeS, segmented);
	cvAnd(planeV, segmented, segmented);

	//cvShowImage("H", planeH);
	//cvShowImage("S", planeS);
	//cvShowImage("V", planeV);
	//cvShowImage("comb", segmented);

	cvMorphologyEx(segmented, segmented, NULL, morphKernel, CV_MOP_OPEN, 1);
	IplImage *labelImg = cvCreateImage(cvGetSize(frame), IPL_DEPTH_LABEL, 1);
	 cvLabel(segmented, labelImg, blobs);
	cvFilterByArea(blobs, 100, 1000000);

	int maxArea = 0;

	maxOrangeBlob.minx = -1;
	maxOrangeBlob.maxx = -1;
	maxOrangeBlob.miny = -1;
	maxOrangeBlob.maxy = -1;
	maxOrangeBlob.area = 0;
	maxOrangeBlob.centroid = cvPoint2D64f(0.0, 0.0);

	for (CvBlobs::const_iterator it = blobs.begin(); it != blobs.end(); ++it) {
		CvBlob *blob = (*it).second;
		if ((int)blob->area > maxArea) {
			maxArea = blob->area;
			maxOrangeBlob = *blob;
		}
	}

	cvReleaseBlobs(blobs);

	if (maxOrangeBlob.area > 0) {
		cvRectangle(frame, cvPoint(maxOrangeBlob.minx, maxOrangeBlob.miny), cvPoint(maxOrangeBlob.maxx, maxOrangeBlob.maxy), cvScalar(0, 0, 0, 0), 1,
				0, 0);
	}

	cvReleaseImage(&labelImg);
	cvReleaseImage(&segmented);
}

void BlobTracking::trackCyan()
{
	segmented = cvCreateImage(imgSize, 8, 1);

	cvCvtColor(frame, hsvFrame, CV_BGR2HSV);
	cvCvtPixToPlane(hsvFrame, planeH, planeS, planeV, 0);

	IplImage* upper = cvCreateImage(imgSize, 8, 1);
	IplImage* lower = cvCreateImage(imgSize, 8, 1);

	cvThreshold(planeH, upper, CYAN_H, 255, CV_THRESH_BINARY);
	cvThreshold(planeH, lower, CYAN_H + 30, 255, CV_THRESH_BINARY_INV);
	cvAnd(upper, lower, planeH);

	cvThreshold(planeS, upper, CYAN_S, 255, CV_THRESH_BINARY);
	cvThreshold(planeS, lower, CYAN_S + 50, 255, CV_THRESH_BINARY_INV);
	cvAnd(upper, lower, planeS);

	cvThreshold(planeV, planeV, CYAN_V, 255, CV_THRESH_BINARY);
	//cvThreshold(planeV, lower, v_threshold+75, 255, CV_THRESH_BINARY_INV);
	//cvAnd(upper, lower, planeV);

	cvReleaseImage(&upper);
	cvReleaseImage(&lower);

	cvAnd(planeH, planeS, segmented);
	cvAnd(planeV, segmented, segmented);

	//cvShowImage("H", planeH);
	//cvShowImage("S", planeS);
	//cvShowImage("V", planeV);
	//cvShowImage("comb", segmented);

	cvMorphologyEx(segmented, segmented, NULL, morphKernel, CV_MOP_OPEN, 1);
	IplImage *labelImg = cvCreateImage(cvGetSize(frame), IPL_DEPTH_LABEL, 1);
	 cvLabel(segmented, labelImg, blobs);
	cvFilterByArea(blobs, 100, 1000000);

	int maxArea = 0;

	maxCyanBlob.minx = -1;
	maxCyanBlob.maxx = -1;
	maxCyanBlob.miny = -1;
	maxCyanBlob.maxy = -1;
	maxCyanBlob.area = 0;
	maxCyanBlob.centroid = cvPoint2D64f(0.0, 0.0);

	for (CvBlobs::const_iterator it = blobs.begin(); it != blobs.end(); ++it) {
		CvBlob *blob = (*it).second;
		if ((int)blob->area > maxArea) {
			maxArea = blob->area;
			maxCyanBlob = *blob;
		}
	}

	cvReleaseBlobs(blobs);

	if (maxOrangeBlob.area > 0) {
		cvRectangle(frame, cvPoint(maxCyanBlob.minx, maxCyanBlob.miny), cvPoint(maxCyanBlob.maxx, maxCyanBlob.maxy), cvScalar(255, 0, 0, 0), 1, 0, 0);
	}

	cvReleaseImage(&labelImg);
	cvReleaseImage(&segmented);
}

void BlobTracking::trackPink()
{
	segmented = cvCreateImage(imgSize, 8, 1);

	cvCvtColor(frame, hsvFrame, CV_BGR2HSV);
	cvCvtPixToPlane(hsvFrame, planeH, planeS, planeV, 0);

	cvThreshold(planeH, planeH, PINK_H, 255, CV_THRESH_BINARY);
	cvThreshold(planeS, planeS, PINK_S, 255, CV_THRESH_BINARY);
	cvThreshold(planeV, planeV, PINK_V, 255, CV_THRESH_BINARY);

	cvAnd(planeH, planeS, segmented);
	cvAnd(planeV, segmented, segmented);

	//cvShowImage("H", planeH);
	//cvShowImage("S", planeS);
	//cvShowImage("V", planeV);
	//cvShowImage("comb", segmented);

	cvMorphologyEx(segmented, segmented, NULL, morphKernel, CV_MOP_OPEN, 1);
	IplImage *labelImg = cvCreateImage(cvGetSize(frame), IPL_DEPTH_LABEL, 1);
	 cvLabel(segmented, labelImg, blobs);
	cvFilterByArea(blobs, 100, 1000000);

	int maxArea = 0;

	maxPinkBlob.minx = -1;
	maxPinkBlob.maxx = -1;
	maxPinkBlob.miny = -1;
	maxPinkBlob.maxy = -1;
	maxPinkBlob.area = 0;
	maxPinkBlob.centroid = cvPoint2D64f(0.0, 0.0);

	for (CvBlobs::const_iterator it = blobs.begin(); it != blobs.end(); ++it) {
		CvBlob *blob = (*it).second;
		if ((int)blob->area > maxArea) {
			maxArea = blob->area;
			maxPinkBlob = *blob;
		}
	}

	cvReleaseBlobs(blobs);

	if (maxPinkBlob.area > 0) {
		cvRectangle(frame, cvPoint(maxPinkBlob.minx, maxPinkBlob.miny), cvPoint(maxPinkBlob.maxx, maxPinkBlob.maxy), cvScalar(0, 0, 255, 0), 1, 0, 0);
	}

	cvReleaseImage(&labelImg);
	cvReleaseImage(&segmented);
}

void BlobTracking::trackGreen()
{
	segmented = cvCreateImage(imgSize, 8, 1);

	cvCvtColor(frame, hsvFrame, CV_BGR2HSV);
	cvCvtPixToPlane(hsvFrame, planeH, planeS, planeV, 0);

	IplImage* upper = cvCreateImage(imgSize, 8, 1);
	IplImage* lower = cvCreateImage(imgSize, 8, 1);

	cvThreshold(planeH, upper, GREEN_H, 255, CV_THRESH_BINARY);
	cvThreshold(planeH, lower, GREEN_H + 30, 255, CV_THRESH_BINARY_INV);
	cvAnd(upper, lower, planeH);

	cvThreshold(planeS, upper, GREEN_S, 255, CV_THRESH_BINARY);
	cvThreshold(planeS, lower, GREEN_S + 50, 255, CV_THRESH_BINARY_INV);
	cvAnd(upper, lower, planeS);

	cvThreshold(planeV, planeV, GREEN_V, 255, CV_THRESH_BINARY);
	//cvThreshold(planeV, lower, v_threshold+75, 255, CV_THRESH_BINARY_INV);
	//cvAnd(upper, lower, planeV);

	cvReleaseImage(&upper);
	cvReleaseImage(&lower);

	cvAnd(planeH, planeS, segmented);
	cvAnd(planeV, segmented, segmented);

	//cvShowImage("H", planeH);
	//cvShowImage("S", planeS);
	//cvShowImage("V", planeV);
	//cvShowImage("comb", segmented);

	cvMorphologyEx(segmented, segmented, NULL, morphKernel, CV_MOP_OPEN, 1);
	IplImage *labelImg = cvCreateImage(cvGetSize(frame), IPL_DEPTH_LABEL, 1);
	cvLabel(segmented, labelImg, blobs);
	cvFilterByArea(blobs, 100, 1000000);

	int maxArea = 0;

	maxGreenBlob.minx = -1;
	maxGreenBlob.maxx = -1;
	maxGreenBlob.miny = -1;
	maxGreenBlob.maxy = -1;
	maxGreenBlob.area = 0;
	maxGreenBlob.centroid = cvPoint2D64f(0.0, 0.0);

	for (CvBlobs::const_iterator it = blobs.begin(); it != blobs.end(); ++it) {
		CvBlob *blob = (*it).second;
		if ((int)blob->area > maxArea) {
			maxArea = blob->area;
			maxGreenBlob = *blob;
		}
	}

	cvReleaseBlobs(blobs);

	if (maxGreenBlob.area > 0) {
		cvRectangle(frame, cvPoint(maxGreenBlob.minx, maxGreenBlob.miny), cvPoint(maxGreenBlob.maxx, maxGreenBlob.maxy), cvScalar(0, 255, 0, 0), 1,
				0, 0);
	}

	cvReleaseImage(&labelImg);
	cvReleaseImage(&segmented);
}

