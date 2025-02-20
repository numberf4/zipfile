package vn.tapbi.zazip.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

import vn.tapbi.zazip.R;
import vn.tapbi.zazip.data.model.ItemSelectBottom;
import vn.tapbi.zazip.databinding.ItemOptionSelectBottomBinding;
import vn.tapbi.zazip.utils.Utils;

public class OptionSelectBottomAdapter extends RecyclerView.Adapter<OptionSelectBottomAdapter.ViewHolder> {
    private List<ItemSelectBottom> list = new LinkedList<>();
    private OnClickItemBottomSelect onClickItemBottomSelect;
    private Context context;
    private int id = -1;
    private int color = R.color.text_blue;
    private long lastClickTime2 = 0;

    public void setList(List<ItemSelectBottom> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void setId(int id) {
        this.id = id;
    }


    public void changeDataOption(boolean isRename, boolean isExtract) {
        if (list.size() > 5) {
            ItemSelectBottom a = list.get(0);
            ItemSelectBottom b = list.get(2);
            if (isRename && !isExtract) {
                a.setColorText(context.getResources().getColor(R.color.text_blue));
                b.setColorText(context.getResources().getColor(R.color.text_gray));
                a.setImageOption(R.drawable.ic_rename);
                b.setImageOption(R.drawable.ic_extract_gray);
            } else if (isRename && isExtract) {
                a.setColorText(context.getResources().getColor(R.color.text_blue));
                b.setColorText(context.getResources().getColor(R.color.text_blue));
                a.setImageOption(R.drawable.ic_rename);
                b.setImageOption(R.drawable.ic_extract);
            } else if (!isRename && !isExtract) {
                a.setColorText(context.getResources().getColor(R.color.text_gray));
                b.setColorText(context.getResources().getColor(R.color.text_gray));
                a.setImageOption(R.drawable.ic_rename_gray);
                b.setImageOption(R.drawable.ic_extract_gray);
            }
            list.set(0, a);
            list.set(2, b);
            notifyDataSetChanged();
        }

    }

    public OptionSelectBottomAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemOptionSelectBottomBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.tvItemSelect.setText(list.get(holder.getAdapterPosition()).getTxtOption());
        holder.binding.ivItemSelect.setImageResource(list.get(holder.getAdapterPosition()).getImageOption());
        holder.binding.tvItemSelect.setTextColor(list.get(holder.getAdapterPosition()).getColorText());

        holder.binding.cslItemSelectBottom.setOnClickListener(v -> {
            if (Utils.checkTime())
                onClickItemBottomSelect.onClickItemBottom(holder.getAdapterPosition());
        });
    }

    public interface OnClickItemBottomSelect {
        void onClickItemBottom(int position);
    }

    public void setOnClickItemBottomListener(OnClickItemBottomSelect onClickItemBottomSelect) {
        this.onClickItemBottomSelect = onClickItemBottomSelect;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemOptionSelectBottomBinding binding;

        public ViewHolder(@NonNull ItemOptionSelectBottomBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
