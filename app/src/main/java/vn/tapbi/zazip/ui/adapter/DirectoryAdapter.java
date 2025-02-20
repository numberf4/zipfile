package vn.tapbi.zazip.ui.adapter;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import vn.tapbi.zazip.R;
import vn.tapbi.zazip.databinding.ItemDirectoryFileBinding;

public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.ViewHolder> {
    private ArrayList<String> arrayPathDirectory = new ArrayList<>();
    private OnClickPathDirectory onClickPathDirectory;
    private Context context;
    private String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();

    private int getSubLength() {
        String[] s = rootPath.substring(rootPath.indexOf("storage")).split(File.separator);
        return s.length;
    }

    public void setPath(String path) {
        arrayPathDirectory.clear();
        String[] s = path.substring(path.indexOf("storage")).split(File.separator);
        arrayPathDirectory.add(context.getString(R.string.internal_storage));
        arrayPathDirectory.addAll(Arrays.asList(s).subList(getSubLength(), s.length));
        notifyDataSetChanged();
    }

    public DirectoryAdapter(Context context) {
        this.context = context;
    }

    public void setOnClickPathDirectory(OnClickPathDirectory onClickPathDirectory) {
        this.onClickPathDirectory = onClickPathDirectory;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemDirectoryFileBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = arrayPathDirectory.get(holder.getAdapterPosition());

        holder.binding.tvDirectory.setText(item);
        if (position == arrayPathDirectory.size() - 1) {
            holder.binding.tvDirectory.setTextColor(context.getResources().getColor(R.color.text_blue));
        } else {
            holder.binding.tvDirectory.setTextColor(context.getResources().getColor(R.color.text_gray));
        }

        holder.binding.lnDirectory.setOnClickListener(v -> {
            StringBuilder currentPath = new StringBuilder(rootPath);
            for (int i = 1; i <= position; i++) {
                currentPath.append(File.separator);
                currentPath.append(arrayPathDirectory.get(i));
            }
            onClickPathDirectory.onclickPath(currentPath.toString());
        });
    }

    @Override
    public int getItemCount() {
        return arrayPathDirectory.size();
    }


    public interface OnClickPathDirectory {
        void onclickPath(String path);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemDirectoryFileBinding binding;

        public ViewHolder(@NonNull ItemDirectoryFileBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }
}
