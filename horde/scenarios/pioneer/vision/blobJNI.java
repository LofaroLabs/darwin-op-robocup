package sim.app.horde.scenarios.pioneer.vision;

/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.0
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */


public class blobJNI {
    public final static native int CAM_NUMBER_get();
    public final static native String ORIGINAL_FRAME_get();
    public final static native String TRACKING_FRAME_get();
    public final static native long new_BlobTracking__SWIG_0(boolean jarg1);
    public final static native long new_BlobTracking__SWIG_1();
    public final static native void delete_BlobTracking(long jarg1);
    public final static native int BlobTracking_getBiggestBlob(long jarg1, BlobTracking jarg1_);
    public final static native void BlobTracking_display(long jarg1, BlobTracking jarg1_);
    public final static native void BlobTracking_update(long jarg1, BlobTracking jarg1_);
    public final static native boolean BlobTracking_isVisible(long jarg1, BlobTracking jarg1_);
    }
