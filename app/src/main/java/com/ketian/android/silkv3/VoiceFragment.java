package com.ketian.android.silkv3;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ketian.android.silkv3.jni.JNI;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

/**
 * Created by ketian.
 */
public class VoiceFragment extends Fragment {

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
        mAdapter = new ItemAdapter(getContext(), mItems);
        mRecyclerView.setAdapter(mAdapter);

        initData();
    }

    private void initData() {
        if (mTask != null) {
            mTask.cancel(true);
        }

        mTask = new LoadTask(mItems, mAdapter);
        mTask.execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mTask != null) {
            mTask.cancel(true);
        }
    }

    private static class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.VH> {

        private List<String> paths;
        private Context mContext;

        private View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = (int) view.getTag();
                String path = paths.get(index);
                File file = new File(path);
                if (file.exists() && file.canRead()) {
                    String dest = PathUtils.getExportDir() + "/" + file.getName() + ".mp3";
                    JNI.x(path, dest);
                    Toast.makeText(mContext, "Convert to " + dest + " OK", Toast.LENGTH_SHORT).show();
                }
            }
        };

        ItemAdapter(Context context, List<String> items) {
            mContext = context;
            paths = items;
        }

        @NonNull
        @Override
        public ItemAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemAdapter.VH holder, int position) {
            holder.textView.setText(paths.get(position));
            holder.button.setTag(position);
            holder.button.setOnClickListener(listener);
        }

        @Override
        public int getItemCount() {
            return paths != null ? paths.size() : 0;
        }

        static class VH extends RecyclerView.ViewHolder {
            TextView textView;
            Button button;

            VH(View itemView) {
                super(itemView);

                textView = itemView.findViewById(R.id.title);
                button = itemView.findViewById(R.id.click);
            }
        }
    }

    private static class LoadTask extends AsyncTask<Void, Void, List<String>> {

        private final Collection<String> mItems;
        private final RecyclerView.Adapter mAdapter;

        LoadTask(Collection<String> mItems, RecyclerView.Adapter mAdapter) {
            this.mItems = mItems;
            this.mAdapter = mAdapter;
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
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
            mItems.clear();
            mItems.addAll(strings);

            mAdapter.notifyDataSetChanged();
        }
    }
}
