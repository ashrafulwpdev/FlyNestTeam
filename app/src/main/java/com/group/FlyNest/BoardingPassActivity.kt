package com.group.FlyNest.activity

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.group.FlyNest.databinding.ActivityBoardingPassBinding
import com.group.FlyNest.model.Flight
import com.group.FlyNest.model.Passenger
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class BoardingPassActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBoardingPassBinding
    private lateinit var flight: Flight
    private lateinit var passenger: Passenger
    private lateinit var bookingReference: String
    private lateinit var gate: String
    private lateinit var seat: String
    private lateinit var seatClass: String
    private var passengersCount: Int = 1
    private val STORAGE_PERMISSION_CODE = 100
    private var qrBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardingPassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        flight = intent.getParcelableExtra("flight") ?: run {
            Toast.makeText(this, "Flight data not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        passenger = intent.getParcelableExtra("passenger") ?: run {
            Toast.makeText(this, "Passenger data not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        bookingReference = intent.getStringExtra("bookingReference") ?: "FLY12345678"
        gate = intent.getStringExtra("gate") ?: "G12"
        seat = intent.getStringExtra("seat") ?: "12A"
        seatClass = intent.getStringExtra("seatClass") ?: "Economy"
        passengersCount = intent.getIntExtra("passengersCount", 1)

        setupUI()
        generateQRCode()
        setupDownloadButton()
        setupBookAnotherFlight()
    }

    private fun setupUI() {
        binding.passengerName.text = "Mr./Ms. ${passenger.name}"
        binding.departureTime.text = flight.departureTime
        binding.departureAirportCode.text = flight.departureAirport
        binding.departureAirportName.text = getAirportName(flight.departureAirport)
        binding.arrivalTime.text = flight.arrivalTime
        binding.arrivalAirportCode.text = flight.arrivalAirport
        binding.arrivalAirportName.text = getAirportName(flight.arrivalAirport)
        binding.flightDate.text = formatFlightDate(flight.flightDate)
        binding.boardingTime.text = calculateBoardingTime(flight.departureTime)
        binding.flightNumber.text = flight.flightNumber
        binding.gateNumber.text = gate
        binding.seatNumber.text = seat
        binding.flightClass.text = seatClass
        binding.bookingReference.text = bookingReference
        binding.airlineLogo.setImageResource(flight.airlineLogo)
    }

    private fun getAirportName(airportCode: String): String {
        return when (airportCode) {
            "DEL" -> "Indira Gandhi International Airport"
            "CCU" -> "Subhash Chandra Bose International"
            "KUL" -> "Kuala Lumpur International Airport"
            "PEN" -> "Penang International Airport"
            else -> "$airportCode Airport"
        }
    }

    private fun formatFlightDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: return dateString)
        } catch (e: Exception) {
            dateString
        }
    }

    private fun calculateBoardingTime(departureTime: String): String {
        return try {
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val departureDate = timeFormat.parse(departureTime)
            val calendar = Calendar.getInstance().apply {
                time = departureDate!!
                add(Calendar.MINUTE, -30)
            }
            timeFormat.format(calendar.time)
        } catch (e: Exception) {
            "N/A"
        }
    }

    private fun generateQRCode() {
        val qrData = "Booking: $bookingReference\nPassenger: ${passenger.name}\nFlight: ${flight.flightNumber}\nFrom: ${flight.departureAirport}\nTo: ${flight.arrivalAirport}\nDate: ${flight.flightDate}\nSeat: $seat"
        val width = 300
        val height = 300
        try {
            val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
            hints[EncodeHintType.MARGIN] = 1
            hints[EncodeHintType.ERROR_CORRECTION] = com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.H

            val bitMatrix = MultiFormatWriter().encode(qrData, BarcodeFormat.QR_CODE, width, height, hints)
            qrBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    qrBitmap?.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }

            binding.qrCodeImage.setImageBitmap(qrBitmap)
        } catch (e: Exception) {
            Toast.makeText(this, "Error generating QR code", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupDownloadButton() {
        binding.downloadButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q || checkStoragePermission()) {
                generateProfessionalPDF()
            } else {
                requestStoragePermission()
            }
        }
    }

    private fun setupBookAnotherFlight() {
        binding.bookAnotherFlight.setOnClickListener {
            finish()
        }
    }

    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            generateProfessionalPDF()
        } else {
            Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generateProfessionalPDF() {
        val pdfDocument = android.graphics.pdf.PdfDocument()
        val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val titlePaint = Paint().apply {
            color = Color.WHITE
            textSize = 32f
            isAntiAlias = true
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        }
        val headerPaint = Paint().apply {
            color = Color.BLACK
            textSize = 24f
            isAntiAlias = true
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        }
        val bodyPaint = Paint().apply {
            color = Color.BLACK
            textSize = 18f
            isAntiAlias = true
        }
        val linePaint = Paint().apply {
            color = Color.GRAY
            strokeWidth = 2f
        }
        val boxPaint = Paint().apply {
            color = Color.LTGRAY
            style = Paint.Style.FILL
        }
        val headerBackgroundPaint = Paint().apply {
            color = Color.parseColor("#1E88E5")
            style = Paint.Style.FILL
        }

        var yPosition = 30f
        val pageWidth = pageInfo.pageWidth.toFloat()
        val margin = 50f
        val contentWidth = pageWidth - 2 * margin

        canvas.drawRect(0f, 0f, pageWidth, 100f, headerBackgroundPaint)
        canvas.drawText("FlyNest", margin, yPosition + 30f, titlePaint)
        canvas.drawText("Boarding Pass", margin, yPosition + 70f, titlePaint)
        yPosition += 110f

        canvas.drawRect(margin, yPosition, margin + contentWidth, yPosition + 150f, boxPaint)
        yPosition += 30f
        canvas.drawText("Passenger Details", margin + 10f, yPosition, headerPaint)
        yPosition += 30f
        canvas.drawText("Name: ${passenger.name}", margin + 10f, yPosition, bodyPaint)
        yPosition += 25f
        canvas.drawText("Passport: ${passenger.passport}", margin + 10f, yPosition, bodyPaint)
        yPosition += 25f
        canvas.drawText("Email: ${passenger.email}", margin + 10f, yPosition, bodyPaint)
        yPosition += 25f
        canvas.drawText("Phone: ${passenger.phone}", margin + 10f, yPosition, bodyPaint)
        yPosition += 40f

        canvas.drawRect(margin, yPosition, margin + contentWidth, yPosition + 250f, boxPaint)
        yPosition += 30f
        canvas.drawText("Flight Details", margin + 10f, yPosition, headerPaint)
        yPosition += 30f
        canvas.drawText("Flight: ${flight.flightNumber}", margin + 10f, yPosition, bodyPaint)
        yPosition += 25f
        canvas.drawText("From: ${flight.departureAirport}", margin + 10f, yPosition, bodyPaint)
        yPosition += 25f
        canvas.drawText("To: ${flight.arrivalAirport}", margin + 10f, yPosition, bodyPaint)
        yPosition += 25f
        canvas.drawText("Date: ${formatFlightDate(flight.flightDate)}", margin + 10f, yPosition, bodyPaint)
        yPosition += 25f
        canvas.drawText("Departure: ${flight.departureTime}", margin + 10f, yPosition, bodyPaint)
        yPosition += 25f
        canvas.drawText("Boarding: ${calculateBoardingTime(flight.departureTime)}", margin + 10f, yPosition, bodyPaint)
        yPosition += 25f
        canvas.drawText("Gate: $gate | Seat: $seat", margin + 10f, yPosition, bodyPaint)
        yPosition += 40f

        if (qrBitmap != null) {
            canvas.drawRect(margin, yPosition, margin + contentWidth, yPosition + qrBitmap!!.height + 50f, boxPaint)
            yPosition += 30f
            canvas.drawText("Scan to Board", margin + 10f, yPosition, headerPaint)
            yPosition += 30f
            canvas.drawBitmap(qrBitmap!!, margin + 10f, yPosition, null)
        }

        pdfDocument.finishPage(page)

        val fileName = "BoardingPass_$bookingReference.pdf"
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                uri?.let {
                    contentResolver.openOutputStream(it)?.use { outputStream ->
                        pdfDocument.writeTo(outputStream)
                    }
                    Toast.makeText(this, "Boarding pass saved to Downloads", Toast.LENGTH_SHORT).show()
                }
            } else {
                val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
                pdfDocument.writeTo(FileOutputStream(file))
                Toast.makeText(this, "Boarding pass saved to Downloads", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error saving PDF: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            pdfDocument.close()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}