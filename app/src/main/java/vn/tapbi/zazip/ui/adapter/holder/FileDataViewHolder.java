package vn.tapbi.zazip.ui.adapter.holder;

import static vn.tapbi.zazip.common.Constant.DOCUMENT_DOC_X;
import static vn.tapbi.zazip.common.Constant.DOCUMENT_PDF;
import static vn.tapbi.zazip.common.Constant.DOCUMENT_POT;
import static vn.tapbi.zazip.common.Constant.DOCUMENT_POTX;
import static vn.tapbi.zazip.common.Constant.DOCUMENT_PPS;
import static vn.tapbi.zazip.common.Constant.DOCUMENT_PPSX;
import static vn.tapbi.zazip.common.Constant.DOCUMENT_PPT;
import static vn.tapbi.zazip.common.Constant.DOCUMENT_PPTM;
import static vn.tapbi.zazip.common.Constant.DOCUMENT_PPTX;
import static vn.tapbi.zazip.common.Constant.DOCUMENT_TEXT;
import static vn.tapbi.zazip.common.Constant.DOCUMENT_TXT;
import static vn.tapbi.zazip.common.Constant.DOCUMENT_XLS;
import static vn.tapbi.zazip.common.Constant.DOCUMENT_XLSM;
import static vn.tapbi.zazip.common.Constant.DOCUMENT_XLTX;
import static vn.tapbi.zazip.common.Constant.DOCUMENT_XLXS;
import static vn.tapbi.zazip.common.Constant.TYPE_APK;
import static vn.tapbi.zazip.common.Constant.TYPE_DROP_BOX_FILE;
import static vn.tapbi.zazip.common.Constant.TYPE_GG_DRIVE_FILE;
import static vn.tapbi.zazip.common.Constant.TYPE_ONE_DRIVE_FILE;
import static vn.tapbi.zazip.ui.cloud.drive.DriveServiceHelper.TYPE_GOOGLE_DRIVE_FOLDER;
import static vn.tapbi.zazip.utils.Utils.convertByte;
import static vn.tapbi.zazip.utils.Utils.getDate;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.dropbox.core.v2.files.FileMetadata;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import vn.tapbi.zazip.App;
import vn.tapbi.zazip.R;
import vn.tapbi.zazip.common.Constant;
import vn.tapbi.zazip.data.model.FileData;
import vn.tapbi.zazip.data.repository.FileDataRepository;
import vn.tapbi.zazip.databinding.ItemGridViewBinding;
import vn.tapbi.zazip.databinding.ItemRecentFileBinding;
import vn.tapbi.zazip.utils.FileThumbnailRequestHandler;
import vn.tapbi.zazip.utils.Utils;

public class FileDataViewHolder extends RecyclerView.ViewHolder {
    public ItemGridViewBinding bindingGrid;
    public ItemRecentFileBinding binding;
    private Picasso mPicasso;
    FileDataRepository fileDataRepository;
    private List<FileMetadata> metadataList = new ArrayList<>();
    private int typePresentation;

    public FileDataViewHolder(ItemRecentFileBinding binding, FileDataRepository fileDataRepository, int typePresentation) {
        super(binding.getRoot());
        this.binding = binding;
        this.typePresentation = typePresentation;
        this.fileDataRepository = fileDataRepository;
    }

    public FileDataViewHolder(ItemGridViewBinding binding, FileDataRepository fileDataRepository, int typePresentation) {
        super(binding.getRoot());
        this.bindingGrid = binding;
        this.typePresentation = typePresentation;
        this.fileDataRepository = fileDataRepository;
    }

    public List<FileMetadata> getMetadataList() {
        return metadataList;
    }

    public void setMetadataList(List<FileMetadata> metadataList) {
        this.metadataList = metadataList;
    }

    public Picasso getPicasso() {
        return mPicasso;
    }

    public void setPicasso(Picasso mPicasso) {
        this.mPicasso = mPicasso;
    }

    public void setData(FileData fileData) {
        String filePath = fileData.getFilePath();
        String fileName = fileData.getFileName();
        if (typePresentation == Constant.SORT_VIEW_DETAIL) {
            binding.tvNameFileCompact.setVisibility(View.GONE);
            binding.tvTitle.setVisibility(View.VISIBLE);
            binding.tvContent.setVisibility(View.VISIBLE);
            binding.tvTitle.setText(fileName);
            setUpView(fileName, filePath, fileData, binding.imgItemRecent, binding.tvContent);

        } else if (typePresentation == Constant.SORT_VIEW_COMPACT) {
            binding.tvTitle.setVisibility(View.GONE);
            binding.tvContent.setVisibility(View.GONE);
            binding.tvNameFileCompact.setVisibility(View.VISIBLE);
            binding.tvNameFileCompact.setText(fileName);
            setUpView(fileName, filePath, fileData, binding.imgItemRecent, binding.tvContent);

        } else if (typePresentation == Constant.SORT_VIEW_GRID) {
            setUpViewGrid(fileName, filePath, fileData, bindingGrid.ivItemFile, bindingGrid.tvNameFileGrid);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setUpView(String fileName, String filePath, FileData fileData, final ImageView imgItemRecent, TextView tvContent) {
        String category = fileData.getCategory();
        if (category == null || category.isEmpty()) {
            category = fileData.getFileName().substring(fileData.getFileName().lastIndexOf(".") + 1);
        }
        imgItemRecent.setImageBitmap(null);
        if (fileData.isFolder()) {
            binding.imgBtnVideo.setVisibility(View.INVISIBLE);
            setImageForView(R.drawable.ic_folder, imgItemRecent);
            if (category.equals(TYPE_GOOGLE_DRIVE_FOLDER)) {
                if (fileData.getType() == TYPE_ONE_DRIVE_FILE) {
                    tvContent.setText(convertByte(fileData.getFileSize()) + " . " +
                            fileData.getCountFolder() + " file . " + fileData.getTime());
                } else if (fileData.getType() == TYPE_GG_DRIVE_FILE) {
                    tvContent.setText(fileData.getTime());
                } else {
                    tvContent.setVisibility(View.GONE);
                }
            } else {
                tvContent.setText(fileData.getCountItem() + Constant.ITEMSS + " . " + getDate(fileData.getFileTime()));
            }
        } else {
            if (fileData.getType() == TYPE_ONE_DRIVE_FILE || fileData.getType() == TYPE_DROP_BOX_FILE || fileData.getType() == TYPE_GG_DRIVE_FILE) {
                tvContent.setText(convertByte(fileData.getFileSize()) + " . "
                        + fileData.getTime());
            } else {
                tvContent.setText(convertByte(fileData.getFolderSize()) + " . " + getDate(fileData.getFileTime()));
            }

            if (fileName.endsWith(Constant.DOCUMENT_DOC) || Utils.isDocFiles(category)) {
                setImageForView(R.drawable.ic_word_file, imgItemRecent);
                binding.imgBtnVideo.setVisibility(View.INVISIBLE);
            } else if (fileName.endsWith(DOCUMENT_DOC_X) || Utils.isDocFiles(category)) {
                setImageForView(R.drawable.ic_word, imgItemRecent);
                binding.imgBtnVideo.setVisibility(View.INVISIBLE);
            } else if (fileName.endsWith(DOCUMENT_PDF) || Utils.isPdfFiles(category)) {
                setImageForView(R.drawable.ic_pdf_file, imgItemRecent);
                binding.imgBtnVideo.setVisibility(View.INVISIBLE);
            } else if (fileName.endsWith(TYPE_APK)) {
                Glide.with(itemView.getContext()).load(filePath).into(imgItemRecent);
                binding.imgBtnVideo.setVisibility(View.INVISIBLE);
            } else if (fileName.endsWith(DOCUMENT_TXT) || fileName.endsWith(DOCUMENT_TEXT) || Utils.isTxtFiles(category)) {
                setImageForView(R.drawable.ic_txt_file, imgItemRecent);
                binding.imgBtnVideo.setVisibility(View.INVISIBLE);
            } else if (fileName.endsWith(DOCUMENT_PPT) || fileName.endsWith(DOCUMENT_PPSX)
                    || fileName.endsWith(DOCUMENT_PPTX) || fileName.endsWith(DOCUMENT_PPS)
                    || fileName.endsWith(DOCUMENT_POT) || fileName.endsWith(DOCUMENT_POTX)
                    || fileName.endsWith(DOCUMENT_PPTM) || Utils.isPptFiles(category)) {
                setImageForView(R.drawable.ic_ppt_file, imgItemRecent);
                binding.imgBtnVideo.setVisibility(View.INVISIBLE);
            } else if (fileName.endsWith(DOCUMENT_XLXS) || fileName.endsWith(DOCUMENT_XLSM)
                    || fileName.endsWith(DOCUMENT_XLS) || fileName.endsWith(DOCUMENT_XLTX) || Utils.isExcelFiles(category)) {
                setImageForView(R.drawable.ic_xlxs_file, imgItemRecent);
                binding.imgBtnVideo.setVisibility(View.INVISIBLE);
            } else if (Utils.checkFileArchive(fileName)) {
                setImageForView(R.drawable.ic_archive_file, imgItemRecent);
                binding.imgBtnVideo.setVisibility(View.INVISIBLE);
            } else if (Utils.isMusicFile(fileName)) {
                setImageForView(R.drawable.ic_mp3_file, imgItemRecent);
                binding.imgBtnVideo.setVisibility(View.INVISIBLE);
            } else if (Utils.isPhotoFile(fileName)) {
                if (fileData.getType() == TYPE_DROP_BOX_FILE) {
                    showImageDropBox(fileData, imgItemRecent, R.drawable.ic_image_default);
                } else {
                    setImageForImageView(R.drawable.ic_image_default, imgItemRecent, filePath);
                }
                binding.imgBtnVideo.setVisibility(View.INVISIBLE);
            } else if (Utils.isVideoFile(fileName)) {
                if (fileData.getType() == TYPE_DROP_BOX_FILE) {
                    showImageDropBox(fileData, imgItemRecent, R.drawable.ic_video_default);
                } else {
                    setImageForImageView(R.drawable.ic_video_default, imgItemRecent, filePath);
                }
                binding.imgBtnVideo.setVisibility(View.VISIBLE);

            } else {

                binding.imgBtnVideo.setVisibility(View.INVISIBLE);
                setImageForImageView(R.drawable.ic_noname_file, imgItemRecent, filePath);
            }
        }

    }

    private void setImageForImageView(int drawable, ImageView imgItemRecent, String path) {
        Glide.with(App.getInstance().getApplicationContext()).asBitmap().load(path)
                .placeholder(drawable)
                .error(drawable)
                .centerCrop()
                .into(imgItemRecent);
    }

    private void setImageForView(int drawable, ImageView imgItemRecent) {
        Glide.with(App.getInstance().getApplicationContext()).asBitmap().load(drawable)
                .placeholder(drawable)
                .error(drawable)
                .centerCrop()
                .into(imgItemRecent);
    }

    private void showImageDropBox(FileData fileData, ImageView imgItemRecent, int drawable) {
        for (int i = 0; i < metadataList.size(); i++) {
            if (metadataList.get(i) != null) {
                if (metadataList.get(i).getName().equals(fileData.getFileName())) {
                    mPicasso.load(FileThumbnailRequestHandler.buildPicassoUri((FileMetadata) metadataList.get(i)))
                            .resize(200, 200)
                            .placeholder(drawable)
                            .error(drawable)
                            .centerCrop()
                            .into(imgItemRecent);
                }
            }
        }
    }

    private void setUpViewGrid(String fileName, String filePath, FileData fileData, ImageView imgItemRecent, TextView tvContent) {
        String category = fileData.getCategory();
        if (category == null || category.isEmpty()) {
            category = fileData.getFilePath().substring(fileData.getFilePath().lastIndexOf(".") + 1);
        }
        if (fileData.isFolder() || fileData.getCategory().equals(TYPE_GOOGLE_DRIVE_FOLDER)) {
            setImageForView(R.drawable.ic_folder, imgItemRecent);
            tvContent.setText(fileName);
            bindingGrid.imgBtnVideo.setVisibility(View.INVISIBLE);
        } else {
            tvContent.setText(fileName);
            if (fileName.endsWith(Constant.DOCUMENT_DOC) || Utils.isDocFiles(category)) {
                setImageForView(R.drawable.ic_word_grid, imgItemRecent);
                bindingGrid.imgBtnVideo.setVisibility(View.INVISIBLE);
            } else if (fileName.endsWith(DOCUMENT_DOC_X) || Utils.isDocFiles(category)) {
                setImageForView(R.drawable.ic_word_grid, imgItemRecent);
                bindingGrid.imgBtnVideo.setVisibility(View.INVISIBLE);
            } else if (fileName.endsWith(DOCUMENT_PDF) || Utils.isPdfFiles(category)) {
                setImageForView(R.drawable.ic_pdf_grid, imgItemRecent);
                bindingGrid.imgBtnVideo.setVisibility(View.INVISIBLE);
            } else if (fileName.endsWith(TYPE_APK)) {
                Glide.with(itemView.getContext()).load(filePath).into(imgItemRecent);
                bindingGrid.imgBtnVideo.setVisibility(View.INVISIBLE);
            } else if (fileName.endsWith(DOCUMENT_TXT) || fileName.endsWith(DOCUMENT_TEXT) || Utils.isTxtFiles(category)) {
                setImageForView(R.drawable.ic_txt_grid, imgItemRecent);
                bindingGrid.imgBtnVideo.setVisibility(View.INVISIBLE);
            } else if (fileName.endsWith(DOCUMENT_PPT) || fileName.endsWith(DOCUMENT_PPSX)
                    || fileName.endsWith(DOCUMENT_PPTX) || fileName.endsWith(DOCUMENT_PPS)
                    || fileName.endsWith(DOCUMENT_POT) || fileName.endsWith(DOCUMENT_POTX)
                    || fileName.endsWith(DOCUMENT_PPTM) || Utils.isPptFiles(category)) {
                setImageForView(R.drawable.ic_ppt_grid, imgItemRecent);
                bindingGrid.imgBtnVideo.setVisibility(View.INVISIBLE);
            } else if (fileName.endsWith(DOCUMENT_XLXS) || fileName.endsWith(DOCUMENT_XLSM)
                    || fileName.endsWith(DOCUMENT_XLS) || fileName.endsWith(DOCUMENT_XLTX) || Utils.isExcelFiles(category)) {
                setImageForView(R.drawable.ic_xlsx_grid, imgItemRecent);
                bindingGrid.imgBtnVideo.setVisibility(View.INVISIBLE);
            } else if (Utils.checkFileArchive(fileName)) {
                setImageForView(R.drawable.ic_archiver_grid, imgItemRecent);
                bindingGrid.imgBtnVideo.setVisibility(View.INVISIBLE);
            } else if (fileDataRepository.isMusicFile(fileName)) {
                setImageForImageView(R.drawable.ic_music_grid, imgItemRecent, filePath);
                bindingGrid.imgBtnVideo.setVisibility(View.INVISIBLE);
            } else if (fileDataRepository.isPhotoFile(fileName)) {
                if (fileData.getType() == TYPE_DROP_BOX_FILE) {
                    showImageDropBox(fileData, imgItemRecent, R.drawable.ic_jpg_grid_default);
                } else {
                    setImageForImageView(R.drawable.ic_jpg_grid_default, imgItemRecent, filePath);
                }
                bindingGrid.imgBtnVideo.setVisibility(View.INVISIBLE);
            } else if (fileDataRepository.isVideoFile(fileName)) {
                bindingGrid.imgBtnVideo.setVisibility(View.VISIBLE);
                if (fileData.getType() == TYPE_DROP_BOX_FILE) {
                    showImageDropBox(fileData, imgItemRecent, R.drawable.ic_video_grid_default);
                } else {
                    setImageForImageView(R.drawable.ic_video_grid_default, imgItemRecent, filePath);
                }
            } else {
                setImageForView(R.drawable.ic_noname_file, imgItemRecent);
                bindingGrid.imgBtnVideo.setVisibility(View.INVISIBLE);
            }
        }

    }
}
