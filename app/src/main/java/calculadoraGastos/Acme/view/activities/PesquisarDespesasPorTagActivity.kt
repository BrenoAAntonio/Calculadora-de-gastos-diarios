package calculadoraGastos.Acme.view.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.navigation.NavigationView
import calculadoraGastos.Acme.R
import calculadoraGastos.Acme.database.AppDatabase
import calculadoraGastos.Acme.model.Despesa
import calculadoraGastos.Acme.view.adapters.DespesaAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log

class PesquisarDespesasPorTagActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var chipGroupTags: ChipGroup
    private lateinit var rvDespesasFiltradas: RecyclerView
    private lateinit var despesaAdapter: DespesaAdapter
    private val tagDao by lazy { AppDatabase.getDatabase(this).tagDao() }
    private val despesaDao by lazy { AppDatabase.getDatabase(this).despesaDao() }
    private val despesaTagDao by lazy { AppDatabase.getDatabase(this).despesaTagDao() }
    private val selectedTagIds = mutableSetOf<Int>()
    private val _despesasFiltradas = MutableStateFlow<List<Despesa>>(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pesquisar_despesas_por_tag)

        drawerLayout = findViewById(R.id.drawerLayoutPesquisaTag)
        navigationView = findViewById(R.id.navViewPesquisaTag)
        toolbar = findViewById(R.id.toolbarPesquisaTag)
        chipGroupTags = findViewById(R.id.chipGroupTags)
        rvDespesasFiltradas = findViewById(R.id.rvDespesasFiltradas)

        setupToolbarAndDrawer()
        setupRecyclerView()
        observeDespesasFiltradas()
        loadTags()
    }

    private fun setupToolbarAndDrawer() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        rvDespesasFiltradas.layoutManager = LinearLayoutManager(this)
        despesaAdapter = DespesaAdapter(emptyList(), {}, {}, {}, false, false, this)
        rvDespesasFiltradas.adapter = despesaAdapter
    }

    private fun observeDespesasFiltradas() {
        lifecycleScope.launch {
            _despesasFiltradas.collectLatest { despesas ->
                Log.d("PesquisaTags", "StateFlow emitiu uma nova lista, tamanho: ${despesas.size}")
                Log.d("PesquisaTags", "Conteúdo da lista emitida: ${despesas.joinToString { it.id.toString() }}")
                withContext(Dispatchers.Main) {
                    despesaAdapter.atualizarLista(despesas)
                    Log.d("PesquisaTags", "Adapter atualizado, total de itens: ${despesaAdapter.itemCount}")
                }
            }
        }
    }

    private fun loadTags() {
        lifecycleScope.launch {
            tagDao.getAll().collectLatest { tags ->
                withContext(Dispatchers.Main) {
                    chipGroupTags.removeAllViews()
                    tags.forEach { tag ->
                        val chip = Chip(this@PesquisarDespesasPorTagActivity)
                        chip.text = tag.nome
                        chip.isCheckable = true
                        chip.setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) {
                                selectedTagIds.add(tag.id)
                            } else {
                                selectedTagIds.remove(tag.id)
                            }
                            filterDespesasByTags()
                        }
                        chipGroupTags.addView(chip)
                    }
                }
            }
        }
    }

    private fun filterDespesasByTags() {
        lifecycleScope.launch {
            val todasDespesas = mutableSetOf<Despesa>()
            if (selectedTagIds.isNotEmpty()) {
                Log.d("PesquisaTags", "Tags selecionadas: ${selectedTagIds.joinToString()}")

                for (tagId in selectedTagIds) {
                    Log.d("PesquisaTags", "Buscando despesas para a tag $tagId")
                    despesaTagDao.getDespesasForTag(tagId).collectLatest { despesaTags ->
                        Log.d("PesquisaTags", "Número de DespesaTag encontradas para a tag $tagId: ${despesaTags.size}")
                        despesaTags.forEach { despesaTag ->
                            val despesa = withContext(Dispatchers.IO) {
                                despesaDao.buscarDespesaPorId(despesaTag.despesaId)
                            }
                            despesa?.let {
                                todasDespesas.add(it)
                                Log.d("PesquisaTags", "Despesa adicionada à lista: ${it.id}")
                            }
                        }
                        Log.d("PesquisaTags", "Lista de despesas após processar tag $tagId: ${todasDespesas.joinToString { it.id.toString() }}")
                        _despesasFiltradas.value = todasDespesas.toList()
                        Log.d("PesquisaTags", "StateFlow emitido (dentro do loop), tamanho: ${_despesasFiltradas.value.size}, conteúdo: ${_despesasFiltradas.value.joinToString { it.id.toString() }}")
                    }
                }
                Log.d("PesquisaTags", "Total de despesas encontradas (final): ${todasDespesas.size}")
                Log.d("PesquisaTags", "Lista final emitida para StateFlow: ${todasDespesas.toList().joinToString { it.id.toString() }}")

            } else {
                _despesasFiltradas.value = emptyList()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@PesquisarDespesasPorTagActivity,
                        "Selecione ao menos uma tag para filtrar.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_add_category -> startActivity(Intent(this, AdicionarCategoriaActivity::class.java))
            R.id.nav_list_expenses -> startActivity(Intent(this, ListaDespesasActivity::class.java))
            R.id.nav_calculator -> startActivity(Intent(this, CalculadoraActivity::class.java))
            R.id.nav_home -> startActivity(Intent(this, MainActivity::class.java))
            R.id.nav_manage_tags -> startActivity(Intent(this, GerenciarTagsActivity::class.java))
            R.id.nav_search_tags -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}