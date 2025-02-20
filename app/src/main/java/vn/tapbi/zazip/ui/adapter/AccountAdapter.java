package vn.tapbi.zazip.ui.adapter;

import static vn.tapbi.zazip.common.Constant.DROP_BOX;
import static vn.tapbi.zazip.common.Constant.GOOGLE_DRIVE;
import static vn.tapbi.zazip.common.Constant.ONE_DRIVE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;
import vn.tapbi.zazip.R;
import vn.tapbi.zazip.data.model.Account;
import vn.tapbi.zazip.databinding.ItemAccountBinding;
import vn.tapbi.zazip.utils.Utils;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.HolderCloud> {

    private OnItemClickedListener onItemClickedListener;
    private Context context;
    private boolean isEditAccount = false;

    private boolean checkShowAddAccountOther = false;
    private int count = 0;

    public AccountAdapter(OnItemClickedListener onItemClickedListener, Context context) {
        this.onItemClickedListener = onItemClickedListener;
        this.context = context;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void checkEditAccount(boolean isEditAccount) {
        this.isEditAccount = isEditAccount;
        notifyDataSetChanged();
    }

    public boolean getCheckEditAccount() {
        return isEditAccount;
    }

    private List<Account> accountList = new ArrayList<>();

    public static class HolderCloud extends RecyclerView.ViewHolder {
        ItemAccountBinding binding;

        public HolderCloud(@NonNull ItemAccountBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public boolean isCheckShowAddAccountOther() {
        return checkShowAddAccountOther;
    }

    public void setCheckShowAddAccountOther(boolean checkShowAddAccountOther) {
        this.checkShowAddAccountOther = checkShowAddAccountOther;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<Account> accountList) {
        this.accountList.clear();
        this.accountList.addAll(accountList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HolderCloud onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HolderCloud(ItemAccountBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCloud holder, int position) {
        holder.binding.ivDeleteAccount.setVisibility(View.GONE);
        int countDefault = 0;
        for (int i = 0; i < accountList.size(); i++) {
            String type = accountList.get(i).getType();
            switch (type) {
                case GOOGLE_DRIVE:
                    if (accountList.get(i).getAccountName().equals(GOOGLE_DRIVE)) {
                        countDefault += 1;
                    }
                    break;
                case ONE_DRIVE:
                    if (accountList.get(i).getAccountName().equals(ONE_DRIVE)) {
                        countDefault += 1;
                    }
                    break;
                case DROP_BOX:
                    if (accountList.get(i).getAccountName().equals(DROP_BOX)) {
                        countDefault += 1;
                    }
                    break;
            }
        }
        if(position==0){
            holder.binding.viewTop.setVisibility(View.VISIBLE);
        }else{
            holder.binding.viewTop.setVisibility(View.GONE);
        }
        if (position == accountList.size() - 1 && countDefault != 3) {
            holder.binding.tvAddAccountOther.setVisibility(View.VISIBLE);
        } else {
            holder.binding.tvAddAccountOther.setVisibility(View.GONE);
        }
        if(position==accountList.size()-1){
            holder.binding.viewBot.setVisibility(View.VISIBLE);
        }else{
            holder.binding.viewBot.setVisibility(View.GONE);
        }
        switch (accountList.get(position).getType()) {
            case GOOGLE_DRIVE:
                setLayoutForAccount(holder, R.drawable.ic_icon_gg_drive, R.string.google_drive, GOOGLE_DRIVE, position);
                break;
            case ONE_DRIVE:
                setLayoutForAccount(holder, R.drawable.ic_icon_one_drive, R.string.one_drive, ONE_DRIVE, position);
                break;
            case DROP_BOX:
                setLayoutForAccount(holder, R.drawable.icon_dropbox, R.string.drop_box, DROP_BOX, position);
                break;
        }
        holder.binding.viewClickItem.setOnClickListener(v -> {
            if (Utils.checkLongTime()) {
                if (holder.binding.layoutDrive.getVisibility() == View.VISIBLE) {
                    onItemClickedListener.onAccountClicked(holder.getAdapterPosition());
                } else {
                    onItemClickedListener.onAddClicked(holder.getAdapterPosition());
                }
            }
        });

        if (isEditAccount) {
            if (accountList.get(position).getAccountName().equals(context.getString(R.string.gg_drive))
                    || accountList.get(position).getAccountName().equals(context.getString(R.string.one_drive)) ||
                    accountList.get(position).getAccountName().equals(context.getString(R.string.drop_box))) {
                holder.binding.ivDeleteAccount.setVisibility(View.GONE);
            } else {
                holder.binding.ivDeleteAccount.setVisibility(View.VISIBLE);
            }
            holder.binding.viewClickItem.setEnabled(false);
            if(countDefault==3){
                holder.binding.viewClickItem.setEnabled(true);
            }
            Timber.d("isEditAccount false");
        } else {
            holder.binding.viewClickItem.setEnabled(true);
            Timber.d("isEditAccount true");
            holder.binding.ivDeleteAccount.setVisibility(View.GONE);
        }
        holder.binding.ivDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickedListener.onDeleteClicked(holder.getAdapterPosition());
            }
        });
        holder.binding.tvAddAccountOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickedListener.onClickAddAccountOther();
            }
        });
    }

    public void setLayoutForAccount(@NonNull HolderCloud holder, int p, int p2, String googleDrive, int position) {
        setContentForAccount(holder, p, p2);
        if (accountList.get(position).getAccountName().equals(googleDrive)) {
            setShowHideAccount(holder, View.VISIBLE, View.INVISIBLE);
            holder.binding.ivArrowNext.setImageResource(R.drawable.ic_icon_add);
        } else {
            setAccount(holder, position, p);
            holder.binding.ivArrowNext.setImageResource(R.drawable.ic_icon_arrow_right);
        }
    }

    public void setShowHideAccount(@NonNull HolderCloud holder, int visible, int gone) {
        holder.binding.tvGgDrive.setVisibility(visible);
        holder.binding.layoutDrive.setVisibility(gone);
    }

    @SuppressLint("SetTextI18n")
    public void setAccount(@NonNull HolderCloud holder, int position, int p) {
        holder.binding.imgIconCloud.setImageResource(p);
        holder.binding.tvAccountName.setText(accountList.get(position).getAccountName());
        String timeLogin = new SimpleDateFormat("HH:mm", Locale.US).format(accountList.get(position).getTime());
        String contentTime = Utils.getDateLogin(accountList.get(position).getTime() / 1000, context);

        holder.binding.tvDetailTime.setText(context.getString(R.string.last_logged_in) + " " + contentTime + " " + context.getString(R.string.at) + " " + timeLogin);

        setShowHideAccount(holder, View.INVISIBLE, View.VISIBLE);
    }

    public void setContentForAccount(@NonNull HolderCloud holder, int p, int p2) {
        holder.binding.tvGgDrive.setCompoundDrawablesWithIntrinsicBounds(p, 0, 0, 0);
        holder.binding.tvGgDrive.setText(holder.itemView.getContext().getResources().getString(p2));
    }

    public interface OnItemClickedListener {
        void onAddClicked(int position);

        void onAccountClicked(int position);

        void onDeleteClicked(int position);

        void onClickAddAccountOther();
    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }
}
