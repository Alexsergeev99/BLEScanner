package ru.alexsergeev.bluetoothscanner.viewmodel.factory

import android.bluetooth.BluetoothManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.alexsergeev.bluetoothscanner.model.repository.BLEScannerRepository
import ru.alexsergeev.bluetoothscanner.viewmodel.BLEScannerViewModel

class BLEScannerViewModelFactory(
    private val bluetoothManager: BluetoothManager,
    private val repository: BLEScannerRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BLEScannerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BLEScannerViewModel(bluetoothManager, repository) as T
        }
        throw IllegalArgumentException()
    }
}