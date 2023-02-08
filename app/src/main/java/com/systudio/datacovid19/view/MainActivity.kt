package com.systudio.datacovid19.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.systudio.datacovid19.R
import com.systudio.datacovid19.databinding.ActivityMainBinding
import com.systudio.datacovid19.view.fragment.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var bottomNav : BottomNavigationView
    private var activeFragment : Fragment = BarchartFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        supportFragmentManager.beginTransaction().apply {
//
//            add(R.id.container,StackBarFragment(),"StackBar").hide(StackBarFragment())
//            add(R.id.container,LineChartFragment(),"LineChart").hide(LineChartFragment())
//            add(R.id.container,PieChartFragment(),"PieChart").hide(PieChartFragment())
//            add(R.id.container,CombineChartFragment(),"Combine").hide(CombineChartFragment())
//            add(R.id.container,BarchartFragment(),"Barchart")
//        }.commit()
//        bottomNav = findViewById(R.id.main_bottom_navigation)
//        botomNav()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_fragment_nav) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.main_bottom_navigation)
        bottomNavigationView.setupWithNavController(navController)

        if (!navController.popBackStack(R.id.barchartFragment,false)){
            navController.navigateUp()
        }
//
//        appBarConfiguration = AppBarConfiguration(
//            setOf(R.id.barchartFragment,R.id.stackBarFragment,R.id.linechart_navigation,R.id.pieChartFragment,R.id.combineChartFragment)
//        )
        //setupActionBarWithNavController(navController,appBarConfiguration)
//        val bottomNav : BottomNavigationView = findViewById(R.id.main_bottom_navigation)
//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_fragment_nav) as NavHostFragment
//        val navController = navHostFragment.navController
//        bottomNav.setupWithNavController(navController)
//        bottomNav.setOnItemReselectedListener {
//            val reselectedId = it.itemId
//            navController.popBackStack(reselectedId,inclusive = false)
//        }
//        bottomNav.apply {
//            navController.let {
//                NavigationUI.setupWithNavController(this,navController)
//                setOnItemSelectedListener {
//                    NavigationUI.onNavDestinationSelected(it,navController)
//                    true
//                }
//                setOnItemReselectedListener {
//                    navController.popBackStack(it.itemId,false)
//                }
//            }
//        }
//        main_bottom_navigation.setupWithNavController(navController)
    }

//    override fun onSupportNavigateUp(): Boolean {
//        return navController.navigateUp(appBarConfiguration)
//    }

    private fun loadFragment(fragment:Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.commit()

    }

//    private fun botomNav(){
//        bottomNav.setOnNavigationItemSelectedListener {
//            when (it.itemId){
//                R.id.barchart_navigation -> {
//                    supportFragmentManager.beginTransaction().hide(activeFragment).show(BarchartFragment()).commit()
//                    activeFragment = BarchartFragment()
//                    true
//                }
//                R.id.stackchart_navigation -> {
//                    supportFragmentManager.beginTransaction().hide(activeFragment).show(StackBarFragment()).commit()
//                    activeFragment = StackBarFragment()
//                    true
//                }
//                R.id.linechart_navigation -> {
//                    supportFragmentManager.beginTransaction().hide(activeFragment).show(LineChartFragment()).commit()
//                    activeFragment = LineChartFragment()
//                    true
//                }
//                R.id.piechart_navigation -> {
//                    supportFragmentManager.beginTransaction().hide(activeFragment).show(PieChartFragment()).commit()
//                    activeFragment = PieChartFragment()
//                    true
//                }
//                R.id.combine_navigation -> {
//                    supportFragmentManager.beginTransaction().hide(activeFragment).show(CombineChartFragment()).commit()
//                    activeFragment = CombineChartFragment()
//                    true
//                }
//                else-> false
//            }
//        }
//    }
}