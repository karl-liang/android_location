package com.lh.android.location.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.myapplication.R;
import com.lh.android.location.image.AsynImageLoader;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TrailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TrailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public TrailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrailFragment newInstance(String param1, String param2) {
        TrailFragment fragment = new TrailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private ImageView imageView;
    private Button addZoomBotton;
    private Button subZoomBotton;

    private int zoom  =12;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trail, container, false);
        // Inflate the layout for this fragment
        imageView = (ImageView) view.findViewById(R.id.trail_imageview);
        addZoomBotton = view.findViewById(R.id.trail_button_addzoom);
        subZoomBotton = view.findViewById(R.id.trail_button_subzoom);


        addZoomBotton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                if(zoom > 16){
                    Toast.makeText(view.getContext(), "已最大", Toast.LENGTH_SHORT).show();
                }else{
                    zoom ++;
                    loadTrail();
                }

            }
        });
        subZoomBotton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                if(zoom < 2){
                    Toast.makeText(view.getContext(), "已最小", Toast.LENGTH_SHORT).show();
                }else{
                    zoom --;
                    loadTrail();
                }

            }
        });
        loadTrail();


        return view;
    }

    private void loadTrail(){
        AsynImageLoader asynImageLoader = new AsynImageLoader();
        try{
            TelephonyManager tm = (TelephonyManager) this.getActivity().getSystemService(this.getActivity().TELEPHONY_SERVICE);
            String phoneNumber1 = tm.getLine1Number();
            if(phoneNumber1.length() == 0)
                phoneNumber1 = tm.getSimSerialNumber();
//&startDateStr="+java.net.URLEncoder.encode("2018-08-08 00:00:00")+"
            asynImageLoader.showImageAsyn(imageView, "http://120.78.82.133:8080/cnicg-code/v1/trail?zoom="+zoom+"&person="+phoneNumber1, R.mipmap.ic_launcher);
        }catch (SecurityException e){
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
