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

import android.util.Log;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Custom callback, wrapper for Retrofit callback
 *
 * @param <T>
 */
public abstract class ServiceCallback<T> implements Callback<ResponseBody> {

    private static final String TAG = ServiceCallback.class.getSimpleName();
    private Retrofit retrofit;

    public ServiceCallback( Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    @Override
    public void onResponse(Call call, Response response) {

        if(!response.isSuccessful()) {
            ServiceError error = convertError(response);
            Log.e(TAG, error.toString());
        }
    }

    @Override
    public void onFailure(Call call, Throwable t) {

        if (t instanceof IOException){
            Log.e(TAG, "Error connecting to the server.");
        } else {
            Log.e(TAG, "Unexpected error - " + t.getLocalizedMessage());
        }

    }

    /**
     * Convert error body to ServiceError object
     * @param response
     * @return
     */
    public ServiceError convertError(Response response) {
        Converter<ResponseBody, ServiceError> converter = retrofit.responseBodyConverter(ServiceError.class, new Annotation[0]);
        ServiceError serviceError = null;

        try {
            serviceError = converter.convert(response.errorBody());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return serviceError;
    }
}
