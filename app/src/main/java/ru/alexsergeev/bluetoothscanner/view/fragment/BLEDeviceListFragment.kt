package ru.alexsergeev.bluetoothscanner.view.fragment

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.BLUETOOTH_SCAN
import android.bluetooth.BluetoothManager
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.alexsergeev.bluetoothscanner.databinding.DeviceListFragmentBinding
import ru.alexsergeev.bluetoothscanner.model.adapter.DeviceAdapter
import ru.alexsergeev.bluetoothscanner.model.repository.BLEScannerRepository
import ru.alexsergeev.bluetoothscanner.model.repository.BLEScannerRepositoryImpl
import ru.alexsergeev.bluetoothscanner.viewmodel.BLEScannerViewModel
import ru.alexsergeev.bluetoothscanner.viewmodel.BLEScannerViewModel.Companion.LOCATION_PERMISSION_REQUEST_CODE
import ru.alexsergeev.bluetoothscanner.viewmodel.factory.BLEScannerViewModelFactory
import kotlin.time.Duration.Companion.seconds

class BLEDeviceListFragment : Fragment() {

    private val bluetoothManager by lazy { requireActivity().getSystemService(BluetoothManager::class.java) }
    private val viewModel: BLEScannerViewModel by viewModels {
        BLEScannerViewModelFactory(
            bluetoothManager,
            repository
        )
    }

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var repository: BLEScannerRepository
    private lateinit var deviceListAdapter: DeviceAdapter

    private var _binding: DeviceListFragmentBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DeviceListFragmentBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = BLEScannerRepositoryImpl(bluetoothManager, requireContext())
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    viewModel.startScanning(requireActivity(), context ?: throw Exception())
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Разрешение на использование Bluetooth не предоставлено",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        setupRecyclerView()

        lifecycleScope.launch {
            deviceListAdapter.submitList(viewModel.getDevices())
            viewModel.setDevices(viewModel.getDevices())
        }

        binding.scanButton.setOnClickListener {
            lifecycleScope.launch {
                binding.progressBar.isVisible = true
                delay(3.seconds)
                binding.progressBar.isVisible = false
                requestBluetoothPermissions()
            }
        }
    }

    private fun requestBluetoothPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(BLUETOOTH_SCAN)
        }
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(BLUETOOTH_CONNECT)
        }
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(ACCESS_FINE_LOCATION)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionsToRequest.toTypedArray(),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            viewModel.startScanning(requireActivity(), context ?: throw Exception())
        }
    }

    private fun setupRecyclerView() {
        deviceListAdapter = DeviceAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = deviceListAdapter
    }
}