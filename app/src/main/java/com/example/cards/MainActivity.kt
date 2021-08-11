package com.example.cards

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cards.fragments.CardsFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = CardsFragment()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
}