package com.example.listapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listapp.Adapter.ParentExpandableAdapter
import com.example.listapp.ViewModel.ListViewModel
import com.example.listapp.databinding.ActivityMainBinding
import com.example.listapp.ui.CameraActivity

/**
 * main activity which shows the home activity with the expandable recycler view
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        MyApplication.instance.applicationComponent.inject(this)
        val listViewModel = ViewModelProvider(this).get(ListViewModel::class.java)
        /**
         * a textview that shows that selected value from the drop down view of the recycler view
         */
        listViewModel.dropDownString.observe(this){
            binding.dropDown.text = "Selected Language: $it"
        }
        /**
         * button to capture a video
         */
        binding.videoCaptureBtn.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            intent.putExtra("type", "VIDEO")
            startActivity(intent)
        }
        /**
         * button to capture a photo
         */
        binding.imgCapture.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            intent.putExtra("type", "CAMERA")
            startActivity(intent)
        }
        /**
         * parent recycler view containing all the child views
         */
        val recyclerView = binding.recyclerView
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        val adapter = ParentExpandableAdapter(this, listViewModel)
        recyclerView.adapter = adapter

    }
}