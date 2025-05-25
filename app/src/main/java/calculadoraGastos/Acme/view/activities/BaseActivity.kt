package calculadoraGastos.Acme.view.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import calculadoraGastos.Acme.R
import com.google.android.material.bottomnavigation.BottomNavigationView

abstract class BaseActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    protected lateinit var drawerLayout: DrawerLayout
    protected lateinit var navigationView: NavigationView
    protected lateinit var toolbar: Toolbar
    protected lateinit var toolbarTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        drawerLayout = findViewById(R.id.drawerLayoutBase)
        navigationView = findViewById(R.id.navViewBase)
        toolbar = findViewById(R.id.toolbarBase)
        toolbarTitle = findViewById(R.id.toolbarTitleBase)

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)


    }

    protected fun setToolbarTitle(title: String) {
        toolbarTitle.text = title
    }

    protected fun setContentLayout(layoutId: Int) {
        layoutInflater.inflate(layoutId, findViewById(R.id.fragmentContainer))
    }

    private fun navigateTo(activity: Class<*>) {
        startActivity(Intent(this, activity))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
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
            R.id.nav_home -> navigateTo(MainActivity::class.java)
            R.id.nav_add_category -> navigateTo(AdicionarCategoriaActivity::class.java)
            R.id.nav_manage_tags -> navigateTo(GerenciarTagsActivity::class.java)
            R.id.nav_search_tags -> navigateTo(PesquisarDespesasPorTagActivity::class.java)
            R.id.nav_list_expenses -> navigateTo(ListaDespesasActivity::class.java)
            R.id.nav_calculator -> navigateTo(CalculadoraActivity::class.java)
            R.id.nav_definir_orcamento -> navigateTo(DefinirOrcamentoActivity::class.java)
            R.id.nav_relatorio_orcamento -> navigateTo(RelatorioOrcamentoActivity::class.java)
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}