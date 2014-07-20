

#include <iostream> 
#include <opencv/cv.h>
#include <opencv/highgui.h>
#include "Blob.h"
#include "BlobResult.h"

using namespace std; 

int main (int argc, char **argv)
{
  const int LOWER = 5; 
  const int UPPER = 178; 
  const int THRESHOLD = 210; 
	
  const char *WINDOW_NAME = "Camera"; 
  const char *TRACKING_NAME = "Tracking"; 
	
  CvCapture *camera =  cvCreateCameraCapture(CV_CAP_ANY);

  if (!camera) { 
    cout << "ERROR creating camera" << endl;  
    exit(-1); 
  }

  cvNamedWindow(WINDOW_NAME);

  int width = 100; 
  int height = 100; 
  IplImage *template_image = cvCreateImage(cvSize(width, height), IPL_DEPTH_8U,3); 
  
  char *data = template_image->imageData; 
  for (int i=0; i < width; i++) { 
    for (int j=0; j < height; j++) {
      int idx = i*template_image->widthStep + j*template_image->nChannels;
      
      if (i > width/2) {  
	data[idx + 0] = 255; 
	data[idx + 1] = 0; 
	data[idx + 2] = 0; 
      }
      else {  
	data[idx + 0] = 0; 
	data[idx + 1] = 255; 
	data[idx + 2] = 255;
      }
    }
  }

  double m, M; 
  CvPoint point1, point2; 
   while (1) { 
    IplImage *current_frame = cvQueryFrame(camera); 
    IplImage *thresh_image; 
    if (!current_frame) 
      cout << "NULL frame " << endl; 
    else { 
      CBlobResult blobs = CBlobResult(current_frame, NULL, 100, true); 
      
      //cvMerge(thresh_image, thresh_image, thresh_image, NULL, current_frame);

      for (int i=0; i < blobs.GetNumBlobs(); i++) { 
	blobs.GetBlob(i)->FillBlob(current_frame, CV_RGB(255,0,0));
      }

      cvShowImage(WINDOW_NAME, current_frame); 

    }
    /* if (!current_frame) 
      cout << "NULL frame" << endl;
    else { 
      int resultW = current_frame->width - template_image->width + 1; 
      int resultH = current_frame->height - template_image->height + 1; 
      IplImage* result = cvCreateImage(cvSize(resultW, resultH), IPL_DEPTH_32F, 1); 
      cvMatchTemplate(current_frame, template_image, result, CV_TM_SQDIFF);

      cvMinMaxLoc(result, &m, &M, &point2, &point1, NULL); 
      cvRectangle( current_frame, point2,
		   cvPoint( point2.x + template_image->width, point2.y + template_image->height ), 
		   cvScalar( 0, 0, 255, 0 ), 1, 0, 0 );
      

      cvShowImage(WINDOW_NAME, current_frame);
    }
    */
    if( (cvWaitKey(10) & 255) == 27 ) break;  
  }    // Release the capture device housekeeping   
  
  cvReleaseCapture( &camera );   
  cvDestroyWindow( WINDOW_NAME );
  
	
  return 0;
}


/*
#include "opencv/cv.h" 
#include "opencv/highgui.h" 
#include <iostream>

using namespace std; 
  
// A Simple Camera Capture Framework 
int main() {    
  CvCapture* capture = cvCaptureFromCAM( CV_CAP_ANY );   
  if( !capture ) {     
    cout << "ERROR: capture is NULL" << endl;      
    //getchar();     
    return -1;   }    
  // Create a window in which the captured images will be presented   
  cvNamedWindow( "mywindow", CV_WINDOW_AUTOSIZE );    

  // Show the image captured from the camera in the window and repeat   
  while( 1 ) {     // Get one frame     
    IplImage* frame = cvQueryFrame( capture );     
    if( !frame ) {       //fprintf( stderr, "ERROR: frame is null...\n" );       getchar();       
      break;     }      
    cvShowImage( "mywindow", frame );     // Do not release the frame!      
    //If ESC key pressed, Key=0x10001B under OpenCV 0.9.7(linux version),     //remove higher bits using AND operator     
    if( (cvWaitKey(10) & 255) == 27 ) break;  
  }    // Release the capture device housekeeping   
  cvReleaseCapture( &capture );   
  cvDestroyWindow( "mywindow" );   
  return 0; }        
*/
