package be.appreciate.buttonsforcleaners.api;

import be.appreciate.buttonsforcleaners.model.api.ContractTypesResponse;
import be.appreciate.buttonsforcleaners.model.api.FeedbackRequest;
import be.appreciate.buttonsforcleaners.model.api.FeedbackResponse;
import be.appreciate.buttonsforcleaners.model.api.LoginRequest;
import be.appreciate.buttonsforcleaners.model.api.NotifyCustomerRequest;
import be.appreciate.buttonsforcleaners.model.api.NotifyCustomerResponse;
import be.appreciate.buttonsforcleaners.model.api.PlanningResponse;
import be.appreciate.buttonsforcleaners.model.api.ProductsResponse;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by Inneke De Clippel on 1/03/2016.
 */
public interface BfcApi
{
    @POST("/Login")
    Observable<Response> logIn(@Body LoginRequest request);

    @GET("/GetContractTypes")
    Observable<ContractTypesResponse> getContractTypes(@Header("Cookie") String applicationCookie);

    @GET("/GetProducts")
    Observable<ProductsResponse> getProducts(@Header("Cookie") String applicationCookie);

    @GET("/GetPlanning")
    Observable<PlanningResponse> getPlanning(@Header("Cookie") String applicationCookie,
                                             @Query("Date") String date,
                                             @Query("ResourceId") int userId,
                                             @Query("ResourceType") int type);

    @POST("/SendNotification")
    Observable<NotifyCustomerResponse> notifyCustomer(@Header("Cookie") String applicationCookie,
                                                      @Body NotifyCustomerRequest request);

    @POST("/SetExecution")
    Observable<FeedbackResponse> postFeedback(@Header("Cookie") String applicationCookie,
                                              @Body FeedbackRequest request);

    @POST("/SetExecution")
    FeedbackResponse postFeedbackBlocking(@Header("Cookie") String applicationCookie,
                                          @Body FeedbackRequest request);
}
