package com.gmail.safecart.trash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmail.safecart.R
import com.gmail.safecart.lists.DeletedListDb

class TrashActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var trashRv: RecyclerView

    private lateinit var adapter: TrashAdapter
    private lateinit var deletedListDb: DeletedListDb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trash)
        init()
        toolbar()

        trashRv.setHasFixedSize(true)
        trashRv.layoutManager = LinearLayoutManager(this)
        trashRv.adapter = adapter
    }
    private fun init(){
        toolbar = findViewById(R.id.trashToolbar)
        trashRv = findViewById(R.id.trashRv)
        deletedListDb = DeletedListDb(this)

        adapter = TrashAdapter(this, DeletedListDb(this).read())
    }
    private fun toolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val leftArrow = AppCompatResources.getDrawable(this, R.drawable.ic_arrow_left)
        leftArrow?.setTint(resources.getColor(R.color.colorOnPrimary, null))
        supportActionBar?.setHomeAsUpIndicator(leftArrow)
    }
    fun tempRemove(position: Int){
        val read = deletedListDb.read()
        read.removeAt(position)
        trashRv.adapter?.notifyItemRemoved(position)
        trashRv.adapter = TrashAdapter(this, read)
    }
    fun recoverTempRemoved(position: Int){
        trashRv.adapter?.notifyItemRemoved(position)
        trashRv.adapter = TrashAdapter(this, deletedListDb.read())
    }
}