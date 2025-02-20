package vn.tapbi.zazip.ui.adapter;

import static vn.tapbi.zazip.common.Constant.TYPE_DROP_BOX_FILE;
import static vn.tapbi.zazip.common.Constant.TYPE_GG_DRIVE_FILE;
import static vn.tapbi.zazip.common.Constant.TYPE_ONE_DRIVE_FILE;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import vn.tapbi.zazip.R;
import vn.tapbi.zazip.common.Constant;
import vn.tapbi.zazip.data.model.FileData;
import vn.tapbi.zazip.data.repository.FileDataRepository;
import vn.tapbi.zazip.databinding.ItemDatePhotoBinding;
import vn.tapbi.zazip.databinding.ItemFilePhotoBinding;
import vn.tapbi.zazip.databinding.ItemRecentFileBinding;
import vn.tapbi.zazip.ui.adapter.holder.FileDataViewHolder;
import vn.tapbi.zazip.utils.Utils;

public class PhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public FileDataRepository fileDataRepository;
    public static final int TYPE_TEXT_TIME = 0;
    public static final int TYPE_ITEM = 1;
    private OnItemPhotoClickedListener onItemClickedListener;
    private List<FileData> listPhoto = new LinkedList<>();
    private List<FileData> list = new LinkedList<>();
    private List<FileData> listTemp = new LinkedList<>();
    public List<String> listSelect = new LinkedList<>();
    private Context context;
    private int typePresentationView;

    public void setTypePresentationView(int typePresentationView) {
        this.typePresentationView = typePresentationView;
    }

    public void setFileDataRepository(FileDataRepository fileDataRepository) {
        this.fileDataRepository = fileDataRepository;
    }

    public PhotoAdapter(Context context) {
        this.context = context;
    }

    public void setListAndSort(List<FileData> list, int typeSortView, boolean desc, String filter) {
        this.list.clear();
        this.list.addAll(list);
        this.listTemp.clear();
        this.listTemp.addAll(list);
        sortView(typeSortView, desc, filter);

    }

    public void setListSelect(List<FileData> listData) {
        if (this.listSelect == null || listData != null && this.listSelect.size() != listData.size()) {
            listSelect.clear();
            for (int i = 0; i < listData.size(); i++) {
                this.listSelect.add(listData.get(i).getFilePath());
            }
            notifyDataSetChanged();
        }
    }

    public void resetAllSelect() {
        listSelect.clear();
        notifyDataSetChanged();
    }

    public void resetList(String filePath) {
        for (int i = listSelect.size() - 1; i >= 0; i--) {
            if (listSelect.get(i).equals(filePath)) {
                listSelect.remove(filePath);
            }
        }
        notifyDataSetChanged();
    }

    public List<FileData> getListPhoto() {
        return listPhoto;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault()).trim();
        listPhoto.clear();
        if (charText.length() == 0) {
            listPhoto.addAll(list);
        } else {
            for (FileData fileData : list) {
                if (fileData.getFileName().toLowerCase(Locale.getDefault()).contains(charText) && fileData.getType() == TYPE_ITEM) {
                    listPhoto.add(fileData);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setTypeSortView(int typeSortView, boolean sortDesc, String filter) {
        sortView(typeSortView, sortDesc, filter);

    }

    private void sortView(int typeSortView, boolean desc, String filter) {
        if (typeSortView == Constant.SORT_VIEW_TYPE_NAME) {
            sortByName(desc, filter);
        } else if (typeSortView == Constant.SORT_VIEW_TYPE_DATE) {
            sortByDate(context, desc, filter);
        } else if (typeSortView == Constant.SORT_VIEW_TYPE_TYPE) {
            sortByType(desc, filter);
        } else if (typeSortView == Constant.SORT_VIEW_TYPE_SIZE) {
            sortBySize(desc, filter);
        }
    }

    private void sortByDate(Context context, boolean desc, String filter) {
        fileDataRepository.sortFileMediaByDate(context, listTemp, desc).subscribe(new SingleObserver<List<FileData>>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
            }

            @Override
            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<FileData> listSort) {
                list.clear();
                list.addAll(listSort);
                filter(filter);
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                e.printStackTrace();
            }
        });

    }

    private void sortByName(boolean desc, String filter) {
        fileDataRepository.sortFileMediaByName(listTemp, desc).subscribe(new SingleObserver<List<FileData>>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
            }

            @Override
            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<FileData> listSort) {
                list.clear();
                list.addAll(listSort);
                filter(filter);
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                e.printStackTrace();
            }
        });

    }

    private void sortByType(boolean desc, String filter) {
        fileDataRepository.sortFileMediaByType(listTemp, desc).subscribe(new SingleObserver<List<FileData>>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
            }

            @Override
            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<FileData> listSort) {
                list.clear();
                list.addAll(listSort);
                filter(filter);
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                e.printStackTrace();
            }
        });

    }

    private void sortBySize(boolean desc, String filter) {
        fileDataRepository.sortFileMediaBySize(listTemp, desc, context).subscribe(new SingleObserver<List<FileData>>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
            }

            @Override
            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<FileData> listSort) {
                list.clear();
                list.addAll(listSort);
                filter(filter);
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                e.printStackTrace();
            }
        });

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (typePresentationView == Constant.SORT_VIEW_DETAIL || typePresentationView == Constant.SORT_VIEW_COMPACT) {
            ItemRecentFileBinding binding = ItemRecentFileBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new FileDataViewHolder(binding, fileDataRepository, typePresentationView);
        } else {
            if (viewType == TYPE_ITEM) {
                ItemFilePhotoBinding binding = ItemFilePhotoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new PhotoViewHolder(binding);
            } else {
                ItemDatePhotoBinding binding = ItemDatePhotoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new TimeViewHolder(binding);
            }
        }
    }

    private void spaceItem(View view, int position) {
        if (position == list.size() - 1) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }
    private void spaceItem(View view, int position, FileData fileData) {
        if (position == list.size() - 1) {
            if (fileData.getType() == TYPE_DROP_BOX_FILE || fileData.getType() == TYPE_GG_DRIVE_FILE || fileData.getType() == TYPE_ONE_DRIVE_FILE) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
        } else {
            view.setVisibility(View.GONE);
        }
    }
    @Override
    public int getItemViewType(int position) {
        return listPhoto.get(position).getType();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FileDataViewHolder) {
            if (typePresentationView == Constant.SORT_VIEW_DETAIL || typePresentationView == Constant.SORT_VIEW_COMPACT) {
                ((FileDataViewHolder) holder).binding.viewSpace.setVisibility(View.GONE);
            }
        }

        if (typePresentationView == Constant.SORT_VIEW_DETAIL || typePresentationView == Constant.SORT_VIEW_COMPACT) {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (holder.getItemViewType() == TYPE_ITEM) {
                FileData fileData = listPhoto.get(holder.getAdapterPosition());
                ((FileDataViewHolder) holder).setData(fileData);
                ((FileDataViewHolder) holder).binding.imgItemCheck.setVisibility(View.VISIBLE);
                ((FileDataViewHolder) holder).binding.ivCancel.setVisibility(View.GONE);

                ((FileDataViewHolder) holder).binding.imgItemCheck.checkClickItem(checkItemSelected(holder.getAdapterPosition()));//setImageResource(R.drawable.ic_check_true);

                eventViewHolder(((FileDataViewHolder) holder), fileData, ((FileDataViewHolder) holder).binding.imgItemCheck);
                ((FileDataViewHolder) holder).binding.cslItemRecentFile.setOnClickListener(view -> {
                            if (Utils.checkLongTime())
                                onItemClickedListener.onItemClick(holder.getAdapterPosition(), fileData);
                        }
                );
                ((FileDataViewHolder) holder).binding.cslItemRecentFile.setPadding(0,
                        (int) context.getResources().getDimension(R.dimen.margin_13),
                        0,
                        (int) context.getResources().getDimension(R.dimen.margin_13));
            } else {
                params.height = 0;
                ((FileDataViewHolder) holder).binding.cslItemRecentFile.setPadding(0,
                        0,
                        0,
                        0);
            }
            holder.itemView.setLayoutParams(params);
        } else {
            if (holder.getItemViewType() == TYPE_TEXT_TIME) {
                ((TimeViewHolder) holder).bind(listPhoto.get(holder.getAdapterPosition()));
            } else {
                ((PhotoViewHolder) holder).bind(listPhoto.get(holder.getAdapterPosition()));
            }
        }
    }

    private void eventViewHolder(FileDataViewHolder holder, FileData fileData, ImageView imgItemCheck) {
        imgItemCheck.setOnClickListener(v -> {
            onItemClickedListener.onItemCheckClick(holder.getAdapterPosition(), fileData);
            if (checkItemSelected(holder.getAdapterPosition())) {
                listSelect.remove(fileData.getFilePath());

            } else {
                listSelect.add(fileData.getFilePath());
            }
            notifyItemChanged(holder.getAdapterPosition());
        });
    }

    private boolean checkItemSelected(int position) {
        for (int i = 0; i < listSelect.size(); i++) {
            if (listSelect.get(i).equals(listPhoto.get(position).getFilePath())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return listPhoto.size();
    }

    public void setOnItemClickedListener(OnItemPhotoClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }

    public interface OnItemPhotoClickedListener {
        void onItemClick(int path, FileData dataItem);

        void onItemCheckClick(int path, FileData dataItem);
    }

    public class TimeViewHolder extends RecyclerView.ViewHolder {
        ItemDatePhotoBinding binding;

        public TimeViewHolder(@NonNull ItemDatePhotoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(FileData fileData) {
            binding.tvDate.setText(fileData.getFileName());
        }
    }


    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        ItemFilePhotoBinding binding;

        public PhotoViewHolder(@NonNull ItemFilePhotoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(FileData fileData) {
            String filePath = fileData.getFilePath();
            String fileName = fileData.getFileName();
            long duration = fileData.getDurationVideo();
            if (duration > 0) {
                binding.tvTimeVideo.setVisibility(View.VISIBLE);
                binding.tvTimeVideo.setText(Utils.convertDuration(duration));
            } else binding.tvTimeVideo.setVisibility(View.GONE);
            binding.tvContentPhoto.setText(fileName);

            int drawableError;
            if (fileDataRepository.isVideoFile(filePath)) {
                drawableError = R.drawable.ic_video_default;
            } else {
                drawableError = R.drawable.ic_image_default;
            }
            Glide.with(itemView.getContext()).asBitmap().error(drawableError).load(filePath).into(binding.ivPhoto);

            if (checkItemSelected(getPositionAdapter())) {
                binding.ivCheckPhoto.setImageResource(R.drawable.ic_check_true);
            } else {
                binding.ivCheckPhoto.setImageResource(R.drawable.ic_check_false_photo);
            }
            binding.viewClick.setOnClickListener(v -> {
                if (Utils.checkLongTime())
                    onItemClickedListener.onItemClick(getPositionAdapter(), listPhoto.get(getPositionAdapter()));
            });

            binding.ivCheckPhoto.setOnClickListener(v -> {
                if (!Utils.checkTimeShort()) return;
                if (checkItemSelected(getPositionAdapter())) {
                    listSelect.remove(listPhoto.get(getPositionAdapter()).getFilePath());
                } else {
                    listSelect.add(listPhoto.get(getPositionAdapter()).getFilePath());
                }
                notifyItemChanged(getPositionAdapter());
                onItemClickedListener.onItemCheckClick(getPositionAdapter(), listPhoto.get(getPositionAdapter()));
            });
        }

        private int getPositionAdapter() {
            return Math.max(0, getAdapterPosition());
        }
    }
}
