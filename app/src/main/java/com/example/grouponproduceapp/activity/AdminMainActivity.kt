package com.app.growceries.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.app.growceries.R
import com.app.growceries.databinding.ActivityAdminMainBinding


class AdminMainActivity : AppCompatActivity() {

    lateinit var binding : ActivityAdminMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding= ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        NavigationUI.setupWithNavController(binding.bottomMenu, Navigation.findNavController(this,
            R.id.fragmentContainerView3
        ))


    }
}