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
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.cognitive.textanalytic.sample.R;
import com.microsoft.cognitive.textanalytics.model.request.RequestDoc;
import com.microsoft.cognitive.textanalytics.model.request.keyphrases_sentiment.TextRequest;
import com.microsoft.cognitive.textanalytics.model.request.RequestDocIncludeLanguage;
import com.microsoft.cognitive.textanalytics.model.request.language.LanguageRequest;
import com.microsoft.cognitive.textanalytics.model.response.keyphrases.KeyPhrasesResponse;
import com.microsoft.cognitive.textanalytics.model.response.language.LanguageResponse;
import com.microsoft.cognitive.textanalytics.model.response.sentiment.SentimentResponse;
import com.microsoft.cognitive.textanalytics.retrofit.ServiceCall;
import com.microsoft.cognitive.textanalytics.retrofit.ServiceCallback;
import com.microsoft.cognitive.textanalytics.retrofit.ServiceRequestClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Sample Android app demonstrate the Text Analytics APIs
 *
 * This main screen shows 3 types of text analyses:
 * 1. Language detection
 * 2. Key phrases - request includes language code
 * 3. Sentiment score - request includes language code
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String mSubscriptionKey;

    private static final String TAG = MainActivity.class.getSimpleName();

    // UI
    private TextInputEditText mTextInput;
    private TextView mDetectedLanguage;
    private TextView mKeyPhrases;
    private TextView mSentimentScore;
    private ProgressDialog mProgressDialog;
    private ImageButton mClearButton;

    // Network request
    private ServiceRequestClient mRequest;
    private RequestDoc mDocument;
    private LanguageRequest mLanguageRequest;       // request for language detection
    private RequestDocIncludeLanguage mDocIncludeLanguage;
    private TextRequest mTextIncludeLanguageRequest;               // request for key phrases and sentiment analysis


    private ServiceCall mLanguageServiceCall;
    private ServiceCallback mLanguageCallback;

    private ServiceCall mKeyPhrasesCall;
    private ServiceCallback mKeyPhrasesCallback;

    private ServiceCall mSentimentCall;
    private ServiceCallback mSentimentCallback;


    //region Activity lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonPositiveSample = (Button) findViewById(R.id.btn_positive_sample);
        Button buttonNegativeSample = (Button) findViewById(R.id.btn_negative_sample);

        mTextInput = (TextInputEditText) findViewById(R.id.text_input);
        mClearButton = (ImageButton) findViewById(R.id.clear_all);

        mKeyPhrases = (TextView) findViewById(R.id.key_phrases);
        mDetectedLanguage = (TextView) findViewById(R.id.detected_language);
        mSentimentScore = (TextView) findViewById(R.id.sentiment_score);

        mTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mTextInput.getText().toString().isEmpty()) {
                    mClearButton.setVisibility(View.GONE);
                } else {
                    mClearButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                // Get input text string
                String textInputString = mTextInput.getText().toString().trim();

                // Request: text without language
                mDocument = new RequestDoc();
                mDocument.setId("1");
                mDocument.setText(textInputString);
                List<RequestDoc> documents = new ArrayList<>();
                documents.add(mDocument);
                mLanguageRequest = new LanguageRequest(documents);

                // Request: text with language hard-coded to "en" for demo purpose, not production quality
                mDocIncludeLanguage = new RequestDocIncludeLanguage();
                mDocIncludeLanguage.setId("1");
                mDocIncludeLanguage.setLanguage("en");
                mDocIncludeLanguage.setText(textInputString);
                List<RequestDocIncludeLanguage> textDocs = new ArrayList<>();
                textDocs.add(mDocIncludeLanguage);
                mTextIncludeLanguageRequest = new TextRequest(textDocs);

            }
        });

        mSubscriptionKey = Utils.getAPiKey(this); // get API key from either strings.xml or SharedPreferences

        // Set OnClick listeners
        buttonPositiveSample.setOnClickListener(this);
        buttonNegativeSample.setOnClickListener(this);
        mClearButton.setOnClickListener(this);
        ((TextView) findViewById(R.id.detect_language)).setOnClickListener(this);
        ((TextView) findViewById(R.id.get_key_phrases)).setOnClickListener(this);
        ((TextView) findViewById(R.id.get_sentiment_score)).setOnClickListener(this);

        ((Button) findViewById(R.id.goto_detected_topics)).setOnClickListener(this);

        // Request for network calls
        mRequest = new ServiceRequestClient(mSubscriptionKey);
    }

    /**
     * Clean up UI and network requests onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        // Cancel all network calls
        if (mLanguageServiceCall != null && !mLanguageServiceCall.isCancelled()) {
            mLanguageServiceCall.cancel();
        }
        if (mKeyPhrasesCall != null && !mKeyPhrasesCall.isCancelled()) {
            mKeyPhrasesCall.cancel();
        }
        if (mSentimentCall != null && !mSentimentCall.isCancelled()) {
            mSentimentCall.cancel();
        }
        // Dismiss dialog
        dismissProgressDialog();
    }

    //endregion

    /**
     * Handles UI onclick events
     *
     * @param v View
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_positive_sample:
                loadSampleText(getString(R.string.positive_sample));
                break;
            case R.id.btn_negative_sample:
                loadSampleText(getString(R.string.negative_sample));
                break;
            case R.id.clear_all:
                clearText();
                break;
            case R.id.detect_language:
                if (Utils.hasApiKey(this, mSubscriptionKey) && Utils.hasText(this, mTextInput)) {
                    getLanguages();
                }
                break;
            case R.id.get_key_phrases:
                if (Utils.hasApiKey(this, mSubscriptionKey) && Utils.hasText(this, mTextInput)) {
                    getKeyPhrases();
                }
                break;
            case R.id.get_sentiment_score:
                if (Utils.hasApiKey(this, mSubscriptionKey) && Utils.hasText(this, mTextInput)) {
                    getSentimentScore();
                }
                break;
            case R.id.goto_detected_topics:
                if (Utils.hasApiKey(this, mSubscriptionKey)) {
                    startActivity(DetectedTopicActivity.createIntent(this, mSubscriptionKey));
                } else {
                    Toast.makeText(this, getString(R.string.need_API_key), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //region UI
    private void loadSampleText(String sampleText) {
        mTextInput.requestFocus();
        mTextInput.setText(sampleText);

        mTextInput.clearFocus();        // clear focus once sample text is entered
        mTextInput.setError(null);      // clear error once sample text is entered
        mClearButton.setVisibility(View.GONE);
    }

    private void clearText() {
        mTextInput.setText("");
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
    //endregion

    //region Network calls

    /**
     * Get languages from Text Analytics service
     */
    private void getLanguages() {
        showProgressDialog();

        mLanguageCallback = new ServiceCallback(mRequest.getRetrofit()) {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                LanguageResponse languageResponse = (LanguageResponse) response.body();
                if (response != null && response.isSuccessful()) {
                    String text = languageResponse.getDocuments().get(0).getDetectedLanguages().get(0).getName();
                    mDetectedLanguage.setText(text);
                }
                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
            }
        };

        try {
            mLanguageServiceCall = mRequest.getLanguagesAsync(mLanguageRequest, 3, mLanguageCallback);
        } catch (IllegalArgumentException e) {
            dismissProgressDialog();
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Get key phrases from Text Analytics service
     */
    private void getKeyPhrases() {
        showProgressDialog();

        mKeyPhrasesCallback = new ServiceCallback(mRequest.getRetrofit()) {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                KeyPhrasesResponse keyPhrasesResponse = (KeyPhrasesResponse) response.body();
                if (response != null && response.isSuccessful()) {
                    List<String> keyPhrasesStringList = keyPhrasesResponse.getDocuments().get(0).getKeyPhrases();
                    String keyPhrasesString = keyPhrasesStringList.get(0);
                    for (int i = 1; i < keyPhrasesStringList.size(); i++) {
                        keyPhrasesString += ", " + keyPhrasesStringList.get(i);
                    }
                    mKeyPhrases.setText(keyPhrasesString);
                }
                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
            }
        };

        try {
            mKeyPhrasesCall = mRequest.getKeyPhrasesAsync(mTextIncludeLanguageRequest, mKeyPhrasesCallback);
        } catch (IllegalArgumentException e) {
            dismissProgressDialog();
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Get sentiment score
     */
    private void getSentimentScore() {
        showProgressDialog();

        mSentimentCallback = new ServiceCallback(mRequest.getRetrofit()) {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                SentimentResponse sentimentResponse = (SentimentResponse) response.body();
                if (response != null && response.isSuccessful()) {
                    mSentimentScore.setText(sentimentResponse.getDocuments().get(0).getScore().toString());
                }
                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
            }
        };

        try {
            mSentimentCall = mRequest.getSentimentAsync(mTextIncludeLanguageRequest, mSentimentCallback);
        } catch (IllegalArgumentException e) {
            dismissProgressDialog();
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //endregion

    //region Toolbar Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_add_key) {
            Intent intent = new Intent(this, KeyActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
    //endregion

}
