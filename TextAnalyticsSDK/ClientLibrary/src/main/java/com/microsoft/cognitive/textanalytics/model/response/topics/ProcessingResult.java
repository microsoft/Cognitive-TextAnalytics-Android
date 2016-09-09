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
package com.microsoft.cognitive.textanalytics.model.response.topics;

import java.util.ArrayList;
import java.util.List;

/**
 * Detailed process result
 */
public class ProcessingResult {

    private List<Topic> topics = new ArrayList<Topic>();
    private List<TopicAssignment> topicAssignments = new ArrayList<TopicAssignment>();

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public List<TopicAssignment> getTopicAssignments() {
        return topicAssignments;
    }

    public void setTopicAssignments(List<TopicAssignment> topicAssignments) {
        this.topicAssignments = topicAssignments;
    }

    public List<String> getTopicStrings() {
        List<String> topicStrings = new ArrayList<>();
        for (Topic topic : topics) {
            topicStrings.add(topic.getKeyPhrase());
        }
        return topicStrings;
    }

}