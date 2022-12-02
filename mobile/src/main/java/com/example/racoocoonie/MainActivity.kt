package com.example.racoocoonie

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.wearable.Wearable
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.abs


class MainActivity : AppCompatActivity() {
    private val messageClient by lazy { Wearable.getMessageClient(this) }

    var xGraph = "▁▁▁▁▁▁▁▁▁▁"
    var yGraph = "▁▁▁▁▁▁▁▁▁▁"
    var zGraph = "▁▁▁▁▁▁▁▁▁▁"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textView = findViewById<TextView>(R.id.textview)

        messageClient.addListener {
            val str = String(it.data)
            val values = str.substring(1, str.length - 1).split(",").map { it.toFloat() }
            val gx = "%.5f".format(values[0])
            val gy = "%.5f".format(values[1])
            val gz = "%.5f".format(values[2])
            xGraph = xGraph.substring(1) + getBar(values[0])
            yGraph = yGraph.substring(1) + getBar(values[1])
            zGraph = zGraph.substring(1) + getBar(values[2])
            textView.text = "Gx: ${gx}\n${xGraph}\n" +
                    "Gy: ${gy}\n${yGraph}\n" +
                    "Gz: ${gz}\n${zGraph}"
            sendData(values[0], values[1], values[2])
        }
    }

    private fun sendData(gx: Float, gy: Float, gz: Float) {
        Thread {
            val data = "[$gx,$gy,$gz]"
            val url = URL("http://ec2-44-210-136-200.compute-1.amazonaws.com:3000/set?data=$data")
            (url.openConnection() as HttpURLConnection).apply {
                try {
                    inputStream.buffered().read()
                } catch (_: Exception) {
                } finally {
                    disconnect()
                }
            }
        }.start()
    }

    private fun getBar(value: Float): String {
        val inc = 0.5
        val n = abs(value)
        return when {
            0 * inc <= n && n < 1 * inc -> "▁"
            1 * inc <= n && n < 2 * inc -> "▂"
            2 * inc <= n && n < 3 * inc -> "▃"
            3 * inc <= n && n < 4 * inc -> "▄"
            4 * inc <= n && n < 5 * inc -> "▅"
            5 * inc <= n && n < 6 * inc -> "▆"
            7 * inc <= n && n < 8 * inc -> "▇"
            else -> "█"
        }
    }
}