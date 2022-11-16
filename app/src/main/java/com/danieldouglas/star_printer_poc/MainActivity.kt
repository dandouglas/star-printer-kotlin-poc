package com.danieldouglas.star_printer_poc
import kotlinx.android.synthetic.main.activity_main.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.starmicronics.stario10.InterfaceType
import com.starmicronics.stario10.StarDeviceDiscoveryManager
import com.starmicronics.stario10.StarDeviceDiscoveryManagerFactory
import com.starmicronics.stario10.StarPrinter
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var _manager: StarDeviceDiscoveryManager? = null

        btCheckForPrinter.setOnClickListener {

            try {
                // Specify your printer interface types.
                val interfaceTypes: List<InterfaceType> = listOf(
//                    InterfaceType.Lan,
//                    InterfaceType.Bluetooth,
                    InterfaceType.Usb
                )

                _manager = StarDeviceDiscoveryManagerFactory.create(
                    interfaceTypes,
                    applicationContext
                )

                // Set discovery time. (option)
                _manager?.discoveryTime = 10000

                _manager?.callback = object : StarDeviceDiscoveryManager.Callback {
                    // Callback for printer found.
                    override fun onPrinterFound(printer: StarPrinter) {
                        tvPrinterDetails.text = "Found printer: ${printer.connectionSettings.identifier}."
//                        Log.d("Discovery", "Found printer: ${printer.connectionSettings.identifier}.")
                    }

                    // Callback for discovery finished. (option)
                    override fun onDiscoveryFinished() {
//                        Log.d("Discovery", "Discovery finished.")
                    }
                }

                // Start discovery.
                _manager?.startDiscovery()

                // Stop discovery.
                //_manager?.stopDiscovery()
            } catch (exception: Exception) {
                // Exception.
                Log.d("Discovery", "${exception.message}")
            }
        }
    }
}