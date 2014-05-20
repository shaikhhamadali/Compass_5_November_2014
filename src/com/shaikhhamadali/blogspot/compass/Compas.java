package com.shaikhhamadali.blogspot.compass;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
 
public class Compas extends Activity implements SensorEventListener {
	//declare Variables
  Float azimut;  
  CustomDrawableView mCustomDrawableView;// View to draw a compass
//SensorManager lets you access the device's sensors
  private SensorManager mSensorManager;
  Sensor accelerometer;
  Sensor magnetometer;
 
  public class CustomDrawableView extends View {
    Paint paint = new Paint();
    public CustomDrawableView(Context context) {
      super(context);
      //color
      paint.setColor(0xff00ff00);
      //style 
      paint.setStyle(Style.STROKE);
      //stroke width
      paint.setStrokeWidth(2);
      //antiAlias
      paint.setAntiAlias(true);
      //text size
      paint.setTextSize(30);
    };
 
    protected void onDraw(Canvas canvas) {
    	//declare Local Variables    	
      int width = getWidth();
      int height = getHeight();
      int centerx = width/2;
      int centery = height/2;
      // Rotate the canvas with the azimut      
      if (azimut != null)
      //Preconcat the current matrix with the specified rotation.
      canvas.rotate(-azimut*360/(2*3.14159f), centerx, centery);
      //set color
      paint.setColor(0xff0000ff);
      //draw two lines
      canvas.drawLine(centerx, -1000, centerx, +1000, paint);
      canvas.drawLine(-1000, centery, 1000, centery, paint);
      //E,W,N,S directions
      canvas.drawText("N", centerx+15, centery-220, paint);
      canvas.drawText("S", centerx-30, centery+225, paint);
      canvas.drawText("E", centerx+215, centery-20, paint);
      canvas.drawText("W", centerx-220, centery+35, paint);
    }
  }
 
 
 
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mCustomDrawableView = new CustomDrawableView(this);
    setContentView(mCustomDrawableView);    
   //create instance of sensor manager and get system service to interact with Sensor
    
    mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
      accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
  }
 
  protected void onResume() {
    super.onResume();
    // Register the sensor listeners
    mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
    mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
  }
 
  protected void onPause() {
    super.onPause();
    // unRegister the sensor listeners
    mSensorManager.unregisterListener(this);
  }
 
  public void onAccuracyChanged(Sensor sensor, int accuracy) {  }
 
  float[] mGravity;
  float[] mGeomagnetic;
  public void onSensorChanged(SensorEvent event) {
    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
      mGravity = event.values;
    if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
      mGeomagnetic = event.values;
    if (mGravity != null && mGeomagnetic != null) {
      float R[] = new float[9];
      float I[] = new float[9];
      /*Computes the inclination matrix I as well as the rotation matrix R transforming a vector from the device coordinate
       *  system to the world's coordinate system which is defined as a direct orthonormal basis*/
      boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
      if (success) {
        float orientation[] = new float[3];
        /*Computes the device's orientation based on the rotation matrix*/
        SensorManager.getOrientation(R, orientation);
        azimut = orientation[0]; // orientation contains: azimut, pitch and roll
      }
    }
    mCustomDrawableView.invalidate();
  }
}