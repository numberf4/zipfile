package vn.tapbi.zazip.ui.adapter;

import static vn.tapbi.zazip.common.Constant.TYPE_DROP_BOX_FILE;
import static vn.tapbi.zazip.common.Constant.TYPE_GG_DRIVE_FILE;
import static vn.tapbi.zazip.common.Constant.TYPE_ONE_DRIVE_FILE;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dropbox.core.v2.files.Metadata;

import java.util.LinkedList;
import java.util.List;

import vn.tapbi.zazip.common.Constant;
import vn.tapbi.zazip.data.model.FileData;
import vn.tapbi.zazip.data.repository.FileDataRepository;
import vn.tapbi.zazip.databinding.ItemRecentFileBinding;
import vn.tapbi.zazip.ui.adapter.holder.FileDataViewHolder;
import vn.tapbi.zazip.utils.Utils;


public class DetailSelectAdapter extends RecyclerView.Adapter<FileDataViewHolder> {

    public FileDataRepository fileDataRepository;
    private OnClickItemDetail onClickItemDetail;
    private List<FileData> fileDataList = new LinkedList<>();

    public void setFileDataRepository(FileDataRepository fileDataRepository) {
        this.fileDataRepository = fileDataRepository;
    }

    public List<FileData> getFileDataList() {
        return fileDataList;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearList() {
        fileDataList.clear();
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFileDataList(List<FileData> fileDataList) {
        this.fileDataList = fileDataList;
        notifyDataSetChanged();
    }

    public void setOnClickItemDetail(OnClickItemDetail onClickItemDetail) {
        this.onClickItemDetail = onClickItemDetail;
    }

    public DetailSelectAdapter() {

    }

    @NonNull
    @Override
    public FileDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecentFileBinding binding = ItemRecentFileBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FileDataViewHolder(binding, fileDataRepository, Constant.SORT_VIEW_DETAIL);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull FileDataViewHolder holder, int position) {
        FileData fileData = fileDataList.get(holder.getAdapterPosition());
        if ((fileData.getType() == TYPE_ONE_DRIVE_FILE || fileData.getType() == TYPE_DROP_BOX_FILE || fileData.getType() == TYPE_GG_DRIVE_FILE)) {
            holder.binding.viewSpace.setVisibility(View.VISIBLE);
        } else {
            holder.binding.viewSpace.setVisibility(View.GONE);
        }
        holder.binding.ivCancel.setVisibility(View.VISIBLE);
        holder.binding.imgItemCheck.setVisibility(View.GONE);
        holder.setData(fileData);
        holder.binding.ivCancel.setOnClickListener(v ->
                {
                    if (Utils.checkTime()) {
                        onClickItemDetail.onClickCancelItem(fileDataList.get(holder.getAdapterPosition()));
                    }
                }
        );
    }

    public interface OnClickItemDetail {
        void onClickCancelItem(FileData path);
    }

    @Override
    public int getItemCount() {
        return fileDataList.size();
    }
}
