package com.systudio.datacovid19.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.systudio.datacovid19.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initSpinner()
    }

    private fun initSpinner(){
        val provinsi = resources.getStringArray(R.array.provinsi)
        val spinner = mainspinner
        if (spinner!=null){
            val adapter = ArrayAdapter(this,android.R.layout.simple_spinner_item,provinsi)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val intent  = Intent(applicationContext,BarChartActivity::class.java)
                intent.putExtra("id",p2)
                startActivity(intent)

            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }
}