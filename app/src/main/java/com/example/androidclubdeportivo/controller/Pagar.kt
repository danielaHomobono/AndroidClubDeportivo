package com.example.androidclubdeportivo.controller

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.androidclubdeportivo.R
import com.example.androidclubdeportivo.model.Pago
import com.example.androidclubdeportivo.model.dao.PagoDAO
import com.example.androidclubdeportivo.model.dao.ClubDatabaseHelper
import com.example.androidclubdeportivo.model.dao.SociosDAO
import com.example.androidclubdeportivo.model.dao.ClienteDAO
import java.text.SimpleDateFormat
import java.util.*

class Pagar : AppCompatActivity() {
    private lateinit var dbHelper: ClubDatabaseHelper
    private lateinit var clienteDAO: ClienteDAO
    private lateinit var pagosDAO: PagoDAO
    private lateinit var sociosDAO: SociosDAO
    private lateinit var spinnerDocumentType: Spinner
    private lateinit var etDocumentNumber: EditText
    private lateinit var btnSubscribe: Button
    private lateinit var btnHome: ImageButton
    private lateinit var spinnerFormaPago: Spinner
    private lateinit var spinnerTipoCuota: Spinner
    private lateinit var textViewNombre: TextView
    private lateinit var textViewTotal: TextView
    private lateinit var textViewEfectivo: TextView
    private lateinit var textViewTipoUsuario: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagar)

        dbHelper = ClubDatabaseHelper(this)
        pagosDAO = PagoDAO(dbHelper)
        clienteDAO = ClienteDAO(dbHelper)
        sociosDAO = SociosDAO(dbHelper)

        spinnerDocumentType = findViewById(R.id.spinnerDocumentType)
        etDocumentNumber = findViewById(R.id.etDocumentNumber)
        spinnerFormaPago = findViewById(R.id.spinnerFormaPago)
        spinnerTipoCuota = findViewById(R.id.spinnerTipoCuota)
        btnSubscribe = findViewById(R.id.btnSubscribe)
        btnHome = findViewById(R.id.homeButton)
        textViewNombre = findViewById(R.id.textViewNombre)
        textViewTotal = findViewById(R.id.textViewTotal)
        textViewEfectivo = findViewById(R.id.textViewEfectivo)
        textViewTipoUsuario = findViewById(R.id.textViewTipoUsuario)

        setupSpinners()
        setupListeners()

        btnSubscribe.setOnClickListener {
            registrarPago()
        }

        btnHome.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun setupSpinners() {
        val adapterDocumentType = ArrayAdapter.createFromResource(
            this,
            R.array.document_type_array,
            android.R.layout.simple_spinner_item
        )
        adapterDocumentType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDocumentType.adapter = adapterDocumentType

        val adapterFormaPago = ArrayAdapter.createFromResource(
            this,
            R.array.forma_pago_array,
            android.R.layout.simple_spinner_item
        )
        adapterFormaPago.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFormaPago.adapter = adapterFormaPago

        val adapterTipoCuota = ArrayAdapter.createFromResource(
            this,
            R.array.tipo_cuota_array,
            android.R.layout.simple_spinner_item
        )
        adapterTipoCuota.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoCuota.adapter = adapterTipoCuota
    }

    private fun setupListeners() {
        etDocumentNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                actualizarCardView()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        spinnerDocumentType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                actualizarCardView()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerFormaPago.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                actualizarCardView()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerTipoCuota.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                actualizarCardView()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun actualizarCardView() {
        val numeroDocumento = etDocumentNumber.text.toString().trim()
        val tipoDocumento = spinnerDocumentType.selectedItem.toString()

        val idCliente = clienteDAO.getClienteByDocumento(tipoDocumento, numeroDocumento)

        if (idCliente != null) {
            val cliente = clienteDAO.getClienteById(idCliente)
            val nombreCliente = "${cliente?.nombre} ${cliente?.apellido}"
            textViewNombre.text = nombreCliente

            val esSocio = sociosDAO.esSocio(idCliente)
            textViewTipoUsuario.text = if (esSocio) "Socio" else "Cliente"

            if (esSocio) {
                spinnerTipoCuota.visibility = View.GONE
                textViewTotal.text = "Total a pagar $20000"
            } else {
                spinnerTipoCuota.visibility = View.VISIBLE
                val tipoCuota = spinnerTipoCuota.selectedItem.toString()
                val montoPago = when (tipoCuota) {
                    "Mensual" -> obtenerTotalActividades(idCliente)
                    "Diaria" -> obtenerCuotaDiaria(idCliente)
                    else -> 0.0
                }
                textViewTotal.text = "Total a pagar $${montoPago}"
            }

            textViewEfectivo.text = spinnerFormaPago.selectedItem.toString()
        } else {
            textViewNombre.text = "Nombre del Cliente"
            textViewTotal.text = "Total a pagar $0"
            textViewEfectivo.text = "Método de Pago"
            textViewTipoUsuario.text = "Tipo de Usuario"
            spinnerTipoCuota.visibility = View.GONE
        }
    }

    private fun registrarPago() {
        val numeroDocumento = etDocumentNumber.text.toString().trim()
        val tipoDocumento = spinnerDocumentType.selectedItem.toString()
        val idCliente = clienteDAO.getClienteByDocumento(tipoDocumento, numeroDocumento)

        if (idCliente != null) {
            val cliente = clienteDAO.getClienteById(idCliente)
            if (cliente != null) {
                val esSocio = sociosDAO.esSocio(idCliente)
                val montoPago = if (esSocio) {
                    20000.0
                } else {
                    val tipoCuota = spinnerTipoCuota.selectedItem.toString()
                    when (tipoCuota) {
                        "Mensual" -> obtenerTotalActividades(idCliente)
                        "Diaria" -> obtenerCuotaDiaria(idCliente)
                        else -> 0.0
                    }
                }

                val fechaPago = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val nuevoPago = Pago(0, idCliente, if (esSocio) 1 else 2, fechaPago, montoPago)
                val resultado = pagosDAO.insertarPago(nuevoPago)

                if (resultado != -1L) {
                    Toast.makeText(this, "Pago registrado con éxito", Toast.LENGTH_SHORT).show()

                    val reciboIntent = Intent(this, Recibo::class.java)
                    reciboIntent.putExtra("CLIENT_NAME", "${cliente.nombre} ${cliente.apellido}")
                    reciboIntent.putExtra("MEMBER_NUMBER", cliente.id_cliente.toString())
                    reciboIntent.putExtra("DNI_NUMBER", cliente.documento)
                    reciboIntent.putExtra("DATE", fechaPago)
                    reciboIntent.putExtra("ACTIVITY", if (esSocio) "Cuota de Socio" else "Actividades")
                    reciboIntent.putExtra("METHOD_PAYMENT", spinnerFormaPago.selectedItem.toString())
                    reciboIntent.putExtra("TOTAL", montoPago)

                    startActivity(reciboIntent)
                    finish()
                } else {
                    Toast.makeText(this, "Error al registrar el pago", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Cliente no encontrado", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Cliente no encontrado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun obtenerTotalActividades(idCliente: Int): Double {
        val db = dbHelper.readableDatabase
        var total = 0.0

        val cursor = db.rawQuery(
            """
            SELECT SUM(a.precio) AS total
            FROM Actividades a
            INNER JOIN Inscripciones i ON a.id_actividad = i.id_actividad
            WHERE i.id_cliente = ?
            """, arrayOf(idCliente.toString())
        )

        if (cursor.moveToFirst()) {
            val totalIndex = cursor.getColumnIndex("total")
            if (totalIndex != -1) {
                total = cursor.getDouble(totalIndex)
            }
        }
        cursor.close()

        return total
    }

    private fun obtenerCuotaDiaria(idCliente: Int): Double {
        return 250.0 // Cuota diaria fija de 250
    }
}