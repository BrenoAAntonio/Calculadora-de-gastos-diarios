package calculadoraGastos.Acme.view.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import calculadoraGastos.Acme.R
import calculadoraGastos.Acme.controller.MainController
import calculadoraGastos.Acme.view.adapters.DespesaAdapter
import com.github.mikephil.charting.charts.PieChart
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var controller: MainController
    private lateinit var adapter: DespesaAdapter
    private lateinit var pieChart: PieChart
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        controller = MainController(this)
        setupToolbarAndDrawer()
        setupViews()
        setupListeners()
        carregarDados()
    }

    private fun setupToolbarAndDrawer() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navView)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        findViewById<ImageButton>(R.id.btnMenu).setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    private fun setupViews() {

        pieChart = findViewById<FrameLayout>(R.id.containerGrafico).let {
            PieChart(this).apply {
                description.isEnabled = false
                setUsePercentValues(true)
                setExtraOffsets(5f, 10f, 5f, 5f)
                isDrawHoleEnabled = true
                setHoleColor(Color.WHITE)
                holeRadius = 58f
                setDrawCenterText(true)
                animateY(1400)
                setNoDataText("Sem despesas cadastradas")
            }.also { chart ->
                it.addView(chart)
            }
        }

        val recyclerView = findViewById<RecyclerView>(R.id.rvDespesas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = DespesaAdapter(
            emptyList(),
            onDespesaClick = {},
            onDespesaDelete = {},
            onDespesaEdit = {},
            mostrarBotaoExcluir = false,
            mostrarBotaoEditar = false,
            this
        )
        recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        findViewById<BottomNavigationView>(R.id.bottomNavigationView).setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_list -> {
                    navigateTo(ListaDespesasActivity::class.java)
                    true
                }
                R.id.menu_calculator -> {
                    navigateTo(CalculadoraActivity::class.java)
                    true
                }
                R.id.menu_home -> {
                    true
                }
                else -> false
            }
        }

        findViewById<FloatingActionButton>(R.id.fabAddDespesa).setOnClickListener {
            navigateTo(RegistrarDespesaActivity::class.java)
        }
    }

    private fun navigateTo(activity: Class<*>) {
        startActivity(Intent(this, activity))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun carregarDados() {
        CoroutineScope(Dispatchers.Main).launch {
            val (totalGastos, categoriaMap, despesasRecentes) = controller.carregarDados()

            val categoriaCoresMap = mutableMapOf<String, Int>()
            categoriaMap.keys.forEach { categoria ->
                categoriaCoresMap[categoria] = withContext(Dispatchers.IO) {
                    controller.obterCorCategoria(categoria)
                }
            }

            findViewById<TextView>(R.id.tvTotalGastos).text = controller.formatarValorMonetario(totalGastos)
            findViewById<TextView>(R.id.tvTotalHoje).text = controller.formatarValorMonetario(totalGastos)

            pieChart.data = controller.criarDadosGrafico(categoriaMap, categoriaCoresMap)
            pieChart.invalidate()

            adapter.atualizarLista(despesasRecentes)
        }
    }

    override fun onResume() {
        super.onResume()
        findViewById<BottomNavigationView>(R.id.bottomNavigationView).selectedItemId = R.id.menu_home
        carregarDados()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_add_category -> {
                startActivity(Intent(this, AdicionarCategoriaActivity::class.java))
            }
            R.id.nav_list_expenses -> {
                navigateTo(ListaDespesasActivity::class.java)
            }
            R.id.nav_calculator -> {
                navigateTo(CalculadoraActivity::class.java)
            }
            R.id.nav_home -> {
            }
            R.id.nav_manage_tags -> { // Adicione este caso
                startActivity(Intent(this, GerenciarTagsActivity::class.java))
            }
            R.id.nav_search_tags -> { // Adicione esta linha para iniciar a nova Activity
                startActivity(Intent(this, PesquisarDespesasPorTagActivity::class.java))
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}