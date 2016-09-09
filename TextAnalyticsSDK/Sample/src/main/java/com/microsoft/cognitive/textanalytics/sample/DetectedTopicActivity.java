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
package com.microsoft.cognitive.textanalytics.sample;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.microsoft.cognitive.textanalytic.sample.BuildConfig;
import com.microsoft.cognitive.textanalytic.sample.R;
import com.microsoft.cognitive.textanalytics.model.request.RequestDoc;
import com.microsoft.cognitive.textanalytics.model.request.topics.TopicRequest;
import com.microsoft.cognitive.textanalytics.model.response.topics.ProcessingResult;
import com.microsoft.cognitive.textanalytics.model.response.topics.TopicResponse;
import com.microsoft.cognitive.textanalytics.retrofit.ServiceRequestClient;
import com.microsoft.cognitive.textanalytics.sample.model.Question;
import com.microsoft.cognitive.textanalytics.sample.model.QuestionList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * This screen allows topic detection of multiple records, example by using titles from unanswered questions on StackOverflow
 * Must include at least 100 documents in the request
 * Note the process may take longer than 5 minutes
 */
public class DetectedTopicActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String BASE_URL = "https://api.stackexchange.com/";
    private static final String TAG = DetectedTopicActivity.class.getSimpleName();
    private static final String SUBSCRIPTION_KEY = "key";

    // Network
    private ServiceRequestClient mRequest;
    private TopicRequest mTopicRequest;
    private CompositeSubscription mSubscriptions;

    private List<String> mQuestionTitles;
    private String mOperationId;

    // UI
    private ProgressDialog mProgressDialog;
    private TextView mNumberOfSOTitles;
    private TextView mOperationIdView;
    private TextView mTopicsView;

    public static Intent createIntent(Context context, String key) {
        Intent intent = new Intent(context, DetectedTopicActivity.class);
        intent.putExtra(SUBSCRIPTION_KEY, key);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detected_topic);
        String subscriptionKey = getIntent().getStringExtra(SUBSCRIPTION_KEY);

        mRequest = new ServiceRequestClient(subscriptionKey);
        mSubscriptions = new CompositeSubscription();

        mNumberOfSOTitles = (TextView) findViewById(R.id.no_of_so_questions);
        mOperationIdView = (TextView) findViewById(R.id.opId);
        mTopicsView = (TextView) findViewById(R.id.detected_topics);

        Button getSOButton = (Button) findViewById(R.id.btn_get_so_questions);
        Button getOpId = (Button) findViewById(R.id.btn_get_OpId);
        Button detectButton = ((Button) findViewById(R.id.btn_detect_topics));

        // Set button OnClickListeners
        getSOButton.setOnClickListener(this);
        getOpId.setOnClickListener(this);
        detectButton.setOnClickListener(this);

        // Construct a request with at least 100 text records
        mTopicRequest = new TopicRequest();
        mQuestionTitles = new ArrayList<>();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_so_questions:
                showProgressDialog();
                getSOQuestions();
                break;
            case R.id.btn_get_OpId:
                getOperationId();
                break;
            case R.id.btn_detect_topics:
                showProgressDialog();
                getDetectedTopics();
                break;
            default:
        }
    }

    // Get questions from StackOverflow
    private void getSOQuestions() {

        // Define the interceptor, add authentication headers
        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder().build();
                return chain.proceed(newRequest);
            }
        };

        // Add the interceptor to OkHttpClient
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().add(interceptor);
        OkHttpClient client = builder.build();

        // Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(client)
                .build();

        // Service for StackOverflow
        StackExchangeService service = retrofit.create(StackExchangeService.class);

        Observable<QuestionList> unAnsweredQuestions = service.getQuestionsUnanswered();
        Observable<QuestionList> noAnswersQuestions = service.getQuestionsWithNoAnswers();

        // Add StackOver subscription by concatenating the two calls, executed sequentially
        mSubscriptions.add(
                Observable.concat(unAnsweredQuestions, noAnswersQuestions)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<QuestionList>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                dismissProgressDialog();
                                Log.e(TAG, "error is " + e.getMessage());

                            }

                            @Override
                            public void onNext(QuestionList questionList) {
                                Log.i(TAG, "Number of questions - " + questionList.getItems().size());

                                for (int i = 0; i < questionList.getItems().size(); i++) {
                                    Question q = questionList.getItems().get(i);
                                    mQuestionTitles.add(q.getTitle());
                                }

                                dismissProgressDialog();
                                String text = String.format(getResources().getString(R.string.no_of_so_questions), mQuestionTitles.size());
                                mNumberOfSOTitles.setText(text);

                                // Log the request json
                                if (BuildConfig.DEBUG) {
                                    Gson gson = new Gson();
                                    String requestString = gson.toJson(mTopicRequest);
                                    Log.i(TAG, "Request is " + requestString);
                                }

                            }
                        }));
    }

    private void getOperationId() {

        // Show progress dialog
        showProgressDialog();

        // Add stack overflow question titles to the topic request
        for (int i = 0; i < mQuestionTitles.size(); i++) {
            RequestDoc topicRequestDoc = new RequestDoc();
            topicRequestDoc.setId(String.valueOf(i));
            topicRequestDoc.setText(mQuestionTitles.get(i));
            mTopicRequest.addDocument(topicRequestDoc);
        }

        //TODO: You can add stop words and stop phrases optionally
/*
        List<String> stopWords = new ArrayList<>();
        List<String> stopPhrases = new ArrayList<>();
        stopWords.add("question");
        stopPhrases.add("question");
        mTopicRequest.setStopWords(stopWords);
        mTopicRequest.setStopPhrases(stopPhrases);
*/

        mSubscriptions.add(
                mRequest.getTopicsUrlRx(mTopicRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Response<ResponseBody>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(Response<ResponseBody> response) {
                                mOperationId = response.headers().get("Operation-Location");
                                Log.i(TAG, "OperationId is " + mOperationId);
                                mOperationIdView.setText(mOperationId);
                                dismissProgressDialog();
                            }
                        }));
    }

    /**
     * Get detected topics from over 100 text records, polling every 30 seconds until we reach "Succeeded" status
     */
    private void getDetectedTopics() {

        mSubscriptions.add(mRequest.getTopicsRx(mOperationId)
                // Skip while the status is not yet "Succeeded"
                .skipWhile(new Func1<TopicResponse, Boolean>() {
                    @Override
                    public Boolean call(TopicResponse topicResponse) {
                        boolean toSkip = (!topicResponse.getStatus().equals("Succeeded"));
                        Log.i(TAG, "In skipWhile(), status is now " + topicResponse.getStatus());
                        return toSkip;
                    }
                })// Polling every 60 seconds
                .repeatWhen(new Func1<Observable<? extends Void>, Observable<?>>() {
                    @Override
                    public Observable<?> call(Observable<? extends Void> observable) {
                        return observable.delay(60, TimeUnit.SECONDS);
                    }
                }) // Keep polling until the status becomes "Succeeded"
                .takeUntil(new Func1<TopicResponse, Boolean>() {
                    @Override
                    public Boolean call(TopicResponse topicResponse) {
                        return topicResponse.getStatus().equals("Succeeded");
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TopicResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                        dismissProgressDialog();
                    }

                    @Override
                    public void onNext(TopicResponse topicResponse) {
                        Log.i(TAG, "In subscribe(), status is now " + topicResponse.getStatus());
                        ProcessingResult result = topicResponse.getProcessingResult();
                        if (result != null) {
                            mTopicsView.setText(topicResponse.getProcessingResult().getTopicStrings().toString());
                            Log.i(TAG, "Topics are " + topicResponse.getProcessingResult().getTopicStrings());
                        } else {
                            Log.e(TAG, "Processing result is null");
                        }
                        dismissProgressDialog();
                    }
                }));

    }

    /**
     * Un-subscribe all RxJava subscriptions onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mSubscriptions != null) {
            mSubscriptions.unsubscribe();
        }

    }

    private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setTitle(getString(R.string.progress_bar_title));
        mProgressDialog.show();
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

}
