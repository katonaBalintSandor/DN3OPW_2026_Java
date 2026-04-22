package com.example.dn3opw_2026.network;

import com.example.dn3opw_2026.network.responses.AdminBookDetailResponse;
import com.example.dn3opw_2026.network.responses.AdminCommunityTopicsResponse;
import com.example.dn3opw_2026.network.responses.AdminEventDetailResponse;
import com.example.dn3opw_2026.network.responses.AdminEventsResponse;
import com.example.dn3opw_2026.network.responses.AdminLeasesResponse;
import com.example.dn3opw_2026.network.responses.AdminLoginResponse;
import com.example.dn3opw_2026.network.responses.AdminTopicDetailsResponse;
import com.example.dn3opw_2026.network.responses.BaseResponse;
import com.example.dn3opw_2026.network.responses.BookDetailResponse;
import com.example.dn3opw_2026.network.responses.BookResponse;
import com.example.dn3opw_2026.network.responses.CommentsResponse;
import com.example.dn3opw_2026.network.responses.EventsResponse;
import com.example.dn3opw_2026.network.responses.LeasedBooksResponse;
import com.example.dn3opw_2026.network.responses.LibrariesResponse;
import com.example.dn3opw_2026.network.responses.LibraryDetailResponse;
import com.example.dn3opw_2026.network.responses.LoginResponse;
import com.example.dn3opw_2026.network.responses.RecommendationsResponse;
import com.example.dn3opw_2026.network.responses.RegisterResponse;
import com.example.dn3opw_2026.network.responses.TopicDetailResponse;
import com.example.dn3opw_2026.network.responses.TopicsResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {

    @FormUrlEncoded
    @POST("login.php")
    Call<LoginResponse> login(
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("register.php")
    Call<RegisterResponse> register(
            @Field("lastname") String lastname,
            @Field("firstname") String firstname,
            @Field("username") String username,
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("admin_login.php")
    Call<AdminLoginResponse> loginAdmin(
            @Field("username") String username,
            @Field("password") String password,
            @Field("admin_code") String adminCode
    );

    @GET("get_all_books.php")
    Call<BookResponse> getAllBooks();

    @GET("get_libraries.php")
    Call<LibrariesResponse> getLibraries();

    @GET("get_library_details.php")
    Call<LibraryDetailResponse> getLibraryDetails(
            @Query("library_id") int libraryId
    );

    @GET("get_book_details.php")
    Call<BookDetailResponse> getBookDetails(
            @Query("book_id") int bookId,
            @Query("library_id") int libraryId
    );

    @GET("get_recommendations.php")
    Call<RecommendationsResponse> getRecommendations(
            @Query("user_id") int userId,
            @Query("limit") int limit
    );

    @GET("get_user_leased_books.php")
    Call<LeasedBooksResponse> getLeasedBooks(
            @Query("user_id") int userId
    );

    @FormUrlEncoded
    @POST("lease_book.php")
    Call<BaseResponse> leaseBook(
            @Field("user_id") int userId,
            @Field("library_id") int libraryId,
            @Field("book_id") int bookId
    );

    @FormUrlEncoded
    @POST("return_book.php")
    Call<BaseResponse> returnBook(
            @Field("lease_id") int leaseId,
            @Field("book_id") int bookId,
            @Field("library_id") int libraryId
    );

    @FormUrlEncoded
    @POST("update_password.php")
    Call<BaseResponse> updatePassword(
            @Field("user_id") int userId,
            @Field("password") String password
    );

    @GET("get_all_topics.php")
    Call<TopicsResponse> getAllTopics();

    @GET("get_topic_by_id.php")
    Call<TopicDetailResponse> getTopicById(
            @Query("topic_id") int topicId
    );

    @FormUrlEncoded
    @POST("add_topic.php")
    Call<BaseResponse> addTopic(
            @Field("topic") String topic,
            @Field("description") String description,
            @Field("rating") int rating,
            @Field("book_id") int bookId,
            @Field("user_id") int userId
    );

    @FormUrlEncoded
    @POST("delete_topic.php")
    Call<BaseResponse> deleteTopic(
            @Field("topic_id") int topicId
    );

    @GET("get_comments_for_topic.php")
    Call<CommentsResponse> getCommentsForTopic(
            @Query("topic_id") int topicId
    );

    @FormUrlEncoded
    @POST("add_comment.php")
    Call<BaseResponse> addComment(
            @Field("topic_id") int topicId,
            @Field("user_id") int userId,
            @Field("comment") String comment
    );

    @FormUrlEncoded
    @POST("delete_comment.php")
    Call<BaseResponse> deleteComment(
            @Field("comment_id") int commentId,
            @Field("user_id") int userId
    );

    @GET("get_all_events.php")
    Call<EventsResponse> getAllEvents();

    @GET("get_admin_books.php")
    Call<BookResponse> getAdminBooks(
            @Query("library_id") int libraryId
    );

    @GET("get_admin_library.php")
    Call<LibraryDetailResponse> getAdminLibrary(
            @Query("library_id") int libraryId
    );

    @FormUrlEncoded
    @POST("get_admin_book_by_id.php")
    Call<AdminBookDetailResponse> getAdminBookById(
            @Field("book_id") int bookId,
            @Field("library_id") int libraryId
    );

    @FormUrlEncoded
    @POST("update_book_quantity.php")
    Call<BaseResponse> updateBookQuantity(
            @Field("library_id") int libraryId,
            @Field("book_id") int bookId,
            @Field("quantity") int quantity
    );

    @Multipart
    @POST("add_admin_book.php")
    Call<BaseResponse> addAdminBook(
            @Part("title") RequestBody title,
            @Part("author") RequestBody author,
            @Part("category") RequestBody category,
            @Part("release_date") RequestBody releaseDate,
            @Part("description") RequestBody description,
            @Part("uploaded_by") RequestBody uploadedBy,
            @Part("library_id") RequestBody libraryId,
            @Part("quantity") RequestBody quantity,
            @Part MultipartBody.Part image
    );

    @Multipart
    @POST("update_admin_book.php")
    Call<BaseResponse> updateAdminBook(
            @Part("book_id") RequestBody bookId,
            @Part("title") RequestBody title,
            @Part("author") RequestBody author,
            @Part("category") RequestBody category,
            @Part("release_date") RequestBody releaseDate,
            @Part("description") RequestBody description,
            @Part("old_picture") RequestBody oldPicture,
            @Part MultipartBody.Part image
    );

    @FormUrlEncoded
    @POST("get_admin_leases.php")
    Call<AdminLeasesResponse> getAdminLeases(
            @Field("library_id") int libraryId
    );

    @GET("get_admin_community_topics.php")
    Call<AdminCommunityTopicsResponse> getAdminCommunityTopics();

    @FormUrlEncoded
    @POST("get_admin_topic_details.php")
    Call<AdminTopicDetailsResponse> getAdminTopicDetails(
            @Field("topic_id") int topicId
    );

    @FormUrlEncoded
    @POST("delete_admin_topic.php")
    Call<BaseResponse> deleteAdminTopic(
            @Field("topic_id") int topicId
    );

    @FormUrlEncoded
    @POST("delete_admin_comment.php")
    Call<BaseResponse> deleteAdminComment(
            @Field("comment_id") int commentId
    );

    @GET("get_admin_events.php")
    Call<AdminEventsResponse> getAdminEvents();

    @FormUrlEncoded
    @POST("get_admin_event_by_id.php")
    Call<AdminEventDetailResponse> getAdminEventById(
            @Field("event_id") int eventId
    );

    @FormUrlEncoded
    @POST("delete_admin_event.php")
    Call<BaseResponse> deleteEvent(
            @Field("event_id") int eventId
    );

    @Multipart
    @POST("update_admin_event.php")
    Call<BaseResponse> updateAdminEvent(
            @Part("event_id") RequestBody eventId,
            @Part("title") RequestBody title,
            @Part("header") RequestBody header,
            @Part("date") RequestBody date,
            @Part("description") RequestBody description,
            @Part("old_picture") RequestBody oldPicture,
            @Part MultipartBody.Part image
    );

    @Multipart
    @POST("add_admin_event.php")
    Call<BaseResponse> addAdminEvent(
            @Part("title") RequestBody title,
            @Part("header") RequestBody header,
            @Part("date") RequestBody date,
            @Part("description") RequestBody description,
            @Part("admin_id") RequestBody adminId,
            @Part("library_id") RequestBody libraryId,
            @Part MultipartBody.Part image
    );
}