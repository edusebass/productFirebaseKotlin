package com.example.firebasekotlin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

data class Product(val name: String = "", val quantity: Int = 0)

class MainActivity : AppCompatActivity() {

    private lateinit var editTextProductName: EditText
    private lateinit var editTextProductQuantity: EditText
    private lateinit var buttonSave: Button
    private lateinit var buttonLoad: Button
    private lateinit var textViewData: TextView

    private val database = FirebaseDatabase.getInstance()
    private val productsRef = database.getReference("products")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextProductName = findViewById(R.id.editTextProductName)
        editTextProductQuantity = findViewById(R.id.editTextProductQuantity)
        buttonSave = findViewById(R.id.buttonSave)
        buttonLoad = findViewById(R.id.buttonLoad)
        textViewData = findViewById(R.id.textViewData)

        buttonSave.setOnClickListener {
            val name = editTextProductName.text.toString()
            val quantity = editTextProductQuantity.text.toString().toIntOrNull() ?: 0
            val product = Product(name, quantity)
            saveProduct(product)
        }

        buttonLoad.setOnClickListener {
            loadProducts()
        }
    }

    private fun saveProduct(product: Product) {
        val key = productsRef.push().key
        key?.let {
            productsRef.child(it).setValue(product)
        }
    }

    private fun loadProducts() {
        productsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = mutableListOf<Product>()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let { products.add(it) }
                }
                displayProducts(products)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun displayProducts(products: List<Product>) {
        val displayText = products.joinToString(separator = "\n") { "${it.name}: ${it.quantity}" }
        textViewData.text = displayText
    }
}