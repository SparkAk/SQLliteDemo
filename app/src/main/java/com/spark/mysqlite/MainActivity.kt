package com.spark.mysqlite

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var u_id: EditText? = null
    private var u_name: EditText? = null
    private var u_email: EditText? = null
    private var saveRecord: Button? = null
    private var viewRecord: Button? = null
    private var updateRecord: Button? = null
    private var deleteRecord: Button? = null
    private var listView: ListView? = null
    private var myListAdapter: MyListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        u_id = findViewById(R.id.u_id)
        u_name = findViewById(R.id.u_name)
        u_email = findViewById(R.id.u_email)
        listView = findViewById(R.id.listView)
        saveRecord = findViewById(R.id.saveRecord)
        viewRecord = findViewById(R.id.viewRecord)
        updateRecord = findViewById(R.id.updateRecord)
        deleteRecord = findViewById(R.id.deleteRecord)

        saveRecord!!.setOnClickListener {
            saveRecord()
        }

        viewRecord!!.setOnClickListener {
            viewRecord()
        }

        updateRecord!!.setOnClickListener {
            updateRecord()
        }

        deleteRecord!!.setOnClickListener {
            deleteRecord()
        }
    }


    //method for saving records in database
    fun saveRecord() {
        val id = u_id!!.text.toString()
        val name = u_name!!.text.toString()
        val email = u_email!!.text.toString()
        val databaseHandler = DatabaseHandler(this)
        if (id.trim() != "" && name.trim() != "" && email.trim() != "") {
            val status =
                databaseHandler.addEmployee(EmpModelClass(Integer.parseInt(id), name, email))
            if (status > -1) {
                Toast.makeText(applicationContext, "record save", Toast.LENGTH_LONG).show()
                u_id!!.text.clear()
                u_name!!.text.clear()
                u_email!!.text.clear()
            }
        } else {
            Toast.makeText(
                applicationContext,
                "id or name or email cannot be blank",
                Toast.LENGTH_LONG
            ).show()
        }

    }

    //method for read records from database in ListView
    fun viewRecord() {
        //creating the instance of DatabaseHandler class
        val databaseHandler = DatabaseHandler(this)
        //calling the viewEmployee method of DatabaseHandler class to read the records
        val emp: ArrayList<EmpModelClass> = databaseHandler.viewEmployee()

        val empArrayId = Array(emp.size) { "0" }
        val empArrayName = Array(emp.size) { "null" }
        val empArrayEmail = Array(emp.size) { "null" }
        for ((index, e) in emp.withIndex()) {
            empArrayId[index] = e.userId.toString()
            empArrayName[index] = e.userName
            empArrayEmail[index] = e.userEmail
        }
        //creating custom ArrayAdapter
        val myListAdapter = MyListAdapter(this, empArrayId, empArrayName, empArrayEmail)
        listView!!.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

            }

        }
        listView!!.adapter = myListAdapter
    }

    //method for updating records based on user id
    fun updateRecord() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.update_dialog, null)
        dialogBuilder.setView(dialogView)

        val edtId = dialogView.findViewById(R.id.updateId) as EditText
        val edtName = dialogView.findViewById(R.id.updateName) as EditText
        val edtEmail = dialogView.findViewById(R.id.updateEmail) as EditText

        dialogBuilder.setTitle("Update Record")
        dialogBuilder.setMessage("Enter data below")
        dialogBuilder.setPositiveButton("Update", DialogInterface.OnClickListener { _, _ ->

            val updateId = edtId.text.toString()
            val updateName = edtName.text.toString()
            val updateEmail = edtEmail.text.toString()
            //creating the instance of DatabaseHandler class
            val databaseHandler: DatabaseHandler = DatabaseHandler(this)
            if (updateId.trim() != "" && updateName.trim() != "" && updateEmail.trim() != "") {
                //calling the updateEmployee method of DatabaseHandler class to update record
                val status = databaseHandler.updateEmployee(
                    EmpModelClass(
                        Integer.parseInt(updateId),
                        updateName,
                        updateEmail
                    )
                )
                if (status > -1) {
                    Toast.makeText(applicationContext, "record update", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "id or name or email cannot be blank",
                    Toast.LENGTH_LONG
                ).show()
            }

        })
        dialogBuilder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
            //pass
        })
        val b = dialogBuilder.create()
        b.show()
    }

    //method for deleting records based on id
    fun deleteRecord() {
        //creating AlertDialog for taking user id
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.delete_dialog, null)
        dialogBuilder.setView(dialogView)

        val dltId = dialogView.findViewById(R.id.deleteId) as EditText
        dialogBuilder.setTitle("Delete Record")
        dialogBuilder.setMessage("Enter id below")
        dialogBuilder.setPositiveButton("Delete", DialogInterface.OnClickListener { _, _ ->

            val deleteId = dltId.text.toString()
            //creating the instance of DatabaseHandler class
            val databaseHandler: DatabaseHandler = DatabaseHandler(this)
            if (deleteId.trim() != "") {
                //calling the deleteEmployee method of DatabaseHandler class to delete record
                val status = databaseHandler.deleteEmployee(
                    EmpModelClass(
                        Integer.parseInt(deleteId),
                        "",
                        ""
                    )
                )
                if (status > -1) {
                    viewRecord()
                    Toast.makeText(applicationContext, "record deleted", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "id or name or email cannot be blank",
                    Toast.LENGTH_LONG
                ).show()
            }

        })
        dialogBuilder.setNegativeButton("Cancel", DialogInterface.OnClickListener { _, _ ->
            //pass
        })
        val b = dialogBuilder.create()
        b.show()
    }
}