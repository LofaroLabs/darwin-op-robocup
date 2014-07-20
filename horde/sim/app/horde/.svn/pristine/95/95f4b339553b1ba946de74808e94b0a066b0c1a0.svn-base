#include "tracking.h" 

using namespace std;
using namespace cvb;
using namespace cv;

BlobTracking::BlobTracking(bool display, int h_thresh, int s_thresh,
		int v_thresh)
{
	capture = cvCreateCameraCapture(CV_CAP_ANY);

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

	still_tracking = true;

	hsvFrame = cvCreateImage(imgSize, 8, 3);
	planeH = cvCreateImage(imgSize, 8, 1);
	planeS = cvCreateImage(imgSize, 8, 1);
	planeV = cvCreateImage(imgSize, 8, 1);

	h_threshold = h_thresh;
	s_threshold = s_thresh;
	v_threshold = v_thresh;

	if (display) {
		cvNamedWindow(ORIGINAL_FRAME);
	}

	maxBlob = new CvBlob();

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

void BlobTracking::updateMaxBlob()
{
	int maxArea = 0;

	maxBlob->minx = -1;
	maxBlob->maxx = -1;
	maxBlob->miny = -1;
	maxBlob->maxy = -1;
	maxBlob->area = 0;
	maxBlob->centroid = cvPoint2D64f(0.0, 0.0);

	for (CvBlobs::const_iterator it = blobs.begin(); it != blobs.end(); ++it) {
		CvBlob *blob = (*it).second;
		if (blob && blob->area > maxArea) {
			maxArea = blob->area;
			*maxBlob = *blob;
			//maxBlob->minx = blob->minx;
		}
	}
}

void BlobTracking::display()
{
	cvShowImage(ORIGINAL_FRAME, frame);
}

void BlobTracking::update()
{

	frame = cvQueryFrame(capture);

	if (frame) {
		segmented = cvCreateImage(imgSize, 8, 1);

		cvCvtColor(frame, hsvFrame, CV_BGR2HSV);
		cvCvtPixToPlane(hsvFrame, planeH, planeS, planeV, 0);

		// colors are for pink paper
		cvThreshold(planeH, planeH, h_threshold, 255, CV_THRESH_BINARY);
		cvThreshold(planeS, planeS, s_threshold, 255, CV_THRESH_BINARY);
		cvThreshold(planeV, planeV, v_threshold, 255, CV_THRESH_BINARY);

		cvAnd(planeH, planeS, segmented);
		cvAnd(planeV, segmented, segmented);

		//cvShowImage("Hue", planeH);
		//cvShowImage("Sat", planeS);
		//cvShowImage("V", planeV);
		//cvShowImage("Combined", segmented);

		cvMorphologyEx(segmented, segmented, NULL, morphKernel, CV_MOP_OPEN, 1);
		IplImage *labelImg =
				cvCreateImage(cvGetSize(frame), IPL_DEPTH_LABEL, 1);
		unsigned int result = cvLabel(segmented, labelImg, blobs);
		cvFilterByArea(blobs, 500, 1000000);

		updateMaxBlob();
		cvReleaseBlobs(blobs);

		if (maxBlob->area > 0) {
			cvRectangle(frame, cvPoint(maxBlob->minx, maxBlob->miny), cvPoint(
					maxBlob->maxx, maxBlob->maxy), cvScalar(0, 0, 255, 0), 1, 0,
					0);
		}

		cvReleaseImage(&labelImg);
		cvReleaseImage(&segmented);

		still_tracking = true;
	}
	else
		still_tracking = false;
}

bool BlobTracking::isVisible()
{
	return still_tracking;
}
