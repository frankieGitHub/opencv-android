//参考https://www.cnblogs.com/yunfang/p/6149831.html

package coolpad.myopencv;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity {

    Button btnProcess;
    Button btnBrightness;
    Bitmap srcBitmap;
    Bitmap grayBitmap;
    ImageView imgHuaishi;

    private double sum_of_gray_pixels = 0;
    private double avg_of_gray_pixels = 0;
    private int calc_gray_pixel[] = new int[256];
    private static boolean flag = true;
    //private static boolean isFirst = true;
    private static final String TAG = "MainActivity";


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            // TODO Auto-generated method stub
            switch (status){
                case BaseLoaderCallback.SUCCESS:
                    Log.i(TAG, "成功加载");
                    break;
                default:
                    super.onManagerConnected(status);
                    Log.i(TAG, "加载失败");
                    break;
            }

        }
    };


    private class BrightnessClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Mat rgbMat = new Mat();
                    Mat grayMat = new Mat();

                    srcBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.clear_220_220_new_dark);
                    grayBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Bitmap.Config.RGB_565);
                    Utils.bitmapToMat(srcBitmap, rgbMat);//convert original bitmap to Mat, R G B.

                    Imgproc.cvtColor(rgbMat, grayMat, Imgproc.COLOR_RGB2GRAY);//rgbMat to gray grayMat

                    Utils.matToBitmap(grayMat, grayBitmap); //convert mat to bitmap


                    for (int i = 0; i < 256; i++) calc_gray_pixel[i] = 0;

                    int m_Height = srcBitmap.getHeight();
                    int m_Width = srcBitmap.getWidth();

                    Log.i(TAG, "m_Height is" + m_Height);
                    Log.i(TAG, "m_Width is" + m_Width);

                    for (int kk = 0; kk < m_Height; kk++) {
                        for (int aa = 0; aa < m_Width; aa++) {
                            double gray_pixel_value[] = grayMat.get(aa, kk);
                            sum_of_gray_pixels += (gray_pixel_value[0] - 128);

                            int x = (int) gray_pixel_value[0];
                            calc_gray_pixel[x]++;
                        }
                    }

                    Log.i(TAG, "ooooookkkkkk1111");

                    avg_of_gray_pixels = sum_of_gray_pixels / (grayBitmap.getHeight() * grayBitmap.getWidth());

                    double total = 0;
                    double mean = 0;

                    for (int i = 0; i < 256; i++) {
                        total += Math.abs(i - 128 - avg_of_gray_pixels) * calc_gray_pixel[i];
                    }

                    Log.i(TAG, "ooooookkkkkk22222");
                    mean = total / (grayBitmap.getHeight() * grayBitmap.getWidth());
                    double cast = Math.abs(avg_of_gray_pixels / mean);

                    //            Log.i(TAG, "light: %f", cast);



                    if (cast > 1) {
                        if (avg_of_gray_pixels > 0) {
                            Log.i(TAG, "too light");
                        } else
                            Log.i(TAG, "too dark");
                    } else
                        Log.i(TAG, "brightness normal!!");
                }
            }).start();

            imgHuaishi.setImageBitmap(grayBitmap);
            Log.i(TAG, "over!!");

        }
    }
    private class ProcessClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
//            if(isFirst)
//            {
            procSrc2Gray();
//                isFirst = false;
//            }
            if(flag){
                imgHuaishi.setImageBitmap(grayBitmap);
                btnProcess.setText("查看原图");
                flag = false;
            }
            else{
                imgHuaishi.setImageBitmap(srcBitmap);
                btnProcess.setText("灰度化");
                flag = true;
            }
        }

    }

    public void procSrc2Gray(){
        Mat rgbMat = new Mat();
        Mat grayMat = new Mat();

        MatOfDouble m = new MatOfDouble();
        MatOfDouble s = new MatOfDouble();

        srcBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.clear_220_220_new);
        grayBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Bitmap.Config.RGB_565);
        Utils.bitmapToMat(srcBitmap, rgbMat);//convert original bitmap to Mat, R G B.

        long time1 = System.currentTimeMillis();
        Imgproc.Laplacian(rgbMat, grayMat, -1, 3, 1, 0, Core.BORDER_DEFAULT);
        Core.meanStdDev(grayMat, m, s);

        double mm = m.get(0,0)[0];
        double ss = s.get(0,0)[0];
        long take_time = System.currentTimeMillis() - time1;


//        int d = 10;
        //Imgproc.cvtColor(rgbMat, grayMat, Imgproc.COLOR_RGB2GRAY);//rgbMat to gray grayMat
        Utils.matToBitmap(grayMat, grayBitmap); //convert mat to bitmap

        Log.i(TAG, "std is:..." + ss);
        String  str=String.valueOf(take_time);
        Log.i(TAG, str);
//        System.out.println("aaa %lf", ss);
    }


    public void initUI(){
        btnProcess = (Button)findViewById(R.id.btn_gray_process);
        btnBrightness = (Button)findViewById(R.id.btn_brightness);
        imgHuaishi = (ImageView)findViewById(R.id.img_huaishi);
        Log.i(TAG, "initUI sucess...");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        btnProcess.setOnClickListener(new ProcessClickListener());
        btnBrightness.setOnClickListener(new BrightnessClickListener());

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
}
