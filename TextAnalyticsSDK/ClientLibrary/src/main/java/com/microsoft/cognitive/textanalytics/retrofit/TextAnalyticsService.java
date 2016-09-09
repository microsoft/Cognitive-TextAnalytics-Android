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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

public interface TextAnalyticsService {

    @POST("languages")
    Call<LanguageResponse> getLanguages(@Body LanguageRequest body, @Query("numberOfLanguagesToDetect") int numLanguage);

    @POST("keyPhrases")
    Call<KeyPhrasesResponse> getKeyPhrases(@Body TextRequest body);

    @POST("sentiment")
    Call<SentimentResponse> getSentiments(@Body TextRequest body);

    // 1st call for retrieving detected topics, one time request
    @POST("topics")
    Call<Void> retrieveTopicsUrl(@Body TopicRequest body);

    // 2nd call for detected topics, to be polled every 60 seconds
    @GET
    Call<TopicResponse> getDetectedTopics(@Url String operationId);

    // Observable for RxJava - 1st call for retrieving detected topics url, one time request
    @POST("topics")
    Observable<Response<ResponseBody>> retrieveTopicsUrlRx(@Body TopicRequest body);

    // Observable for RxJava - 2nd call for detected topics, to be polled every 60 seconds
    @GET
    Observable<TopicResponse> getDetectedTopicsRx(@Url String operationId);

}
