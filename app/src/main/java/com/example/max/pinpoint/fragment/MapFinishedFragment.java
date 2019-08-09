package com.example.max.pinpoint.fragment;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.max.pinpoint.BeaconData;
import com.example.max.pinpoint.DistanceCalculator;
import com.example.max.pinpoint.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.max.pinpoint.fragment.SetupMap1Fragment.MAX_WALLS;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFinishedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFinishedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFinishedFragment extends Fragment {

    private static final String TAG = "MapFinishedFragment";

    private String mParam1;
    private String mParam2;

    private double length;
    private double width;
    private String filepath;
    private List<BeaconData> beacons = new ArrayList<BeaconData>();

    private OnFragmentInteractionListener mListener;

    public MapFinishedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFinishedFragment.
     */
    public static MapFinishedFragment newInstance(String param1, String param2) {
        MapFinishedFragment fragment = new MapFinishedFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map_finished, container, false);

        // Gets selected arguments
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            length = bundle.getDouble("length");
            width = bundle.getDouble("width");

            for(int i = 0; i < MAX_WALLS; ++i) {
                BeaconData beacon = bundle.getParcelable("beacon" + Integer.toString(i + 1));
                // Store them for use
                beacons.add(beacon);
            }

            new AlertDialog.Builder(getActivity())
                    .setMessage("이미지 크기 세로: " + Double.toString(length) + ",  가로: " + Double.toString(width))
                    .setPositiveButton("Ok", null)
                    .show();
        }

        generateMap();

        Button continueBtn = (Button) rootView.findViewById(R.id.continueButton);
        continueBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Fragment frag = new HomeFragment();
                FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
                fragTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);

                // Pass the bundle to the main activity
                Bundle args = new Bundle();

                // Add the filepath to the bundle
                args.putString("filepath", filepath);

                // Add the beacons to the bundle
                for (int i = 0; i < MAX_WALLS; ++i)
                {
                    args.putParcelable("beacon" + Integer.toString(i + 1), beacons.get(i));
                }

                // Store width and length for relative distance measurements
                args.putDouble("length", length);
                args.putDouble("width", width);

                frag.setArguments(args);

                fragTransaction.replace(R.id.frame, frag);
                fragTransaction.commitAllowingStateLoss();
            }
        });

        return rootView;
    }

    public void generateMap() {

        Log.e(TAG, "generateMap()===Start");

        Paint myRectPaint = new Paint();
        DistanceCalculator dc = new DistanceCalculator();
//        float x1 = 0;
//        float y1 = 0;
//        float x2 = (float) width * dc.expansionScale(width, length);
//        float y2 = (float) length * dc.expansionScale(width, length);

        // Create a new image bitmap and attach a brand new canvas to it
        //Bitmap tempBitmap = Bitmap.createBitmap((int)(x2), (int)(y2), Bitmap.Config.ARGB_8888);

        AssetManager am = this.getContext().getAssets();
        Bitmap tempBitmap = null;
        BufferedInputStream buf = null;
        try {

            buf = new BufferedInputStream(am.open("IMG_0863.jpg"));
            Bitmap bitmap = BitmapFactory.decodeStream(buf);

            width = bitmap.getWidth();
            length = bitmap.getHeight();

            float x1 = 0;
            float y1 = 0;
            float x2 = (float) width * dc.expansionScale(width, length);
            float y2 = (float) length * dc.expansionScale(width, length);

            //tempBitmap = Bitmap.createBitmap(bitmap);
            tempBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

            Canvas tempCanvas = new Canvas(tempBitmap);
            //테두리.
            myRectPaint.setColor(Color.parseColor("#07f527"));
            myRectPaint.setStyle(Paint.Style.STROKE);
            myRectPaint.setStrokeWidth(10);

            tempCanvas.drawBitmap(tempBitmap, 0, 0, null);

            // Draw what you want on the canvas
            //tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);

            // Draw the beacons
            Paint beaconPaint = new Paint();
            beaconPaint.setColor(Color.parseColor("#07f527"));
            beaconPaint.setStyle(Paint.Style.STROKE);
            //beaconPaint.setStyle(Paint.Style.FILL);
            beaconPaint.setStrokeWidth(20);

            Paint beaconPaint2 = new Paint();
            beaconPaint2.setColor(Color.parseColor("#071bf5"));
            beaconPaint2.setStyle(Paint.Style.STROKE);
            //beaconPaint2.setStyle(Paint.Style.FILL);
            beaconPaint2.setStrokeWidth(20);

            Paint beaconPaint3 = new Paint();
            beaconPaint3.setColor(Color.parseColor("#df05f7"));
            beaconPaint3.setStyle(Paint.Style.STROKE);
            //beaconPaint3.setStyle(Paint.Style.FILL);
            beaconPaint3.setStrokeWidth(20);

            //비콘 위치
//            tempCanvas.drawCircle(x2 / 2, 0, 2, beaconPaint);
//            tempCanvas.drawCircle(x2, y2 / 2, 2, beaconPaint);
//            tempCanvas.drawCircle(x2 / 2, y2, 2, beaconPaint);
//            tempCanvas.drawCircle(0, y2 / 2, 2, beaconPaint);

            Log.e(TAG, "generateMap()===SharedPreferences setting get Value Start");

            //저장된 비콘위치
            SharedPreferences settings = getActivity().getSharedPreferences("pinpoint", Context.MODE_PRIVATE);

            Log.e(TAG, "generateMap()===SharedPreferences settings====>" + settings);

            String beaconNm1 = settings.getString("beaconNm1", "1");
            String beaconNm2 = settings.getString("beaconNm2", "2");
            String beaconNm3 = settings.getString("beaconNm3", "3");

            float beaconW1 = settings.getFloat("beaconW1", 0);
            float beaconW2 = settings.getFloat("beaconW2", 0);
            float beaconW3 = settings.getFloat("beaconW3", 0);

            Log.e(TAG, "generateMap()===SharedPreferences beaconW1====>" + beaconW1);

            float beaconH1 = settings.getFloat("beaconH1", 0);
            float beaconH2 = settings.getFloat("beaconH2", 0);
            float beaconH3 = settings.getFloat("beaconH3", 0);

            Log.e(TAG,"Float.parseFloat(beaconW1), Float.parseFloat(beaconH1)==>" + beaconW1 +", " +beaconH1);
            Log.e(TAG,"Float.parseFloat(beaconW2), Float.parseFloat(beaconH2)==>" + beaconW2 +", " +beaconH2);
            Log.e(TAG,"Float.parseFloat(beaconW3), Float.parseFloat(beaconH3)==>" + beaconW3 +", " +beaconH3);

            tempCanvas.drawCircle( beaconW1, beaconH1, 2, beaconPaint);
            tempCanvas.drawCircle( beaconW2, beaconH2, 2, beaconPaint2);
            tempCanvas.drawCircle( beaconW3, beaconH3, 2, beaconPaint3);


            //Text 볼드처리.
            Typeface typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);

            Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mTextPaint.setColor(Color.parseColor("#07f527"));
            //this ensures X alignment
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            mTextPaint.setTextSize(22f);
            mTextPaint.setTypeface(typeface);
            // Measure the text rectangle to get the height
            Rect result = new Rect();
            mTextPaint.getTextBounds("1", 0, 1, result);
            //take half the height as the offset
            int yOffset = result.height()/2;
            ///add offset to ensure Y is aligned center
            tempCanvas.drawText("1", beaconW1, beaconH1+yOffset, mTextPaint);

            Paint mTextPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
            mTextPaint2.setColor(Color.parseColor("#071bf5"));
            //this ensures X alignment
            mTextPaint2.setTextAlign(Paint.Align.CENTER);
            mTextPaint2.setTextSize(22f);
            mTextPaint2.setTypeface(typeface);
            // Measure the text rectangle to get the height
            Rect result2 = new Rect();
            mTextPaint2.getTextBounds("2", 0, 1, result2);
            //take half the height as the offset
            int yOffset2 = result2.height()/2;
            ///add offset to ensure Y is aligned center
            tempCanvas.drawText("2", beaconW2, beaconH2+yOffset2, mTextPaint2);


            Paint mTextPaint3 = new Paint(Paint.ANTI_ALIAS_FLAG);
            mTextPaint3.setColor(Color.parseColor("#df05f7"));
            //this ensures X alignment
            mTextPaint3.setTextAlign(Paint.Align.CENTER);
            mTextPaint3.setTextSize(22f);
            mTextPaint3.setTypeface(typeface);
            // Measure the text rectangle to get the height
            Rect result3 = new Rect();
            mTextPaint3.getTextBounds("3", 0, 1, result3);
            //take half the height as the offset
            int yOffset3 = result2.height()/2;
            ///add offset to ensure Y is aligned center
            tempCanvas.drawText("3", beaconW3, beaconH3+yOffset3, mTextPaint3);


            // Save to bundle for use in main activity
            filepath = saveToInternalStorage(tempBitmap);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try{
                buf.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }


//        Canvas tempCanvas = new Canvas(tempBitmap);
//        myRectPaint.setColor(Color.parseColor("#07f527"));
//        myRectPaint.setStyle(Paint.Style.STROKE);
//        myRectPaint.setStrokeWidth(10);
//
//        tempCanvas.drawBitmap(tempBitmap, 0, 0, null);
//
//        // Draw what you want on the canvas
//        tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);
//
//        // Draw the beacons
//        Paint beaconPaint = new Paint();
//        beaconPaint.setColor(Color.parseColor("#07f527"));
//        beaconPaint.setStyle(Paint.Style.STROKE);
//        beaconPaint.setStrokeWidth(20);
//
//        //비콘 위치
//        tempCanvas.drawCircle(x2 / 2, 0, 2, beaconPaint);
//        tempCanvas.drawCircle(x2, y2 / 2, 2, beaconPaint);
//        tempCanvas.drawCircle(x2 / 2, y2, 2, beaconPaint);
//        tempCanvas.drawCircle(0, y2 / 2, 2, beaconPaint);
//
//        // Save to bundle for use in main activity
//        filepath = saveToInternalStorage(tempBitmap);
    }

    private String saveToInternalStorage(Bitmap bitmapImage){

        Log.e(TAG, "saveToInternalStorage()===Start");

        ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
        // Path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, "map.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
                Log.e(TAG, "saveToInternalStorage()===End");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return directory.getAbsolutePath();
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    */

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
