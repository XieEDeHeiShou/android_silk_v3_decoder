package com.ketian.android.silkv3;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ketian.
 */
public class VoiceFragment extends Fragment {

    private static final int REQUEST_CODE_PERMISSION = 1;
    private static final String TAG = VoiceFragment.class.getSimpleName();
    private LoadTask mTask = null;
    private RecyclerView mRecyclerView;
    private ItemAdapter mAdapter;
    private List<String> mItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voice, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ItemAdapter(getContext(), mItems);// item click handler inside
        mRecyclerView.setAdapter(mAdapter);

        initData();
    }

    private void initData() {
        if (mTask != null) {
            mTask.cancel(true);
        }

        Activity activity = getActivity();
        if (activity == null) {
            Log.e(TAG, "initData: WHAT?? ");
            return;
        }

        int checkSelfPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
            return;
        }
        mTask = new LoadTask(mItems, mAdapter);
        mTask.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            int length = permissions.length;
            for (int i = 0; i < length; i++) {
                if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getActivity(), "Write external permission not granted.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
            initData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mTask != null) {
            mTask.cancel(true);
        }
    }
}
