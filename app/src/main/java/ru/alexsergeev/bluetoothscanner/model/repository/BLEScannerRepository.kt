package ru.alexsergeev.bluetoothscanner.model.repository

import android.app.Activity
import android.content.Context
import ru.alexsergeev.bluetoothscanner.model.models.Device

interface BLEScannerRepository {
    fun checkBluetoothSupport(): String
    fun startScanning(activity: Activity, context: Context)
    fun stopScanning()
    fun setDevices(devices: List<Device>)
    fun updateDeviceList(device: Device)
}