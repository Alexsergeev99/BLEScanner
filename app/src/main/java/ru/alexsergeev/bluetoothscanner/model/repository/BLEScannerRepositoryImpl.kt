package ru.alexsergeev.bluetoothscanner.model.repository

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.alexsergeev.bluetoothscanner.model.models.Device

class BLEScannerRepositoryImpl(
    private val bluetoothManager: BluetoothManager,
    private val context: Context
) : BLEScannerRepository {
    private val bluetoothAdapter: BluetoothAdapter = bluetoothManager.adapter
    private var scanner: BluetoothLeScanner? = null
    private var scanCallback: ScanCallback? = null

    private val _devices = MutableStateFlow<List<Device>>(emptyList())
    val devices = _devices.asStateFlow()

    override fun checkBluetoothSupport(): String {
        if (!bluetoothAdapter.isEnabled) {
            Toast.makeText(context, "Bluetooth недоступен", Toast.LENGTH_SHORT).show()
            return "Bluetooth недоступен"
        }
        Toast.makeText(context, "Bluetooth доступен", Toast.LENGTH_SHORT).show()
        return "Bluetooth доступен"
    }

    private fun hasLocationPermission(activity: Activity): Boolean {
        return ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    override fun startScanning(activity: Activity, context: Context) {
        if (!hasLocationPermission(activity)) {
            Toast.makeText(context, "Bluetooth выключен", Toast.LENGTH_SHORT).show()
            return
        }

        val scanner = bluetoothAdapter.bluetoothLeScanner ?: run {
            Toast.makeText(
                context,
                "Сканер недоступен, вероятно, у вас отключен Bluetooth",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        stopScanning()

        scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                super.onScanResult(callbackType, result)

                val device = result.device
                if (device != null && device.name != null) {
                    updateDeviceList(Device(device.name, device.address))
                }
            }

            override fun onBatchScanResults(results: List<ScanResult>) {
                super.onBatchScanResults(results)

                if (results.isNotEmpty()) {
                    results.forEach { result ->
                        val device = result.device
                        if (device != null && device.name != null) {
                            updateDeviceList(Device(device.name, device.address))
                        }
                    }
                }
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                Toast.makeText(context, "Ошибка сканирования", Toast.LENGTH_SHORT).show()
            }
        }

        scanner.startScan(scanCallback)
    }

    @SuppressLint("MissingPermission")
    override fun stopScanning() {
        scanCallback?.let {
            scanner?.stopScan(it)
            scanCallback = null
        }
    }

    override fun updateDeviceList(device: Device) {
        val currentList = _devices.value.toMutableList()
        currentList.add(device)
        _devices.value = currentList
    }

    override fun setDevices(devices: List<Device>) {
        _devices.value = devices
    }
}