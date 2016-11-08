package be.appreciate.buttonsforcleaners.api;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import be.appreciate.buttonsforcleaners.R;
import be.appreciate.buttonsforcleaners.contentproviders.ContractTypeContentProvider;
import be.appreciate.buttonsforcleaners.contentproviders.PlanningContentProvider;
import be.appreciate.buttonsforcleaners.contentproviders.PlanningProductContentProvider;
import be.appreciate.buttonsforcleaners.contentproviders.PlanningResourceContentProvider;
import be.appreciate.buttonsforcleaners.contentproviders.ProductContentProvider;
import be.appreciate.buttonsforcleaners.contentproviders.QuestionContentProvider;
import be.appreciate.buttonsforcleaners.model.api.AnsweredQuestion;
import be.appreciate.buttonsforcleaners.model.api.ContractType;
import be.appreciate.buttonsforcleaners.model.api.FeedbackRequest;
import be.appreciate.buttonsforcleaners.model.api.FeedbackResponse;
import be.appreciate.buttonsforcleaners.model.api.LoginRequest;
import be.appreciate.buttonsforcleaners.model.api.LoginResponse;
import be.appreciate.buttonsforcleaners.model.api.NotifyCustomerRequest;
import be.appreciate.buttonsforcleaners.model.api.Product;
import be.appreciate.buttonsforcleaners.model.api.Resource;
import be.appreciate.buttonsforcleaners.utils.CookieHelper;
import be.appreciate.buttonsforcleaners.utils.DateUtils;
import be.appreciate.buttonsforcleaners.utils.ImageUtils;
import be.appreciate.buttonsforcleaners.utils.PreferencesHelper;
import retrofit.RestAdapter;
import retrofit.converter.ConversionException;
import retrofit.converter.GsonConverter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Inneke De Clippel on 1/03/2016.
 */
public class ApiHelper
{
    private static BfcApi service;

    private static BfcApi getService(Context context)
    {
        if(service == null)
        {
            String domain = PreferencesHelper.getDomain(context);
            String domainSuffix = context.getString(R.string.login_domain_suffix);

            if(TextUtils.isEmpty(domain))
            {
                throw new IllegalStateException("The domain can not be an empty String");
            }

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("http://" + domain + domainSuffix + "/API")
                    .build();

            service = restAdapter.create(BfcApi.class);
        }

        return service;
    }

    private static void resetService()
    {
        service = null;
    }

    public static Observable<Object> logIn(Context context, String username, String password)
    {
        ApiHelper.resetService();

        String domain = PreferencesHelper.getDomain(context);
        String domainSuffix = context.getString(R.string.login_domain_suffix);
        String fullUsername = username + "@" + domain + domainSuffix;

        return ApiHelper.getService(context).logIn(new LoginRequest(fullUsername, password))
                .flatMap(response ->
                {
                    String applicationCookie = CookieHelper.getApplicationCookie(response.getHeaders());
                    PreferencesHelper.saveApplicationCookie(context, applicationCookie);

                    LoginResponse loginResponse;

                    try
                    {
                        GsonConverter converter = new GsonConverter(new Gson());
                        loginResponse = (LoginResponse) converter.fromBody(response.getBody(), LoginResponse.class);
                    }
                    catch (ConversionException e)
                    {
                        return Observable.error(e);
                    }

                    if (loginResponse != null && loginResponse.isSuccess())
                    {
                        return Observable.just(loginResponse);
                    }
                    else
                    {
                        String error = loginResponse != null ? loginResponse.getErrorMessage() : "The login response is null";
                        return Observable.error(new IOException(error));
                    }
                })
                .doOnNext(loginResponse ->
                {
                    PreferencesHelper.saveUserId(context, loginResponse.getId());
                    PreferencesHelper.saveUserName(context, loginResponse.getName());
                    PreferencesHelper.saveUserType(context, loginResponse.getType());
                    PreferencesHelper.saveUserImageUrl(context, loginResponse.getImageUrl());
                    PreferencesHelper.saveUserPhone(context, loginResponse.getPhoneNumber());
                    PreferencesHelper.saveUserLogoUrl(context, loginResponse.getLogoUrl());
                    ImageUtils.downloadAndSaveImage(context, loginResponse.getImageUrl());
                    ImageUtils.downloadAndSaveImage(context, loginResponse.getLogoUrl());
                })
                .flatMap(loginResponse -> Observable.empty())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<List<ContractType>> getContractTypes(Context context)
    {
        String cookie = PreferencesHelper.getApplicationCookie(context);

        return ApiHelper.getService(context).getContractTypes(cookie)
                .flatMap(contractTypesResponse ->
                {
                    if (contractTypesResponse != null && contractTypesResponse.isSuccess() && contractTypesResponse.getContractTypes() != null)
                    {
                        return Observable.just(contractTypesResponse.getContractTypes());
                    }
                    else
                    {
                        String error = contractTypesResponse != null ? contractTypesResponse.getErrorMessage() : "The contract types response is null";
                        return Observable.error(new IOException(error));
                    }
                })
                .doOnNext(contractTypes ->
                {
                    ContentResolver contentResolver = context.getContentResolver();
                    contentResolver.delete(ContractTypeContentProvider.CONTENT_URI, null, null);
                    contentResolver.delete(QuestionContentProvider.CONTENT_URI, null, null);
                })
                .flatMap(contractTypes -> Observable.from(contractTypes))
                .doOnNext(contractType ->
                {
                    ContentResolver contentResolver = context.getContentResolver();
                    ContentValues cvContractType = contractType.getContentValues();
                    ContentValues[] cvQuestions = contractType.getQuestionContentValues();
                    contentResolver.insert(ContractTypeContentProvider.CONTENT_URI, cvContractType);
                    contentResolver.bulkInsert(QuestionContentProvider.CONTENT_URI, cvQuestions);
                })
                .toList()
                .subscribeOn(Schedulers.io());
    }

    public static Observable<List<Product>> getProducts(Context context)
    {
        String cookie = PreferencesHelper.getApplicationCookie(context);

        return ApiHelper.getService(context).getProducts(cookie)
                .flatMap(productsResponse ->
                {
                    if (productsResponse != null && productsResponse.isSuccess() && productsResponse.getProducts() != null)
                    {
                        return Observable.just(productsResponse.getProducts());
                    }
                    else
                    {
                        String error = productsResponse != null ? productsResponse.getErrorMessage() : "The products response is null";
                        return Observable.error(new IOException(error));
                    }
                })
                .doOnNext(products ->
                {
                    ContentResolver contentResolver = context.getContentResolver();
                    contentResolver.delete(ProductContentProvider.CONTENT_URI, null, null);
                })
                .flatMap(products -> Observable.from(products))
                .doOnNext(product ->
                {
                    ContentResolver contentResolver = context.getContentResolver();
                    ContentValues cvProduct = product.getContentValues();
                    contentResolver.insert(ProductContentProvider.CONTENT_URI, cvProduct);
                    ImageUtils.downloadAndSaveImage(context, product.getImageUrl());
                })
                .toList()
                .subscribeOn(Schedulers.io());
    }

    public static Observable<Object> doStartUpCalls(Context context)
    {
        return Observable
                .zip(ApiHelper.getContractTypes(context), ApiHelper.getProducts(context), (o, o2) -> null)
                .doOnNext(o -> PreferencesHelper.saveStartupCallsCompleted(context, true))
                .flatMap(o -> Observable.empty())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<Object> getPlanning(Context context)
    {
        String cookie = PreferencesHelper.getApplicationCookie(context);
        String date = DateUtils.formatApiDate(System.currentTimeMillis());
        int userId = PreferencesHelper.getUserId(context);
        int type = PreferencesHelper.getUserType(context);

        return ApiHelper.getService(context).getPlanning(cookie, date, userId, type)
                .flatMap(planningResponse ->
                {
                    if (planningResponse != null && planningResponse.isSuccess() && planningResponse.getPlanningItems() != null)
                    {
                        return Observable.just(planningResponse.getPlanningItems());
                    }
                    else
                    {
                        String error = planningResponse != null ? planningResponse.getErrorMessage() : "The planning response is null";
                        return Observable.error(new IOException(error));
                    }
                })
                .doOnNext(planningItems ->
                {
                    PreferencesHelper.saveLastPlanningRefresh(context, System.currentTimeMillis());

                    ContentResolver contentResolver = context.getContentResolver();
                    contentResolver.delete(PlanningContentProvider.CONTENT_URI, null, null);
                    contentResolver.delete(PlanningProductContentProvider.CONTENT_URI, null, null);
                    contentResolver.delete(PlanningResourceContentProvider.CONTENT_URI, null, null);
                })
                .flatMap(planningItems -> Observable.from(planningItems))
                .doOnNext(planningItem ->
                {
                    ContentResolver contentResolver = context.getContentResolver();
                    ContentValues cvPlanning = planningItem.getContentValues();
                    ContentValues[] cvProducts = planningItem.getProductContentValues();
                    ContentValues[] cvResources = planningItem.getResourceContentValues();
                    contentResolver.insert(PlanningContentProvider.CONTENT_URI, cvPlanning);
                    contentResolver.bulkInsert(PlanningProductContentProvider.CONTENT_URI, cvProducts);
                    contentResolver.bulkInsert(PlanningResourceContentProvider.CONTENT_URI, cvResources);

                    if (planningItem.getProducts() != null)
                    {
                        for (Product product : planningItem.getProducts())
                        {
                            ImageUtils.downloadAndSaveImage(context, product.getImageUrl());
                        }
                    }

                    if (planningItem.getResources() != null)
                    {
                        for (Resource resource : planningItem.getResources())
                        {
                            ImageUtils.downloadAndSaveImage(context, resource.getImageUrl());
                        }
                    }
                })
                .toList()
                .flatMap(planningItems -> Observable.empty())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<Object> notifyCustomer(Context context, int planningId, String accountancyName, String address, String phoneNumber)
    {
        String cookie = PreferencesHelper.getApplicationCookie(context);
        NotifyCustomerRequest request = new NotifyCustomerRequest(planningId, accountancyName, address, phoneNumber);

        return ApiHelper.getService(context).notifyCustomer(cookie, request)
                .flatMap(notifyCustomerResponse ->
                {
                    if (notifyCustomerResponse != null && notifyCustomerResponse.isSuccess())
                    {
                        return Observable.empty();
                    } else
                    {
                        String error = notifyCustomerResponse != null ? notifyCustomerResponse.getErrorMessage() : "The notify response is null";
                        return Observable.error(new IOException(error));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<Object> postFeedback(Context context, int planningId, String startTime, String endTime, List<AnsweredQuestion> answeredQuestions, int distanceTraveled)
    {
        String cookie = PreferencesHelper.getApplicationCookie(context);
        FeedbackRequest request = new FeedbackRequest(planningId, startTime, endTime, answeredQuestions, distanceTraveled);

        return ApiHelper.getService(context).postFeedback(cookie, request)
                .flatMap(feedbackResponse ->
                {
                    if (feedbackResponse != null && feedbackResponse.isSuccess())
                    {
                        return Observable.empty();
                    } else
                    {
                        String error = feedbackResponse != null ? feedbackResponse.getErrorMessage() : "The feedback response is null";
                        return Observable.error(new IOException(error));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static FeedbackResponse postFeedbackBlocking(Context context, FeedbackRequest request)
    {
        String cookie = PreferencesHelper.getApplicationCookie(context);

        return ApiHelper.getService(context).postFeedbackBlocking(cookie, request);
    }
}
