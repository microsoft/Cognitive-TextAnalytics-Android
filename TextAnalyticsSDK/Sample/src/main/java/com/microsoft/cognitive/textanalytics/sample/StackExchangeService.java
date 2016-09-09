package com.microsoft.cognitive.textanalytics.sample;

import com.microsoft.cognitive.textanalytics.sample.model.QuestionList;

import retrofit2.http.GET;
import rx.Observable;

public interface StackExchangeService {

    // Get questions with no answers from StackOverflow
    @GET("2.2/questions/no-answers?pagesize=60&order=desc&sort=activity&site=stackoverflow")
    Observable<QuestionList> getQuestionsWithNoAnswers();

    // Get unanswered questions from StackOverflow
    @GET("2.2/questions/unanswered?pagesize=60&order=desc&sort=activity&site=stackoverflow")
    Observable<QuestionList> getQuestionsUnanswered();

}
