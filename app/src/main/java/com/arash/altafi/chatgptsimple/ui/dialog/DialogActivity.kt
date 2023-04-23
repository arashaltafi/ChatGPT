package com.arash.altafi.chatgptsimple.ui.dialog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.arash.altafi.chatgptsimple.databinding.ActivityDialogBinding
import com.arash.altafi.chatgptsimple.model.DialogModel
import com.arash.altafi.chatgptsimple.ui.chat.ChatActivity

class DialogActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityDialogBinding.inflate(layoutInflater)
    }
    private lateinit var dialogAdapter: DialogAdapter
    private var dialogList: ArrayList<DialogModel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        init()
    }

    private fun init() = binding.apply {
        flNewChat.setOnClickListener {
            startActivity(Intent(this@DialogActivity, ChatActivity::class.java))
        }

        dialogList.add(DialogModel(1, "name 1", 1))
        dialogList.add(DialogModel(2, "name 2", 2))
        dialogList.add(DialogModel(3, "name 3", 3))
        dialogList.add(DialogModel(4, "name 4", 4))
        dialogAdapter = DialogAdapter(dialogList)
        rvDialogs.adapter = dialogAdapter
    }

}