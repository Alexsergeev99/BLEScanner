package ru.alexsergeev.bluetoothscanner.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.alexsergeev.bluetoothscanner.R
import ru.alexsergeev.bluetoothscanner.view.fragment.BLEDeviceListFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, BLEDeviceListFragment())
                .commit()
        }
    }
}