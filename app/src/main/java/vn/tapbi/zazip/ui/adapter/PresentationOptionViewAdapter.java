package vn.tapbi.zazip.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

import vn.tapbi.zazip.R;
import vn.tapbi.zazip.databinding.ItemSortViewBinding;
import vn.tapbi.zazip.interfaces.ClickPresentationView;
import vn.tapbi.zazip.model.PresentationOption;

public class PresentationOptionViewAdapter extends RecyclerView.Adapter<PresentationOptionViewAdapter.ViewHolder> {
    private List<PresentationOption> listOption = new LinkedList<>();
    private Context context;
    private int id = -1;
    private ClickPresentationView cLickItemListener;

    public void setId(int id) {
        this.id = id;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setListOption(List<PresentationOption> listOption) {
        this.listOption = listOption;
        notifyDataSetChanged();
    }

    public void setCLickItemListener(ClickPresentationView cLickItemListener) {
        this.cLickItemListener = cLickItemListener;
    }

    public PresentationOptionViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemSortViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.tvTypeSort.setText(listOption.get(holder.getAdapterPosition()).getNameOption());
        if (id == holder.getAdapterPosition()) {
            holder.binding.ivView.setImageResource(listOption.get(holder.getAdapterPosition()).getImageOptionSelect());
            holder.binding.tvTypeSort.setTextColor(context.getResources().getColor(R.color.text_blueFile));
        } else {
            holder.binding.ivView.setImageResource(listOption.get(holder.getAdapterPosition()).getImageOption());
            holder.binding.tvTypeSort.setTextColor(context.getResources().getColor(R.color.text_gray));
        }
        holder.binding.cslSortView.setOnClickListener(v -> {
            if (id >= 0) notifyItemChanged(id);
            if (id != holder.getAdapterPosition()) {
                id = holder.getAdapterPosition();
                notifyDataSetChanged();
            }
            cLickItemListener.onClickItemPresentation(id);
        });
    }

    @Override
    public int getItemCount() {
        return listOption.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemSortViewBinding binding;

        public ViewHolder(@NonNull ItemSortViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}
