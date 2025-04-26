package calculadoraGastos.Acme

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnRegistrarDespesa: Button = findViewById(R.id.btnRegistrarDespesa)
        btnRegistrarDespesa.setOnClickListener {
            val intent = Intent(this, RegistrarDespesaActivity::class.java)
            startActivity(intent)
        }

        val btnListaDespesas: Button = findViewById(R.id.btnListaDespesas)
        btnListaDespesas.setOnClickListener {
            val intent = Intent(this, ListaDespesasActivity::class.java)
            startActivity(intent)
        }

        val btnCalculadora: Button = findViewById(R.id.btnCalculadora)
        btnCalculadora.setOnClickListener {
            val intent = Intent(this, CalculadoraActivity::class.java)
            startActivity(intent)
        }
    }
}
