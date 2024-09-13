package ru.alexsergeev.bluetoothscanner.model.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.alexsergeev.bluetoothscanner.R
import ru.alexsergeev.bluetoothscanner.model.models.Device

class DeviceAdapter : ListAdapter<Device, DeviceAdapter.DeviceViewHolder>(DeviceDiffCallback()) {

    class DeviceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val deviceNameTextView: TextView = view.findViewById(R.id.device_name)
        private val deviceAddressTextView: TextView = view.findViewById(R.id.device_address)

        fun bind(device: Device) {
            deviceNameTextView.text = device.name
            deviceAddressTextView.text = device.address
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.device_item, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class DeviceDiffCallback : DiffUtil.ItemCallback<Device>() {
    override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
        return oldItem.address == newItem.address
    }

    override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
        return oldItem == newItem
    }
}