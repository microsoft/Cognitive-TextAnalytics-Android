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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.cognitive.textanalytic.sample.R;

public class KeyActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mPrefsEditor;
    public static final String API_KEY = "api_key";
    private TextInputEditText mAPIKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mPrefsEditor = mSharedPreferences.edit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView instructionsTextView = (TextView) findViewById(R.id.key_instructions);
        instructionsTextView.setMovementMethod(LinkMovementMethod.getInstance());

        mAPIKey = (TextInputEditText) findViewById(R.id.input_api_key);

        ImageButton save = (ImageButton) findViewById(R.id.button_save);
        ImageButton delete = (ImageButton) findViewById(R.id.button_delete);

        if (!getApiKeyFromPrefs().isEmpty()) {
            mAPIKey.setText(getApiKeyFromPrefs());
        }

        save.setOnClickListener(this);
        delete.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        String APIKeyInputString = mAPIKey.getText().toString().trim();
        switch (v.getId()) {
            case R.id.button_save:
                if (APIKeyInputString.length() == 0 || APIKeyInputString.isEmpty()) {
                    mAPIKey.setError("Please enter a valid API key");
                } else {
                    saveApiKeyToPrefs(APIKeyInputString);
                }
                break;
            case R.id.button_delete:
                deleteApiKeyFromPrefs();
                break;
        }
    }

    private void saveApiKeyToPrefs(String apikeyString) {
        mPrefsEditor.putString(API_KEY, apikeyString);
        mPrefsEditor.apply();
        Toast.makeText(KeyActivity.this, "API key saved", Toast.LENGTH_SHORT).show();
    }


    private void deleteApiKeyFromPrefs() {
        mPrefsEditor.remove(API_KEY);
        mPrefsEditor.apply();
        mAPIKey.setText("");
        Toast.makeText(KeyActivity.this, "API key deleted", Toast.LENGTH_SHORT).show();
    }

    private String getApiKeyFromPrefs() {
        return mSharedPreferences.getString(API_KEY, "");
    }

}
