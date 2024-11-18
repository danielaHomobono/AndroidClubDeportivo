package com.example.androidclubdeportivo.controller

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.androidclubdeportivo.R
import com.example.androidclubdeportivo.model.Cliente
import com.example.androidclubdeportivo.model.Pago
import com.example.androidclubdeportivo.model.dao.PagoDAO
import com.example.androidclubdeportivo.model.dao.ClubDatabaseHelper
import com.example.androidclubdeportivo.model.dao.SociosDAO
import com.example.androidclubdeportivo.model.dao.ClienteDAO
import com.example.androidclubdeportivo.model.dao.TipoCuota
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
    private lateinit var radioGroupCuotas: RadioGroup

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
        radioGroupCuotas = findViewById(R.id.radioGroupCuotas)

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
                val formaPagoSeleccionada = parent.getItemAtPosition(position).toString()
                val numeroDocumento = etDocumentNumber.text.toString().trim()
                val tipoDocumento = spinnerDocumentType.selectedItem.toString()

                val idCliente = clienteDAO.getClienteByDocumento(tipoDocumento, numeroDocumento)

                if (idCliente != null) {
                    val esSocio = sociosDAO.esSocio(idCliente)
                    updateInstallmentsVisibility(formaPagoSeleccionada, esSocio)
                    Log.d("Pagar", "Forma de pago: $formaPagoSeleccionada, Es socio: $esSocio")
                    actualizarCardView()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }




        spinnerTipoCuota.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                actualizarCardView()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        radioGroupCuotas.setOnCheckedChangeListener { _, _ ->
            actualizarCardView()
        }
    }
    private fun updateInstallmentsVisibility(formaPago: String, esSocio: Boolean) {
        radioGroupCuotas.visibility = if (esSocio && formaPago == "Tarjeta de Crédito") View.VISIBLE else View.GONE
        Log.d("Pagar", "Visibilidad RadioGroup actualizada: ${radioGroupCuotas.visibility}")
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
                // Si es socio, el spinner de tipo de cuota se oculta
                spinnerTipoCuota.visibility = View.GONE
                val formaPago = spinnerFormaPago.selectedItem.toString()

                // Si la forma de pago es "Tarjeta", se muestra el RadioGroup para cuotas
                if (formaPago == "Tarjeta de Crédito") {
                    radioGroupCuotas.visibility = View.VISIBLE
                } else {
                    radioGroupCuotas.visibility = View.GONE
                }

                val montoPago = when {
                    formaPago == "Tarjeta de Crédito" -> {
                        when (radioGroupCuotas.checkedRadioButtonId) {
                            R.id.radioButton1Cuota -> 20000.0
                            R.id.radioButton3Cuotas -> (20000.0 / 3) * 1.2
                            R.id.radioButton6Cuotas -> (20000.0 / 6) * 1.2
                            else -> 20000.0
                        }
                    }
                    else -> 20000.0
                }
                textViewTotal.text = "Total a pagar $${String.format("%.2f", montoPago)}"
            } else {
                // Si no es socio, se muestra el Spinner de tipo de cuota
                spinnerTipoCuota.visibility = View.VISIBLE
                radioGroupCuotas.visibility = View.GONE
                val tipoCuota = spinnerTipoCuota.selectedItem.toString()
                val montoPago = when (tipoCuota) {
                    "Mensual" -> obtenerTotalActividades(idCliente)
                    "Diaria" -> obtenerCuotaDiaria(idCliente)
                    else -> 0.0
                }
                textViewTotal.text = "Total a pagar $${String.format("%.2f", montoPago)}"
            }

            textViewEfectivo.text = spinnerFormaPago.selectedItem.toString()
        } else {
            textViewNombre.text = "Nombre del Cliente"
            textViewTotal.text = "Total a pagar $0"
            textViewEfectivo.text = "Método de Pago"
            textViewTipoUsuario.text = "Tipo de Usuario"
            spinnerTipoCuota.visibility = View.GONE
            radioGroupCuotas.visibility = View.GONE
        }
    }

    private fun registrarPago() {
        Log.d("Pagar", "Iniciando proceso de pago")
        val numeroDocumento = etDocumentNumber.text.toString().trim()
        val tipoDocumento = spinnerDocumentType.selectedItem.toString()
        val idCliente = clienteDAO.getClienteByDocumento(tipoDocumento, numeroDocumento)

        if (idCliente != null) {
            val cliente = clienteDAO.getClienteById(idCliente)
            if (cliente != null) {
                val esSocio = sociosDAO.esSocio(idCliente)
                Log.d("Pagar", "Cliente encontrado. Es socio: $esSocio")

                val montoPago = calcularMontoPago(idCliente, esSocio)

                if (montoPago <= 0) {
                    Toast.makeText(this, "El monto a pagar debe ser mayor que cero", Toast.LENGTH_SHORT).show()
                    return
                }

                val fechaPago = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                Log.d("Pagar", "Monto a pagar: $montoPago, Fecha de pago: $fechaPago")

                val idCuota = pagosDAO.obtenerIdCuotaActual(idCliente)
                if (idCuota != -1) {
                    val nuevoPago = Pago(
                        id_pago = 0,
                        id_cliente = idCliente,
                        id_cuota = idCuota,
                        id_inscripcion = null,
                        fecha_pago = fechaPago,
                        monto_pago = montoPago
                    )

                    val tipoCuota = if (esSocio) {
                        TipoCuota.MENSUAL
                    } else {
                        when (spinnerTipoCuota.selectedItem.toString()) {
                            CUOTA_DIARIA -> TipoCuota.DIARIA
                            CUOTA_MENSUAL -> TipoCuota.MENSUAL
                            else -> TipoCuota.MENSUAL
                        }
                    }

                    val resultadoPago = pagosDAO.insertarPago(nuevoPago, tipoCuota, esSocio)
                    if (resultadoPago != -1L) {
                        val estadoCuota = pagosDAO.verificarEstadoCuota(idCuota)
                        Log.d("Pagar", "Pago registrado. Estado de la cuota después del pago: $estadoCuota")
                        val tipoPago = when {
                            esSocio -> "Cuota de Socio"
                            tipoCuota == TipoCuota.DIARIA -> "Cuota Diaria"
                            else -> "Actividades"
                        }
                        mostrarRecibo(cliente, fechaPago, tipoPago, montoPago)
                        Toast.makeText(this, "Pago registrado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        val mensaje = if (esSocio) "Error al registrar el pago del socio" else "Error al registrar el pago del no socio"
                        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
                        Log.e("Pagar", mensaje)
                    }
                } else {
                    val mensaje = if (esSocio) "No hay cuotas pendientes para este socio" else "No hay cuotas pendientes para este cliente no socio"
                    Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
                    Log.d("Pagar", mensaje)
                }
            } else {
                Toast.makeText(this, "Cliente no encontrado", Toast.LENGTH_SHORT).show()
                Log.e("Pagar", "Cliente no encontrado en la base de datos")
            }
        } else {
            Toast.makeText(this, "Cliente no encontrado", Toast.LENGTH_SHORT).show()
            Log.e("Pagar", "Cliente no encontrado por documento")
        }
    }


    private fun calcularMontoPago(idCliente: Int, esSocio: Boolean): Double {
        return if (esSocio) {
            val formaPago = spinnerFormaPago.selectedItem.toString()
            when {
                formaPago == TARJETA_CREDITO -> {
                    val montoBase = 20000.0
                    when (radioGroupCuotas.checkedRadioButtonId) {
                        R.id.radioButton1Cuota -> montoBase
                        R.id.radioButton3Cuotas -> (montoBase * 1.2) / 3 // Amount per installment for 3 payments
                        R.id.radioButton6Cuotas -> (montoBase * 1.2) / 6 // Amount per installment for 6 payments
                        else -> montoBase
                    }
                }
                else -> 20000.0
            }
        } else {
            val tipoCuota = spinnerTipoCuota.selectedItem.toString()
            when (tipoCuota) {
                CUOTA_MENSUAL -> obtenerTotalActividades(idCliente)
                CUOTA_DIARIA -> obtenerCuotaDiaria(idCliente)
                else -> 0.0
            }
        }
    }


    companion object {
        const val EFECTIVO = "Efectivo"
        const val QR = "QR"
        const val TARJETA_CREDITO = "Tarjeta de Crédito"
        const val TRANSFERENCIA = "Transferencia"
        const val CUOTA_MENSUAL = "Mensual"
        const val CUOTA_DIARIA = "Diaria"
    }
    private fun mostrarRecibo(cliente: Cliente, fechaPago: String, tipoActividad: String, montoPago: Double) {
        val reciboIntent = Intent(this, Recibo::class.java)
        reciboIntent.putExtra("CLIENT_NAME", "${cliente.nombre} ${cliente.apellido}")
        reciboIntent.putExtra("MEMBER_NUMBER", cliente.id_cliente.toString())
        reciboIntent.putExtra("DNI_NUMBER", cliente.documento)
        reciboIntent.putExtra("DATE", fechaPago)
        reciboIntent.putExtra("ACTIVITY", tipoActividad)
        reciboIntent.putExtra("METHOD_PAYMENT", spinnerFormaPago.selectedItem.toString())
        reciboIntent.putExtra("TOTAL", montoPago)
        startActivity(reciboIntent)
        finish()
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
        val db = dbHelper.readableDatabase
        var cantidadActividades = 0

        val query = """
    SELECT COUNT(DISTINCT i.id_actividad) AS cantidad
    FROM Inscripciones i
    WHERE i.id_cliente = ? AND DATE(i.fecha) = DATE('now')
    """

        Log.d("Pagar", "Query for obtenerCuotaDiaria: $query")
        Log.d("Pagar", "id_cliente: $idCliente")

        val cursor = db.rawQuery(query, arrayOf(idCliente.toString()))

        if (cursor.moveToFirst()) {
            val cantidadIndex = cursor.getColumnIndex("cantidad")
            if (cantidadIndex != -1) {
                cantidadActividades = cursor.getInt(cantidadIndex)
            }
        }
        cursor.close()

        val total = 250.0 * cantidadActividades
        Log.d("Pagar", "Cantidad de actividades: $cantidadActividades, Total: $total")

        return total
    }
}















