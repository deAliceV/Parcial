
package com.example.appmusica

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioAttributes
import android.media.SoundPool
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.padding


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions


import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text


import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.ImeAction


import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            //composicion()
            MiBotonGuardar()
            Spacer(modifier = Modifier.height(16.dp))

            MaracaButton()
            Spacer(modifier = Modifier.height(16.dp))

            TamborButton()
            Spacer(modifier = Modifier.height(16.dp))

            buscador()
            Spacer(modifier = Modifier.height(16.dp))


        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun buscador() {
    var nombre by remember { mutableStateOf("") }
    var letra by remember { mutableStateOf("") }
    val context = LocalContext.current

    var showDialog1 by remember { mutableStateOf(false) }
    if (!showDialog1) {
        TextField(
            value = nombre,
            singleLine = true,
            onValueChange = { nombre = it },
            label = { Text("Buscar canción") },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    // Aquí colocas la lógica para buscar la canción
                    letra = obtenerLetraCancion(nombre, context)
                    // Mostrar la vista de la letra de la canción
                    showDialog1 = true
                }
            )
        )
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = letra,
                onValueChange = { },
                label = { Text("$letra") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
@Composable
fun MaracaButton() {
    val context = LocalContext.current
    val maracaDetector = rememberMaracaDetector(context)
    var showDialog2 by remember { mutableStateOf(false) }


    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally){
        Spacer(modifier = Modifier.height(70.dp))
        if (!showDialog2) {
            Button(
                onClick = {
                    showDialog2 = true
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Practica Maraca")
            }
        }
        else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Insrucciones", modifier = Modifier.padding(bottom = 20.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book",
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { maracaDetector.startListening() }) {
                    Text("Comenzar")


                }
            }
        }



    }
}

@Composable
fun TamborButton() {
    val context = LocalContext.current
    val tamborDetector = rememberTamborDetector(context)
    var showDialog2 by remember { mutableStateOf(false) }


    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally){
        Spacer(modifier = Modifier.height(70.dp))
        if (!showDialog2) {
            Button(
                onClick = {
                    showDialog2 = true
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Practica Tambor")
            }
        }
        else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Insrucciones", modifier = Modifier.padding(bottom = 20.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book",
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { tamborDetector.startListening() }) {
                    Text("Comenzar")


                }
            }
        }



    }
}
@Composable
fun rememberTamborDetector(context: Context): tamborDetector {
    return remember { tamborDetector(context) }
}

class tamborDetector(private val context: Context) : SensorEventListener {
    private val sensorManager: SensorManager by lazy {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    private val accelerometer: Sensor? by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }
    private var lastUpdate: Long = 0
    private var lastShake: Long = 0
    private val SHAKE_THRESHOLD = 800
    private lateinit var soundPool: SoundPool
    private var soundId: Int = 0

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()
        soundId = soundPool.load(context, R.raw.tambor, 1)
    }

    fun startListening() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val curTime = System.currentTimeMillis()
            if ((curTime - lastUpdate) > 100) {
                val diffTime = (curTime - lastUpdate)
                lastUpdate = curTime

                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val speed = Math.abs(x + y + z - lastShake) / diffTime * 10000

                if (speed > SHAKE_THRESHOLD) {
                    lastShake = curTime
                    reproducirSonido()
                }
            }
        }
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No necesitas implementar nada aquí
    }

    private fun reproducirSonido() {
        soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f)
    }
}
@Composable
fun rememberMaracaDetector(context: Context): MaracaDetector {
    return remember { MaracaDetector(context) }
}

class MaracaDetector(private val context: Context) : SensorEventListener {
    private val sensorManager: SensorManager by lazy {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    private val accelerometer: Sensor? by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }
    private var lastUpdate: Long = 0
    private var lastShake: Long = 0
    private val SHAKE_THRESHOLD = 800
    private lateinit var soundPool: SoundPool
    private var soundId: Int = 0

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()
        soundId = soundPool.load(context, R.raw.sonidomaracas, 1)
    }

    fun startListening() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val curTime = System.currentTimeMillis()
            if ((curTime - lastUpdate) > 100) {
                val diffTime = (curTime - lastUpdate)
                lastUpdate = curTime

                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val speed = Math.abs(x + y + z - lastShake) / diffTime * 10000

                if (speed > SHAKE_THRESHOLD) {
                    lastShake = curTime
                    reproducirSonido()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No necesitas implementar nada aquí
    }

    private fun reproducirSonido() {
        soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f)
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiBotonGuardar() {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf("") }
    var letra by remember { mutableStateOf("") }

    if (!showDialog) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    showDialog = true
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Guardar Letra")
            }
            Spacer(modifier = Modifier.height(16.dp))

        }
    } else {
        Dialog(
            onDismissRequest = {
                showDialog = false
            },
            properties = DialogProperties(dismissOnClickOutside = false)
        ) {
            Box(
                modifier = Modifier.padding(16.dp)
            ) {
                Column {
                    Text("Guardar letra de la canción", modifier = Modifier.padding(bottom = 8.dp))
                    TextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = letra,
                        onValueChange = { letra = it },
                        label = { Text("Letra") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            // Aquí puedes guardar los datos en SQLite o Shared Preferences
                            guardarLetraCancion(context, nombre, letra)
                            showDialog = false
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}

private fun obtenerLetraCancion(nombreCancion: String, context: Context): String {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    // Obtener la letra de la canción del SharedPreferences utilizando el nombre de la canción como clave
    return sharedPreferences.getString(nombreCancion, "") ?: ""
}

private fun guardarLetraCancion(context: Context, nombreCancion: String, letraCancion: String) {
    // Aquí puedes guardar los datos en SQLite o Shared Preferences
    // Por simplicidad, usaremos SharedPreferences en este ejemplo
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = sharedPreferences.edit()
    editor.putString(nombreCancion, letraCancion)
    editor.apply()
    Toast.makeText(context, "Canción guardada: $nombreCancion", Toast.LENGTH_SHORT).show()
}


