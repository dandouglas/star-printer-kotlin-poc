package com.danieldouglas.star_printer_poc
import kotlinx.android.synthetic.main.activity_main.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.starmicronics.stario10.*
import com.starmicronics.stario10.starxpandcommand.DocumentBuilder
import com.starmicronics.stario10.starxpandcommand.MagnificationParameter
import com.starmicronics.stario10.starxpandcommand.PrinterBuilder
import com.starmicronics.stario10.starxpandcommand.StarXpandCommandBuilder
import com.starmicronics.stario10.starxpandcommand.printer.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

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

        btPrint.setOnClickListener {
            // Specify your printer connection settings.
            val settings = StarConnectionSettings(InterfaceType.Usb, "2600018120600044")
            val printer = StarPrinter(settings, applicationContext)

            val job = SupervisorJob()
            val scope = CoroutineScope(Dispatchers.Default + job)
            scope.launch {
                try {
                    // Connect to the printer.
                    printer.openAsync().await()

                    // create printing data. (Please refer to 'Create Printing data')
                    // Create printing data using StarXpandCommandBuilder object.
                    val builder = StarXpandCommandBuilder()
                    builder.addDocument(
                        DocumentBuilder()
                            .addPrinter(
                                PrinterBuilder()
//                                    .actionPrintImage(ImageParameter(logo, 406))
                                    .styleInternationalCharacter(InternationalCharacterType.Usa)
                                    .styleCharacterSpace(0.0)
                                    .styleAlignment(Alignment.Center)
                                    .actionPrintText(
                                        "Star Clothing Boutique\n" +
                                                "123 Star Road\n" +
                                                "City, State 12345\n" +
                                                "\n"
                                    )
                                    .styleAlignment(Alignment.Left)
                                    .actionPrintText(
                                        "Date:MM/DD/YYYY    Time:HH:MM PM\n" +
                                                "--------------------------------\n" +
                                                "\n"
                                    )
                                    .add(
                                        PrinterBuilder()
                                            .styleBold(true)
                                            .actionPrintText("SALE\n")
                                    )
                                    .actionPrintText(
                                        "SKU         Description    Total\n" +
                                                "300678566   PLAIN T-SHIRT  10.99\n" +
                                                "300692003   BLACK DENIM    29.99\n" +
                                                "300651148   BLUE DENIM     29.99\n" +
                                                "300642980   STRIPED DRESS  49.99\n" +
                                                "300638471   BLACK BOOTS    35.99\n" +
                                                "\n" +
                                                "Subtotal                  156.95\n" +
                                                "Tax                         0.00\n" +
                                                "--------------------------------\n"
                                    )
                                    .actionPrintText("Total     ")
                                    .add(
                                        PrinterBuilder()
                                            .styleMagnification(MagnificationParameter(2, 2))
                                            .actionPrintText("   $156.95\n")
                                    )
                                    .actionPrintText(
                                        "--------------------------------\n" +
                                                "\n" +
                                                "Charge\n" +
                                                "156.95\n" +
                                                "Visa XXXX-XXXX-XXXX-0123\n" +
                                                "\n"
                                    )
                                    .add(
                                        PrinterBuilder()
                                            .styleInvert(true)
                                            .actionPrintText("Refunds and Exchanges\n")
                                    )
                                    .actionPrintText("Within ")
                                    .add(
                                        PrinterBuilder()
                                            .styleUnderLine(true)
                                            .actionPrintText("30 days")
                                    )
                                    .actionPrintText(" with receipt\n")
                                    .actionPrintText(
                                        "And tags attached\n" +
                                                "\n"
                                    )
                                    .styleAlignment(Alignment.Center)
                                    .actionPrintBarcode(
                                        BarcodeParameter("0123456", BarcodeSymbology.Jan8)
                                            .setBarDots(3)
                                            .setHeight(5.0)
                                            .setPrintHri(true)
                                    )
                                    .actionFeedLine(1)
                                    .actionPrintQRCode(
                                        QRCodeParameter("Hello, World\n")
                                            .setLevel(QRCodeLevel.L)
                                            .setCellSize(8)
                                    )
                                    .actionCut(CutType.Partial)
                            )
                    )
// Get printing data from StarXpandCommandBuilder object.
                    val commands = builder.getCommands()
                    // Print.
                    printer.printAsync(commands).await()
                } catch (e: Exception) {
                    // Exception.
                    tvPrinterDetails.text = e.message
                    Log.d("Printing", "${e.message}")
                } finally {
                    // Disconnect from the printer.
                    printer.closeAsync().await()
                }
            }
        }
    }
}