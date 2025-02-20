package vn.tapbi.zazip.interfaces.onedriveApi;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import vn.tapbi.zazip.data.model.onedrive.ResponseOneDrive;
import vn.tapbi.zazip.data.model.onedrive.ResponseShareLink;
import vn.tapbi.zazip.data.model.onedrive.BodyShareFile;

public interface OnedriveApi {

    @GET("drive/root/children")
    Single<ResponseOneDrive> getFolderRoot(@Header("Authorization") String authorization);

    @GET("drive/items/{id}/children")
    Single<ResponseOneDrive> getFolderById(@Header("Authorization") String authorization, @Path("id") String id);

    @GET("drive/items/{id}/content")
    Single<Void> downloadFileById(@Header("Authorization") String authorization, @Path("id") String id);

    ///me/drive/items/{itemId}/createLink
    @POST("drive/items/{id}/createLink")
    Single<ResponseShareLink> shareFileById(@Header("Authorization") String authorization, @Path("id") String id, @Body BodyShareFile bodyShareFile);

}
