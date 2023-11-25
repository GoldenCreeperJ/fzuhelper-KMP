package repository

import data.Feedback.FeelbackDetail.FeedbackDetail
import data.Feedback.SubmitNewFeedBack.FeedbackSubmit
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.http.Parameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FeedbackRepository(val client: HttpClient) {

    fun submitNewFeedBack(content :String,type:Int):Flow<FeedbackSubmit>{
        return flow {
            val response = client.submitForm(
                "/feedback/new",
                formParameters = Parameters.build {
                    append("content",content)
                    append("type",type.toString())
                }
            ).body<FeedbackSubmit>()
            emit(response)
        }
    }

    fun getFeedbackDetail(id:Int ):Flow<FeedbackDetail>{
        return flow {
            val response = client.get("/feedback/detail/${id}").body<FeedbackDetail>()
            emit(response)
        }
    }
}