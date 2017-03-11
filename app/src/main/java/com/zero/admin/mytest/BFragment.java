package com.zero.admin.mytest;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @创建者 ZERO
 * @创建时间 2017/3/8 21:56
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */
public class BFragment extends Fragment {
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        
        Log.i("BFragment","---------onAttach-------");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("BFragment","---------onCreate-------");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("BFragment","---------onCreateView-------");

        TextView tv = new TextView(getActivity());
        tv.setText("BFragment");
        tv.setTextSize(60);
        tv.setGravity(Gravity.CENTER);
        return tv;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.i("BFragment","---------onCreateView-------");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.i("BFragment","---------onStart-------");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.i("BFragment","---------onResume-------");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i("BFragment","---------onPause-------");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i("BFragment","---------onStop-------");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.i("BFragment","---------onDestroyView-------");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i("BFragment","---------onDestroy-------");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.i("BFragment","---------onDetach-------");
        super.onDetach();
    }
}
