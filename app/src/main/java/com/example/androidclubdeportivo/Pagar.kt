package com.example.androidclubdeportivo



import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class Pagar : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagar) // Hace referencia al archivo XML que has mostrado

        // Obtener referencia del ImageButton
        val imageButtonQR = findViewById<ImageButton>(R.id.imageButtonqr)

        // Configurar el OnClickListener para el botón QR
        imageButtonQR.setOnClickListener {
            // URL ficticia de Mercado Pago
            val url = "https://www.mercadopago.com/ficticio-pago"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent) // Abre el navegador con la URL
        }
    }
}

