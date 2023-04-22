package com.arash.altafi.chatgptsimple

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.arash.altafi.chatgptsimple.databinding.ActivityMainBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var messageList: ArrayList<Message> = arrayListOf()
    private lateinit var messageAdapter: MessageAdapter

    private var client: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val json: MediaType = "application/json; charset=utf-8".toMediaType()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        init()
    }

    private fun init() = binding.apply {
        messageAdapter = MessageAdapter(messageList)
        rvChat.adapter = messageAdapter

        btnSend.setOnClickListener {
            val question = edtMessage.text.toString().trim()
            edtMessage.setText("")
            if (question.isEmpty()) {
                Toast.makeText(this@MainActivity, "Please Write Your Question", Toast.LENGTH_SHORT)
                    .show()
            } else {
                addToChat(question, MessageState.ME)
                callAPI(question)
                tvWelcome.visibility = View.GONE
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addToChat(message: String, sentBy: MessageState) {
        runOnUiThread {
            messageList.add(Message(message, sentBy))
            messageAdapter.notifyDataSetChanged()
            binding.rvChat.smoothScrollToPosition(messageAdapter.itemCount)
        }
    }

    private fun addResponse(response: String) {
        messageList.removeAt(messageList.size - 1)
        addToChat(response, MessageState.BOT)
    }

    private fun callAPI(question: String?) {
        messageList.add(Message("Typing... ", MessageState.BOT))
        val jsonBody = JSONObject()
        try {
            jsonBody.put("model", "gpt-3.5-turbo")
            val messageArr = JSONArray()
            val obj = JSONObject()
            obj.put("role", "user")
            obj.put("content", question)
            messageArr.put(obj)
            jsonBody.put("messages", messageArr)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val requestBody = jsonBody.toString().toRequestBody(json)
        val request: Request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer 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")
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                addResponse("Failed to load response due to " + e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val jsonObject: JSONObject?
                    try {
                        jsonObject = JSONObject(response.body!!.string())
                        val jsonArray = jsonObject.getJSONArray("choices")
                        val result = jsonArray.getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content")
                        addResponse(result)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    addResponse("Failed to load response due to " + response.body.toString())
                }
            }
        })
    }

}