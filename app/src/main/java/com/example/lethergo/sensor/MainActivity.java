package com.example.lethergo.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    LineGraphSeries<DataPoint> seriesX;
    LineGraphSeries<DataPoint> seriesY;
    LineGraphSeries<DataPoint> seriesZ;
    private List<DataPoint> listX;
    private List<DataPoint> listY;
    private List<DataPoint> listZ;
    GraphView graph;
    private SensorManager sensorManager;
    private Sensor sensor;
    private final Handler mHandler = new Handler();
    private Runnable mTimer1;
    int dataCount = 1;
    int cnt =1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        assert this.sensorManager != null;
        this.sensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        seriesX = new LineGraphSeries<DataPoint>();
        seriesY = new LineGraphSeries<DataPoint>();
        seriesZ = new LineGraphSeries<DataPoint>();
        listX = new ArrayList<DataPoint>();
        listY = new ArrayList<DataPoint>();
        listZ = new ArrayList<DataPoint>();
        graph = (GraphView) findViewById(R.id.graph);
        graph.addSeries(seriesX);
        graph.addSeries(seriesY);
        graph.addSeries(seriesZ);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(40);

    }
    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {


    }

    @Override
    public final void onSensorChanged(SensorEvent event){

        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
        {
            return;
        }
        seriesX.appendData(new DataPoint(dataCount,event.values[0]), true,100);
        seriesY.appendData(new DataPoint(dataCount,event.values[1]), true,100);
        seriesZ.appendData(new DataPoint(dataCount,event.values[2]), true,100);
        listX.add(new DataPoint(dataCount,event.values[0]));
        listY.add(new DataPoint(dataCount,event.values[1]));
        listZ.add(new DataPoint(dataCount,event.values[2]));
        ++dataCount;
//        if(dataCount>100){
//            listX.remove(0);
//            listY.remove(0);
//            listZ.remove(0);
//            graph.getViewport().setMinX(dataCount-100);
//            graph.getViewport().setMaxX(100);
//        }



    }
    @Override
    protected void onStop()
    {
        //unregister the sensor listener
        sensorManager.unregisterListener(this);
        super.onStop();
    }
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        mTimer1 = new Runnable() {
            @Override
            public void run() {
                DataPoint[] dmmX = new DataPoint[listX.size()];
                DataPoint[] dmmY = new DataPoint[listY.size()];
                DataPoint[] dmmZ = new DataPoint[listZ.size()];
                listX.toArray(dmmX);
                listY.toArray(dmmY);
                listZ.toArray(dmmZ);
                seriesX.resetData(dmmX);
                seriesY.resetData(dmmY);
                seriesZ.resetData(dmmZ);

                mHandler.postDelayed(this,200);
            }
        };
        mHandler.postDelayed(mTimer1, 200);
    }

    @Override
    protected void onPause() {
        mHandler.removeCallbacks(mTimer1);
        super.onPause();
    }


}
