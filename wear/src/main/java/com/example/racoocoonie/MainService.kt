package com.example.racoocoonie

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable

class MainService : Service() {

    var nodes = listOf<Node>()

    private val messageClient by lazy { Wearable.getMessageClient(this) }

    private lateinit var sensorManager: SensorManager
    private lateinit var sensor: Sensor
    private val gyroListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val payload = event.values.toList().toString().toByteArray()
            nodes.forEach {
                messageClient.sendMessage(it.id, START_ACTIVITY_PATH, payload)
            }

            val gx = "%.2f".format(event.values[0])
            val gy = "%.2f".format(event.values[1])
            val gz = "%.2f".format(event.values[2])
            Log.d("ygdanchoi-test", "Gx: ${gx}\nGy: ${gy}\nGz: ${gz}")
        }

        override fun onAccuracyChanged(sensor: Sensor?, acc: Int) = Unit
    }

    override fun onCreate() {
        super.onCreate()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        Thread {
            val nodeListTask: Task<List<Node>> =
                Wearable.getNodeClient(applicationContext).connectedNodes
            val result = Tasks.await(nodeListTask)
            nodes = result
        }.start()

        sensorManager.registerListener(gyroListener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onDestroy() {
        super.onDestroy()

        sensorManager.unregisterListener(gyroListener);
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationChannel = NotificationChannel("racoocoonie", "Racoocoonie", NotificationManager.IMPORTANCE_DEFAULT)
        val notificationService = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationService.createNotificationChannel(notificationChannel)

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val notification = NotificationCompat.Builder(this, "racoocoonie")
            .setContentTitle("Racoocoonie")
            .setContentText("getting gyroscope data")
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)
        return START_NOT_STICKY
    }

    companion object {
        private const val START_ACTIVITY_PATH = "/start-activity"
    }
}