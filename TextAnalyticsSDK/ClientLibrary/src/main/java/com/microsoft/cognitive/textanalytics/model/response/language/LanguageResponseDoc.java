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
package com.microsoft.cognitive.textanalytics.model.response.language;

import java.util.ArrayList;
import java.util.List;

/**
 * One single doc returned in the http call response
 */
public class LanguageResponseDoc {

    private String id;
    private List<Language> detectedLanguages = new ArrayList<Language>();

    /**
     * No args constructor for use in serialization
     *
     */
    public LanguageResponseDoc() {
    }

    /**
     *
     * @param id
     * @param detectedLanguages
     */
    public LanguageResponseDoc(String id, List<Language> detectedLanguages) {
        this.id = id;
        this.detectedLanguages = detectedLanguages;
    }

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The detectedLanguages
     */
    public List<Language> getDetectedLanguages() {
        return detectedLanguages;
    }

    /**
     *
     * @param detectedLanguages
     * The detectedLanguages
     */
    public void setDetectedLanguages(List<Language> detectedLanguages) {
        this.detectedLanguages = detectedLanguages;
    }

}