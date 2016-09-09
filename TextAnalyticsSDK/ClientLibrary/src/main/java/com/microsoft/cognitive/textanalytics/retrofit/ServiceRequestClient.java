//
// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.
//
// Microsoft Cognitive Services (formerly Project Oxford): https://www.microsoft.com/cognitive-services
//
// Microsoft Cognitive Services Text Analytics Android repository on GitHub:
// https://github.com/Microsoft/Cognitive-TextAnalytics-Android
//
// Copyright (c) Microsoft Corporation
// All rights reserved.
//
// MIT License:
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//

package com.microsoft.cognitive.textanalytics.retrofit;

import com.microsoft.cognitive.textanalytics.model.request.keyphrases_sentiment.TextRequest;
import com.microsoft.cognitive.textanalytics.model.request.language.LanguageRequest;
import com.microsoft.cognitive.textanalytics.model.request.topics.TopicRequest;
import com.microsoft.cognitive.textanalytics.model.response.keyphrases.KeyPhrasesResponse;
import com.microsoft.cognitive.textanalytics.model.response.language.LanguageResponse;
import com.microsoft.cognitive.textanalytics.model.response.sentiment.SentimentResponse;
import com.microsoft.cognitive.textanalytics.model.response.topics.TopicResponse;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public class ServiceRequestClient {

    private static final String BASE_URL = "https://westus.api.cognitive.microsoft.com/text/analytics/v2.0/";
    private static final String mHeaderKey = "ocp-apim-subscription-key";

    private String mSubscriptionKey;
    private TextAnalyticsService mTextAnalyticsService;
    private Retrofit mRetrofit;

    public ServiceRequestClient(String key) {
        this.mSubscriptionKey = key;

        // Define the interceptor with authentication header
        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder().addHeader(mHeaderKey, mSubscriptionKey).build();
                return chain.proceed(newRequest);
            }
        };

        // Add the interceptor to OkHttpClient
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().add(interceptor);
        OkHttpClient client = builder.build();

        // Retrofit
        mRetrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(client)
                .build();

        // Service
        mTextAnalyticsService = mRetrofit.create(TextAnalyticsService.class);
    }


    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    //region Languages

    /**
     * Get Languages directly
     *
     * @param request
     * @param numLanguages
     * @return
     */
    public Response getLanguages(LanguageRequest request, int numLanguages) {

        Call<LanguageResponse> languageResponseCall = mTextAnalyticsService.getLanguages(request, numLanguages);
        Response response = null;
        try {
            response = languageResponseCall.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * Get languages asynchronously
     *
     * @param request
     * @param numLanguages
     * @param serviceCallback
     * @return
     * @throws IllegalArgumentException
     */
    public ServiceCall getLanguagesAsync(LanguageRequest request, int numLanguages, ServiceCallback serviceCallback)
            throws IllegalArgumentException {
        if (serviceCallback == null) {
            throw new IllegalArgumentException("Callback is required for async call");
        }

        Call<LanguageResponse> call = mTextAnalyticsService.getLanguages(request, numLanguages);
        final ServiceCall serviceCall = new ServiceCall(call);
        call.enqueue(serviceCallback);

        return serviceCall;
    }
    //endregion

    //region Key Phrases

    /**
     * API call to get key phrases
     *
     * @param request
     * @return
     */
    public Response callKeyPhrases(TextRequest request) {
        Call<KeyPhrasesResponse> keyPhasesCall = mTextAnalyticsService.getKeyPhrases(request);
        Response response = null;
        try {
            response = keyPhasesCall.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * Async API call to tet key phrases
     *
     * @param request
     * @param serviceCallback
     * @return
     * @throws IllegalArgumentException
     */
    public ServiceCall getKeyPhrasesAsync(TextRequest request, ServiceCallback serviceCallback)
            throws IllegalArgumentException {
        if (serviceCallback == null) {
            throw new IllegalArgumentException("Callback is required for async call");
        }

        Call<KeyPhrasesResponse> call = mTextAnalyticsService.getKeyPhrases(request);
        final ServiceCall serviceCall = new ServiceCall(call);
        call.enqueue(serviceCallback);

        return serviceCall;
    }
    //endregion

    //region Sentiment

    /**
     * API call for getting a sentiment score of the text
     *
     * @param request
     * @return
     */
    public Response callSentiments(TextRequest request) {
        Call<SentimentResponse> sentimentCall = mTextAnalyticsService.getSentiments(request);
        Response response = null;
        try {
            response = sentimentCall.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * Async API call for getting a sentiment score of the text
     *
     * @param request
     * @param serviceCallback
     * @return
     */
    public ServiceCall getSentimentAsync(TextRequest request, ServiceCallback serviceCallback) {
        if (serviceCallback == null) {
            throw new IllegalArgumentException("Callback is required for async call");
        }

        Call<SentimentResponse> call = mTextAnalyticsService.getSentiments(request);
        final ServiceCall serviceCall = new ServiceCall(call);
        call.enqueue(serviceCallback);

        return serviceCall;
    }

    //endregion

    //region Detected Topics

    /**
     * 1. Async API call for getting operationId, to be used for GET call of topics detection
     * @param request
     * @param serviceCallback
     * @return
     */
    public ServiceCall getTopicsOpIdAsync(TopicRequest request, ServiceCallback serviceCallback) {
        if (serviceCallback == null) {
            throw new IllegalArgumentException("Callback is required for async call");
        }

        Call<Void> call = mTextAnalyticsService.retrieveTopicsUrl(request);
        final ServiceCall serviceCall = new ServiceCall(call);
        call.enqueue(serviceCallback);

        return serviceCall;
    }

    /**
     * 2. Async API call for getting detected topics
     * @param operationId
     * @param serviceCallback
     * @return
     */
    public ServiceCall getTopicsAsync(String operationId, ServiceCallback serviceCallback){
        if (serviceCallback == null) {
            throw new IllegalArgumentException("Callback is required for async call");
        }
        Call<TopicResponse> call = mTextAnalyticsService.getDetectedTopics(operationId);
        final ServiceCall serviceCall = new ServiceCall(call);
        call.enqueue(serviceCallback);

        return  serviceCall;
    }
    /**
     * 1. For use with RxJava, API call for getting operationId, to be used for GET call of topics detection
     *
     * @param request
     * @return
     */
    public Observable<Response<ResponseBody>> getTopicsUrlRx(TopicRequest request) {
        return mTextAnalyticsService.retrieveTopicsUrlRx(request);
    }

    /**
     * 2. For use with RxJava, API call for topics detection
     *
     * @param operationsId
     * @return
     */
    public Observable<TopicResponse> getTopicsRx(String operationsId) {

        return mTextAnalyticsService.getDetectedTopicsRx(operationsId);
    }

    //endregion
}
