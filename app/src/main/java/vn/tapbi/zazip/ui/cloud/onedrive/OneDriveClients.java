package vn.tapbi.zazip.ui.cloud.onedrive;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;
import vn.tapbi.zazip.data.model.onedrive.BodyShareFile;
import vn.tapbi.zazip.data.model.onedrive.ResponseOneDrive;
import vn.tapbi.zazip.data.model.onedrive.ResponseShareLink;
import vn.tapbi.zazip.interfaces.onedriveApi.OnedriveApi;

public class OneDriveClients {
    OnedriveApi onedriveApi;

    @Inject
    public OneDriveClients(OnedriveApi onedriveApi) {
        this.onedriveApi = onedriveApi;
    }

    public Single<ResponseOneDrive> getOneDriveFolder(String token, String id) {
        String author = "Bearer " + token;
        if (id != null && !id.isEmpty()) {
            return onedriveApi.getFolderById(author, id);
        }
        return onedriveApi.getFolderRoot(author);
    }

    public Single<ResponseShareLink> getLinkOnedrive(String token, String id) {
        return onedriveApi.shareFileById(token, id, new BodyShareFile());
    }
}
