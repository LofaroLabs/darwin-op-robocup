#include "laser.h"
#include <iostream>
#include <cv.h>
#include <highgui.h>

using namespace std;
//using namespace cvb;
using namespace cv;

#define ORIGINAL_FRAME "Laser Visualizer"

void MyLine(Mat img, Point start, Point end, Scalar scalar)
{
	int thickness = 2;
	int lineType = 8;
	line(img, start, end, scalar, thickness, lineType);
}

int main(int argc, char **argv)
{

	char serial[256];
	int baud = 115200;

	// parse command line inputs
	for (int i = 0; i < argc; i++) {
		if (!strcmp("-laser", argv[i])) {
			strcpy(serial, argv[i + 1]);
			i++;
		}
		else if (!strcmp( "-baud",argv[i])) {
			baud = atoi(argv[i + 1]);
			i++;
		}
	}

	cout << "Laser: " << serial << endl;
	cout << "Baud: " << baud << endl;

	Laser laser(serial, baud);

	cvNamedWindow(ORIGINAL_FRAME);
	int width = 640;
	int height = 640;
	int center_x = width / 2;
	int center_y = height / 2;
	Point center_point(center_x, center_y);

	double scale = min(width, height)/10000.0;

	int max_range = 4095;

	while (1) {
		Mat mat = Mat::zeros(height, width, CV_8UC3);
		for (int i = -120; i <= 120; i++) {
			double dist = laser.getRange(i);
			if (dist < 20 || dist > max_range)	dist = max_range;

			// rotate counter-clockwise for drawing purposes (this makes
			// 0 degrees point up )
			double angle = (i - 90) * M_PI/180;

			double x = center_x + scale*cos(angle) * dist;
			double y = center_y + scale*sin(angle) * dist;
			MyLine(mat, center_point, Point(x, y), Scalar(255,0,0));
		}

		laser.computeLines();
		int numLines = laser.lines.size();
		for (int i=0; i < numLines; i++)
		{
			Line l = laser.lines[i];
			l.scale(scale);
			l.shift(center_x, center_y);
			MyLine(mat, Point(l.start.x, l.start.y), Point(l.end.x, l.end.y), Scalar(0,0,255));
		}

		cout << "Angle: " << laser.getAngleLongestLine() * 180 / M_PI << endl;

		cvMoveWindow(ORIGINAL_FRAME, 50, 50);
		imshow(ORIGINAL_FRAME, mat);

		char c = cvWaitKey(100);
		if (c == 27)
			break;
	}

	return 1;
}
