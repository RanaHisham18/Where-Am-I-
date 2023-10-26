package com.example.whereami

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.whereami.databinding.ActivityMainBinding
import java.util.Locale
import android.Manifest
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private val SIM_PERMISSION_CODE = 101
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                SIM_PERMISSION_CODE
            )
        } else {
            val userCountryCode = getUserCountry(this)
            binding.button.setOnClickListener {
                if (userCountryCode != null) {
                    binding.textView.text = userCountryCode
                } else {
                    binding.textView.text = "Can't get the location"
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SIM_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {

                Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun getUserCountry(context: Context): String? {
        try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val simCountry = tm.simCountryIso
            if (simCountry != null && simCountry.length == 2) { // SIM country code is available
                val countryCode = simCountry.lowercase(Locale.US)
                binding.textView.text = countryCode
                return countryCode
            } else if (tm.phoneType != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                val networkCountry = tm.networkCountryIso
                if (networkCountry != null && networkCountry.length == 2) { // network country code is available
                    val countryCode = networkCountry.lowercase(Locale.US)
                    binding.textView.text = countryCode
                    return countryCode
                }
            }
        } catch (e: Exception) {
        }
        return "N/A"
    }
}
