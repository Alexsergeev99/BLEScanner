package ru.alexsergeev.bluetoothscanner.viewmodel

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.alexsergeev.bluetoothscanner.model.models.Device
import ru.alexsergeev.bluetoothscanner.model.repository.BLEScannerRepository

class BLEScannerViewModel(
    private val bluetoothManager: BluetoothManager,
    private val repository: BLEScannerRepository,
) :
    ViewModel() {

    private val _devices = MutableStateFlow<List<Device>>(emptyList())
    val devices = _devices.asStateFlow()

    private var bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    private var scanner: BluetoothLeScanner? = null

    init {
        repository.checkBluetoothSupport()
        scanner = bluetoothAdapter?.bluetoothLeScanner
    }

    fun startScanning(activity: Activity, context: Context) {
        viewModelScope.launch {
            repository.startScanning(activity, context)
        }
    }

    fun setDevices() {
        repository.setDevices(devices.value)
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}