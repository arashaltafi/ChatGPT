package com.arash.altafi.chatgptsimple.ui.chat

import com.arash.altafi.chatgptsimple.base.BaseViewModel
import com.arash.altafi.chatgptsimple.domain.model.chat.ChatPostBody
import com.arash.altafi.chatgptsimple.domain.model.chat.ChatResponseBody
import com.arash.altafi.chatgptsimple.domain.repository.ChatRepository
import com.arash.altafi.chatgptsimple.utils.liveData.SingleLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : BaseViewModel() {

    private val _liveChatData = SingleLiveData<ChatResponseBody>()
    val liveChatData: SingleLiveData<ChatResponseBody>
        get() = _liveChatData

    private val _liveError = SingleLiveData<Boolean>()
    val liveError: SingleLiveData<Boolean>
        get() = _liveError

    var chatMessageList = ArrayList<ChatPostBody.Message>()

    fun sendMessage() =
        callApi(
            chatRepository.sendMessage(
                ChatPostBody(
                    // Number between -2.0 and 2.0. Positive values penalize new tokens based on their existing frequency in the text so far,
                    // decreasing the model's likelihood to repeat the same line verbatim.
                    frequencyPenalty = 0.0,
                    // The maximum number of tokens allowed for the generated answer.
                    // By default, the number of tokens the model can return will be (4096 - prompt tokens).
                    maxTokens = 4000,
                    messages = chatMessageList,
                    model = "gpt-3.5-turbo",
                    // Number between -2.0 and 2.0. Positive values penalize new tokens based on whether they appear in the text so far,
                    // increasing the model's likelihood to talk about new topics.
                    presencePenalty = 0.0,
                    // Controls randomness: Lowering results in less random completions.
                    // As the temperature approaches zero, the model will become deterministic and repetitive
                    temperature = 0.9,
                    // An alternative to sampling with temperature, called nucleus sampling,
                    // where the model considers the results of the tokens with top_p probability mass.
                    // So 0.1 means only the tokens comprising the top 10% probability mass are considered
                    // OpenAI generally recommends altering this or temperature but not both.
                    topP = 1
                )
            ),
            _liveChatData,
        ) {
            _liveError.value = true
        }

}