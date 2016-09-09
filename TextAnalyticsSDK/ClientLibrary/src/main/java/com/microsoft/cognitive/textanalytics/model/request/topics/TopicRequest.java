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
package com.microsoft.cognitive.textanalytics.model.request.topics;

import com.microsoft.cognitive.textanalytics.model.request.RequestDoc;

import java.util.ArrayList;
import java.util.List;

/**
 * Request object for detecting topics among multiple documents
 */
public class TopicRequest {

    // RequestDoc / text record lists, min of 100 per each request
    private List<RequestDoc> documents = new ArrayList<RequestDoc>();
    // Words to be excluded from the topic detection pipeline
    private List<String> stopWords = new ArrayList<String>();
    // Phrases to be excluded from the list of returned topics
    private List<String> stopPhrases = new ArrayList<String>();

    public List<RequestDoc> getDocuments() {
        return documents;
    }

    public void setDocuments(List<RequestDoc> documents) {
        this.documents = documents;
    }

    public void addDocument(RequestDoc doc) {
        documents.add(doc);
    }

    public List<String> getStopWords() {
        return stopWords;
    }

    public void setStopWords(List<String> stopWords) {
        this.stopWords = stopWords;
    }

    public List<String> getStopPhrases() {
        return stopPhrases;
    }

    public void setStopPhrases(List<String> stopPhrases) {
        this.stopPhrases = stopPhrases;
    }
}
