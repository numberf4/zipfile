package vn.tapbi.zazip.ui.adapter;

import static vn.tapbi.zazip.common.Constant.TYPE_DROP_BOX_FILE;
import static vn.tapbi.zazip.common.Constant.TYPE_GG_DRIVE_FILE;
import static vn.tapbi.zazip.common.Constant.TYPE_ONE_DRIVE_FILE;
import static vn.tapbi.zazip.ui.cloud.drive.DriveServiceHelper.TYPE_GOOGLE_DRIVE_FOLDER;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dropbox.core.v2.files.FileMetadata;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;
import vn.tapbi.zazip.App;
import vn.tapbi.zazip.common.Constant;
import vn.tapbi.zazip.data.model.FileData;
import vn.tapbi.zazip.data.repository.FileDataRepository;
import vn.tapbi.zazip.databinding.ItemGridViewBinding;
import vn.tapbi.zazip.databinding.ItemRecentFileBinding;
import vn.tapbi.zazip.ui.adapter.holder.FileDataViewHolder;
import vn.tapbi.zazip.ui.custom.CustomClickItem;
import vn.tapbi.zazip.utils.ComparatorVietnamese;
import vn.tapbi.zazip.utils.Utils;

public class FileDataAdapter extends RecyclerView.Adapter<FileDataViewHolder> {
    public FileDataRepository fileDataRepository;
    public FileDataAdapter.OnItemClickedListener onItemClickedListener;
    public List<FileData> list = new ArrayList<>();
    public List<FileData> listSearch = new ArrayList<>();
    public List<String> listSelect = new ArrayList<>();

    private List<FileData> listFolder = new LinkedList<>();
    private List<FileData> listFile = new LinkedList<>();
    private int type, typePresentationView;
    private Picasso mPicasso;
    private boolean checkEventShare = false;
    private boolean checkClickFolder = false;
    private String pathBrowse = "";
    private boolean isShowAll = true;
    private boolean checkTickChoose = false;
    private List<FileMetadata> fileMetadataList = new ArrayList<>();
    private ComparatorVietnamese cViet = new ComparatorVietnamese();

    public void setFileDataRepository(FileDataRepository fileDataRepository) {
        this.fileDataRepository = fileDataRepository;
    }

    public void setTypePresentationView(int typePresentationView) {
        this.typePresentationView = typePresentationView;
    }

    public void setTypeSortView(int typeSortView, boolean sortDesc, String filter) {
        sortView(typeSortView, sortDesc);
        filter(filter);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void changeShowAll(boolean isShowAll) {
        this.isShowAll = isShowAll;
        notifyDataSetChanged();
    }

    public List<FileMetadata> getFileMetadataList() {
        return fileMetadataList;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFileMetadataList(List<FileMetadata> fileMetadataList) {
        this.fileMetadataList = fileMetadataList;
        notifyDataSetChanged();
    }

    public boolean isCheckEventShare() {
        return checkEventShare;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setCheckEventShare(boolean checkEventShare) {
        this.checkEventShare = checkEventShare;
        notifyDataSetChanged();
    }

    public Picasso getPicasso() {
        return mPicasso;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setPicasso(Picasso mPicasso) {
        this.mPicasso = mPicasso;
        notifyDataSetChanged();
    }

    private void splitList(List<FileData> list) {
        listFolder.clear();
        listFile.clear();
        for (FileData fileData : list) {
            if (fileData != null){
                if (fileData.isFolder()) {
                    listFolder.add(fileData);
                } else listFile.add(fileData);
            }
        }
    }

    private void sortView(int typeSortView, boolean sortDesc) {
        if (typeSortView == Constant.SORT_VIEW_TYPE_NAME) {
            sortByName(listFolder, listFile, sortDesc);
        } else if (typeSortView == Constant.SORT_VIEW_TYPE_DATE) {
            sortByDate(listFolder, listFile, sortDesc);
        } else if (typeSortView == Constant.SORT_VIEW_TYPE_TYPE) {
            sortByType(listFolder, listFile, sortDesc);
        } else if (typeSortView == Constant.SORT_VIEW_TYPE_SIZE) {
            sortBySize(listFolder, listFile, sortDesc);
        }
    }

    private void sortBySize(List<FileData> listFolder, List<FileData> listFile, boolean sortDesc) {
        if (!sortDesc) {
            Collections.sort(listFolder, new Comparator<FileData>() {
                @Override
                public int compare(FileData o1, FileData o2) {
                    return o1.getFileName().toLowerCase(Locale.ROOT).compareTo(o2.getFileName().toLowerCase(Locale.ROOT));
                }
            });
            Collections.sort(listFile, new Comparator<FileData>() {
                @Override
                public int compare(FileData o1, FileData o2) {
                    return Long.compare(o2.getFileSize(), o1.getFileSize());
                }
            });
        } else {
            Collections.sort(listFolder, new Comparator<FileData>() {
                @Override
                public int compare(FileData o1, FileData o2) {
                    return o2.getFileName().toLowerCase(Locale.ROOT).compareTo(o1.getFileName().toLowerCase(Locale.ROOT));
                }
            });
            Collections.sort(listFile, new Comparator<FileData>() {
                @Override
                public int compare(FileData o1, FileData o2) {
                    return Long.compare(o1.getFileSize(), o2.getFileSize());
                }
            });
        }
        list.clear();
        list.addAll(listFolder);
        list.addAll(listFile);
    }

    private void sortByType(List<FileData> listFolder, List<FileData> listFile, boolean sortDesc) {
        if (!sortDesc) {
            Collections.sort(listFolder, new Comparator<FileData>() {
                @Override
                public int compare(FileData o1, FileData o2) {
                    return cViet.generator(o1.getFileName()).compareTo(cViet.generator(o2.getFileName()));
                }
            });
            Collections.sort(listFile, new Comparator<FileData>() {
                @Override
                public int compare(FileData o1, FileData o2) {
                    return cViet.generator(Utils.getMimeTypeFromName(o1.getFileName())).compareTo(cViet.generator(Utils.getMimeTypeFromName(o2.getFileName())));
                }
            });

        } else {
            Collections.sort(listFolder, new Comparator<FileData>() {
                @Override
                public int compare(FileData o1, FileData o2) {
                    return cViet.generatorDesc(o1.getFileName()).compareTo(cViet.generatorDesc(o2.getFileName()));
                }
            });
            Collections.sort(listFile, new Comparator<FileData>() {
                @Override
                public int compare(FileData o1, FileData o2) {
                    return cViet.generatorDesc(Utils.getMimeTypeFromName(o1.getFileName())).compareTo(cViet.generatorDesc(Utils.getMimeTypeFromName(o2.getFileName())));
                }
            });

        }
        list.clear();
        list.addAll(listFolder);
        list.addAll(listFile);
    }

    private void sortByDate(List<FileData> listFolder, List<FileData> listFile, boolean sortDesc) {
        if (!sortDesc) {
            Collections.sort(listFolder, new Comparator<FileData>() {
                @Override
                public int compare(FileData o1, FileData o2) {
                    return Long.compare(o2.getFileTime(), o1.getFileTime());
                }
            });
            Collections.sort(listFile, new Comparator<FileData>() {
                @Override
                public int compare(FileData o1, FileData o2) {
                    return Long.compare(o2.getFileTime(), o1.getFileTime());
                }
            });
        } else {
            Collections.sort(listFolder, new Comparator<FileData>() {
                @Override
                public int compare(FileData o1, FileData o2) {
                    return Long.compare(o1.getFileTime(), o2.getFileTime());
                }
            });
            Collections.sort(listFile, new Comparator<FileData>() {
                @Override
                public int compare(FileData o1, FileData o2) {
                    return Long.compare(o1.getFileTime(), o2.getFileTime());
                }
            });
        }

        list.clear();
        list.addAll(listFolder);
        list.addAll(listFile);
    }

    private void sortByName(List<FileData> listFolder, List<FileData> listFile, boolean sortDesc) {
        if (!sortDesc) {
            Collections.sort(listFolder, new Comparator<FileData>() {
                @Override
                public int compare(FileData o1, FileData o2) {
                    return cViet.generator(o1.getFileName()).compareTo(cViet.generator(o2.getFileName()));
                }
            });
            Collections.sort(listFile, new Comparator<FileData>() {
                @Override
                public int compare(FileData o1, FileData o2) {
                    return cViet.generator(o1.getFileName()).compareTo(cViet.generator(o2.getFileName()));
                }
            });

        } else {
            Collections.sort(listFolder, new Comparator<FileData>() {
                @Override
                public int compare(FileData o1, FileData o2) {
                    return cViet.generatorDesc(o1.getFileName()).compareTo(cViet.generatorDesc(o2.getFileName()));
                }
            });
            Collections.sort(listFile, new Comparator<FileData>() {
                @Override
                public int compare(FileData o1, FileData o2) {
                    return cViet.generatorDesc(o1.getFileName()).compareTo(cViet.generatorDesc(o2.getFileName()));
                }
            });
        }

        list.clear();
        list.addAll(listFolder);
        list.addAll(listFile);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void resetPathBrowse() {
        pathBrowse = "";
        notifyDataSetChanged();
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

    public void setList(List<FileData> list) {
        this.list = list;
        splitList(this.list);
        filter("");
    }


    public void setOnlyList(List<FileData> list) {
        this.list = list;
        splitList(this.list);
    }

    public void resetAllSelect() {
        if (listSelect.size() > 0 && listSearch.size() > 0) {
            for (int i = listSearch.size() - 1; i >= 0; i--) {
                for (int j = 0; j < listSelect.size(); j++) {
                    if (listSelect.get(j).equals(listSearch.get(i).getFilePath())) {
                        listSelect.remove(j);
                        notifyItemChanged(i);
                    }
                }
            }
        }
    }

    public void resetList(String filePath) {
        if (listSearch.size() > 0 && listSelect.size() > 0) {
            for (int i = listSelect.size() - 1; i >= 0; i--) {
                for (int j = 0; j < listSearch.size(); j++) {
                    if (listSelect.get(i).equals(filePath) && listSearch.get(j).getFilePath().equals(filePath)) {
                        listSelect.remove(i);
                        notifyItemChanged(j);
                    }
                }
            }
        }
    }

    public List<FileData> getList() {
        return list;
    }

    public FileDataAdapter(int type) {
        this.type = type;
    }

    public List<FileData> getListSearch() {
        return listSearch;
    }

    //    @SuppressLint("NotifyDataSetChanged")
    public void notifyChanged() {
        notifyDataSetChanged();
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault()).trim();
        listSearch.clear();
        if (charText.length() == 0) {
            listSearch.addAll(list);
        } else {
            for (FileData fileData : list) {
                if (fileData.getFileName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    listSearch.add(fileData);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FileDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecentFileBinding binding = ItemRecentFileBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ItemGridViewBinding gridViewBinding = ItemGridViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        if (typePresentationView == Constant.SORT_VIEW_DETAIL || typePresentationView == Constant.SORT_VIEW_COMPACT) {
            return new FileDataViewHolder(binding, fileDataRepository, typePresentationView);
        } else/* if (typePresentationView == Constant.SORT_VIEW_GRID) */ {
            return new FileDataViewHolder(gridViewBinding, fileDataRepository, typePresentationView);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull FileDataViewHolder holder, int position) {
        FileData fileData = listSearch.get(Math.max(holder.getAdapterPosition(), 0));
        if (typePresentationView == Constant.SORT_VIEW_DETAIL || typePresentationView == Constant.SORT_VIEW_COMPACT) {
            spaceItem(holder.binding.viewSpace, position, fileData);
        } else {
            spaceItem(holder.bindingGrid.viewSpace, position, fileData);
        }
        setUpViewHolder(holder, fileData);
    }

    private void spaceItem(View view, int position, FileData fileData) {
        if (position == listSearch.size() - 1) {
            if (fileData.getType() == TYPE_DROP_BOX_FILE || fileData.getType() == TYPE_GG_DRIVE_FILE || fileData.getType() == TYPE_ONE_DRIVE_FILE) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
        } else {
            view.setVisibility(View.GONE);
        }
    }

    public void clearListSelectDownload() {
        if (App.getInstance().listSelectCloud.size() > 0) {
            Timber.d("clearListSelectDownload");
            App.getInstance().listSelectCloud.clear();
            checkTickChoose = true;
            notifyDataSetChanged();
        }
    }

    private void setUpViewHolder(FileDataViewHolder holder, FileData fileData) {
        if (App.getInstance().listSelectCloud.size() > 0) {
            for (int i = 0; i < App.getInstance().listSelectCloud.size(); i++) {
                if (App.getInstance().listSelectCloud.get(i).getFileId().equals(fileData.getFileId()) && App.getInstance().listSelectCloud.get(i).isChecked()) {
                    fileData.setChecked(true);
                }
            }
        } else {
            fileData.setChecked(false);
        }
        String category = fileData.getCategory();
        if ((category == null || category.isEmpty())) {
            category = fileData.getFileName().substring(fileData.getFileName().lastIndexOf(".") + 1);
        }

        if (fileData.getType() == TYPE_DROP_BOX_FILE) {
            holder.setPicasso(mPicasso);
            holder.setMetadataList(fileMetadataList);
        }
        holder.setData(fileData);

        if (typePresentationView == Constant.SORT_VIEW_DETAIL || typePresentationView == Constant.SORT_VIEW_COMPACT) {

            holder.binding.cslItemRecentFile.setEnabled(!checkEventShare);
            holder.binding.imgItemCheck.setCheckBitmap(false);
            if (fileData.isFolder() && (fileData.getType() == TYPE_ONE_DRIVE_FILE || fileData.getType() == TYPE_DROP_BOX_FILE || fileData.getType() == TYPE_GG_DRIVE_FILE)) {
                holder.binding.imgItemCheck.setVisibility(View.GONE);
            } else {
                holder.binding.imgItemCheck.setVisibility(View.VISIBLE);
            }
            holder.binding.ivCancel.setVisibility(View.GONE);
            if (type == Constant.TYPE_MULTI) {
                holder.binding.imgItemCheck.checkClickItem(checkItemSelected(holder.getAdapterPosition()));//setImageResource(R.drawable.ic_check_true);
                eventViewHolder(holder, fileData, holder.binding.imgItemCheck);
            } else if (type == Constant.TYPE_SINGLE) {
                holder.binding.imgItemCheck.checkClickItem(pathBrowse.equals(fileData.getFilePath()));
                holder.binding.imgItemCheck.setOnClickListener(v -> {
                    int p = getPositionOld();
                    pathBrowse = fileData.getFilePath();
                    notifyItemChanged(p);
                    notifyItemChanged(holder.getAdapterPosition());
                    onItemClickedListener.onItemClick(holder.getAdapterPosition(), fileData);
                });
            }
            String finalCategory = category;
            holder.binding.cslItemRecentFile.setOnClickListener(view -> {
                if (fileData.getType() == TYPE_ONE_DRIVE_FILE || fileData.getType() == TYPE_DROP_BOX_FILE || fileData.getType() == TYPE_GG_DRIVE_FILE) {
                    if (finalCategory.equals(TYPE_GOOGLE_DRIVE_FOLDER)) {
                        if (fileData.getType() == TYPE_DROP_BOX_FILE) {
                            onItemClickedListener.clickFolderCloud(holder.getAdapterPosition(), fileData.getPathDisplayDropBox());
                        } else {
                            onItemClickedListener.clickFolderCloud(holder.getAdapterPosition(), fileData.getFilePath());
                        }
                    } else {
                        holder.binding.imgItemCheck.checkClickItem(!holder.binding.imgItemCheck.getCheckClick());
                        addItemSelectToDownload(holder.binding.imgItemCheck, fileData);
                        onItemClickedListener.onItemClick(holder.getAdapterPosition(), fileData);
                    }
                } else {
                    onItemClickedListener.onFolderDeviceClick(fileData.getFilePath(), true);
                }
                checkTickChoose = false;
            });
            if (fileData.isChecked()) {
                holder.binding.imgItemCheck.checkClickItem(true);
            }
            if (checkTickChoose) {
                holder.binding.imgItemCheck.checkClickItem(false);
            }
        } else if (typePresentationView == Constant.SORT_VIEW_GRID) {
            holder.bindingGrid.cslItemGridView.setEnabled(!checkEventShare);
            holder.bindingGrid.ivCheckItemGrid.setCheckBitmap(true);
            holder.bindingGrid.ivCheckItemGrid.checkClickItem(checkItemSelected(holder.getAdapterPosition()));//setImageResource(R.drawable.ic_check_fill_png);
            if (fileData.isFolder() && (fileData.getType() == TYPE_ONE_DRIVE_FILE || fileData.getType() == TYPE_DROP_BOX_FILE || fileData.getType() == TYPE_GG_DRIVE_FILE)) {
                holder.bindingGrid.ivCheckItemGrid.setVisibility(View.GONE);
            } else {
                holder.bindingGrid.ivCheckItemGrid.setVisibility(View.VISIBLE);
            }
            eventViewHolder(holder, fileData, holder.bindingGrid.ivCheckItemGrid);
            String finalCategory1 = category;
            holder.bindingGrid.cslItemGridView.setOnClickListener(v -> {
                if (fileData.getType() == TYPE_ONE_DRIVE_FILE || fileData.getType() == TYPE_DROP_BOX_FILE || fileData.getType() == TYPE_GG_DRIVE_FILE) {
                    if (finalCategory1.equals(TYPE_GOOGLE_DRIVE_FOLDER)) {
                        if (fileData.getType() == TYPE_DROP_BOX_FILE) {
                            onItemClickedListener.clickFolderCloud(holder.getAdapterPosition(), fileData.getPathDisplayDropBox());
                        } else {
                            onItemClickedListener.clickFolderCloud(holder.getAdapterPosition(), fileData.getFilePath());
                        }
                    } else {
                        holder.bindingGrid.ivCheckItemGrid.checkClickItem(!holder.bindingGrid.ivCheckItemGrid.getCheckClick());
                        addItemSelectToDownload(holder.bindingGrid.ivCheckItemGrid, fileData);
                        onItemClickedListener.onItemClick(holder.getAdapterPosition(), fileData);
                    }
                } else {
                    onItemClickedListener.onFolderDeviceClick(fileData.getFilePath(), true);
                }
                checkTickChoose = false;
            });

            if (fileData.isChecked()) {
                holder.bindingGrid.ivCheckItemGrid.checkClickItem(true);
            }
            if (checkTickChoose) {
                holder.bindingGrid.ivCheckItemGrid.checkClickItem(false);
            }
        }
    }

    private void addItemSelectToDownload(CustomClickItem customClickItem, FileData fileData) {
        if (customClickItem.getCheckClick()) {
            fileData.setChecked(true);
            App.getInstance().listSelectCloud.add(fileData);
        } else {
            fileData.setChecked(false);
            for (int i = 0; i < App.getInstance().listSelectCloud.size(); i++) {
                if (App.getInstance().listSelectCloud.get(i).getFileId().equals(fileData.getFileId())) {
                    App.getInstance().listSelectCloud.remove(i);
                    break;
                }
            }
        }
    }

    private void eventViewHolder(FileDataViewHolder holder, FileData fileData, CustomClickItem imgItemCheck) {
        imgItemCheck.setOnClickListener(v -> {
            onItemClickedListener.onItemClick(holder.getAdapterPosition(), fileData);
            if (fileData.getType() != TYPE_ONE_DRIVE_FILE && fileData.getType() != TYPE_DROP_BOX_FILE && fileData.getType() != TYPE_GG_DRIVE_FILE) {
                imgItemCheck.checkClickItem(!checkItemSelected(holder.getAdapterPosition()));
                if (checkItemSelected(holder.getAdapterPosition())) {
                    listSelect.remove(fileData.getFilePath());
                } else {
                    listSelect.add(fileData.getFilePath());
                }
            } else {
                imgItemCheck.checkClickItem(!imgItemCheck.getCheckClick());
                addItemSelectToDownload(imgItemCheck, fileData);
                onItemClickedListener.onItemClick(holder.getAdapterPosition(), fileData);
            }
        });
    }

    private int getPositionOld() {
        for (int i = 0; i < listSearch.size(); i++) {
            if (listSearch.get(i).getFilePath().equals(pathBrowse)) {
                return i;
            }
        }
        return -1;
    }

    private boolean checkItemSelected(int position) {
        for (int i = 0; i < listSelect.size(); i++) {
            if(position>=0) {
                if (listSelect.get(i).equals(listSearch.get(position).getFilePath())) {
                    return true;
                }
            }else{
                return false;
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return listSearch == null ? 0 : isShowAll ? listSearch.size() : Math.min(10, listSearch.size());
    }

    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }

    public interface OnItemClickedListener {
        void onItemClick(int position, FileData fileData);

        void onFolderDeviceClick(String path, boolean isClickFolder);

        void clickFolderCloud(int position, String path);


    }
}