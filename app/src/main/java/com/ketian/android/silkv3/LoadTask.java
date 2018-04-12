package com.ketian.android.silkv3;

import android.Manifest;
import android.os.AsyncTask;
import android.support.annotation.RequiresPermission;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

/**
 * @author <a href="mailto:qq2325690622@gmail.com">DengChao</a> 2018/4/12
 */
class LoadTask extends AsyncTask<Void, Void, List<String>> {

    private static final String TAG = LoadTask.class.getSimpleName();
    private final Collection<String> mItems;
    private final RecyclerView.Adapter mAdapter;

    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    LoadTask(Collection<String> mItems, RecyclerView.Adapter mAdapter) {
        this.mItems = mItems;
        this.mAdapter = mAdapter;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "onPreExecute: ");
    }

    @Override
    protected List<String> doInBackground(Void... voids) {
        Log.d(TAG, "doInBackground: ");
        List<String> paths = PathUtils.getVoiceFiles_WeChat();
        ArrayList<String> voicePaths = new ArrayList<>();

        if (paths != null && paths.size() > 0) {
            File file;
            for (String path : paths) {
                if (path != null) {
                    file = new File(path);
                    if (file.exists() && file.isDirectory()) {
                        Stack<String> stack = new Stack<>();
                        stack.push(path);
                        while (!stack.empty()) {
                            File[] fs = null;
                            String parent = stack.pop();
                            if (parent != null) {
                                file = new File(parent);
                                if (file.isDirectory()) { // ignore file, FIXME
                                    fs = file.listFiles();
                                } else {
                                    continue;
                                }
                            }
                            if (fs == null || fs.length == 0) continue;
                            for (File f : fs) {
                                final String name = f.getName();
                                if (f.isDirectory() && !name.equals(".")
                                        && !name.equals("..")) {
                                    stack.push(f.getPath());
                                } else if (f.isFile()) {
                                    if (name.endsWith(".amr")) {
                                        voicePaths.add(f.getAbsolutePath());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return voicePaths;
    }

    @Override
    protected void onPostExecute(List<String> strings) {
        Log.d(TAG, "onPostExecute: ");
        mItems.clear();
        mItems.addAll(strings);

        mAdapter.notifyDataSetChanged();
    }
}
