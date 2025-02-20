package vn.tapbi.zazip.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

import vn.tapbi.zazip.R;
import vn.tapbi.zazip.databinding.ItemSortViewBinding;
import vn.tapbi.zazip.interfaces.ClickSortViewListener;
import vn.tapbi.zazip.model.PresentationOption;

public class SortViewAdapter extends RecyclerView.Adapter<SortViewAdapter.ViewHolder> {
    private List<PresentationOption> listSort = new LinkedList<>();
    private Context context;
    private int id = -1, idDesc = -1;
    private boolean sortDesc ;
    private ClickSortViewListener clickSortViewListener;

    public void setId(int id) {
        this.id = id;
    }

    public void setSortDesc(boolean sortDesc) {
        this.sortDesc = sortDesc;
    }

    public void setIdDesc(int idDesc) {
        this.idDesc = idDesc;
    }

    public void setClickSortViewListener(ClickSortViewListener clickSortViewListener) {
        this.clickSortViewListener = clickSortViewListener;
    }

    public void setListSort(List<PresentationOption> listSort) {
        this.listSort = listSort;
    }

    public SortViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemSortViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.tvTypeSort.setText(listSort.get(holder.getAdapterPosition()).getNameOption());
        if (id == holder.getAdapterPosition()) {
            holder.binding.ivView.setImageResource(listSort.get(holder.getAdapterPosition()).getImageOptionSelect());
            holder.binding.tvTypeSort.setTextColor(context.getResources().getColor(R.color.text_blueFile));
        } else {
            holder.binding.ivView.setImageResource(listSort.get(holder.getAdapterPosition()).getImageOption());
            holder.binding.tvTypeSort.setTextColor(context.getResources().getColor(R.color.text_gray));
        }

        if (idDesc == holder.getAdapterPosition()){
            if (sortDesc) {
                holder.binding.ivView.setImageResource(listSort.get(listSort.size()-1).getImageOptionSelect());
                holder.binding.tvTypeSort.setTextColor(context.getResources().getColor(R.color.text_blueFile));
            } else {
                holder.binding.ivView.setImageResource(listSort.get(listSort.size()-1).getImageOption());
                holder.binding.tvTypeSort.setTextColor(context.getResources().getColor(R.color.text_gray));
            }
        }

        holder.binding.cslSortView.setOnClickListener(v -> {
            if (id >= 0 && id < listSort.size() - 1) notifyItemChanged(id);
            if (id != holder.getAdapterPosition() && holder.getAdapterPosition() != listSort.size()-1) {
                id = holder.getAdapterPosition();
                notifyDataSetChanged();
            }

            if (holder.getAdapterPosition() == listSort.size() - 1){
                idDesc = holder.getAdapterPosition();
                sortDesc = !sortDesc;
                notifyDataSetChanged();
            }
            clickSortViewListener.onClickItemSort(id);
            clickSortViewListener.onClickItemSortDesc(sortDesc);

        });
    }

    @Override
    public int getItemCount() {
        return listSort.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemSortViewBinding binding;

        public ViewHolder(@NonNull ItemSortViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
