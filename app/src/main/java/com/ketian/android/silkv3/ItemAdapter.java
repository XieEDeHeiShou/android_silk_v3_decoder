package com.ketian.android.silkv3;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ketian.android.silkv3.jni.JNI;

import java.io.File;
import java.util.List;

/**
 * @author <a href="mailto:qq2325690622@gmail.com">DengChao</a> 2018/4/12
 */
class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.VH> {

    private List<String> paths;
    private Context mContext;

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int index = (int) view.getTag();
            String path = paths.get(index);
            File file = new File(path);
            boolean exists = file.exists();
            boolean canRead = file.canRead();
            if (!exists) {
                Toast.makeText(mContext, "File not exists", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!canRead) {
                Toast.makeText(mContext, "File cannot be read", Toast.LENGTH_SHORT).show();
                return;
            }
            String dest = PathUtils.getExportDir() + "/" + file.getName() + ".mp3";
            JNI.x(path, dest);
            Toast.makeText(mContext, "Convert to " + dest + " OK", Toast.LENGTH_SHORT).show();
        }
    };

    ItemAdapter(Context context, List<String> items) {
        mContext = context;
        paths = items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
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
