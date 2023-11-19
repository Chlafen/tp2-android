package com.example.tp2work

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class MainActivity : AppCompatActivity() {
    var students:Students = Students.getInstance()
    lateinit var searchView: androidx.appcompat.widget.SearchView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        students.generateStudents()
        var filteredStudents = Students.getInstance().getStudents()

        setContentView(R.layout.activity_main)


        val spinner : Spinner by lazy { findViewById(R.id.spinner) }
        val absentText = listOf<String>("Absent students", "Present students")
        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, absentText)
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinner.adapter = arrayAdapter
        var showAbsent = true
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(this@MainActivity, "Nothing selected", Toast.LENGTH_SHORT).show()
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                showAbsent = position == 0
                filteredStudents = if (position == 0) {
                    students.filterByAbsence(true)
                } else {
                    students.filterByAbsence(false)
                }
                val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
                recyclerView.adapter = StudentAdapter(filteredStudents, showAbsent)
                recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            }
        }

        searchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                filteredStudents = students.filterByAbsence(showAbsent)
                if (query != null) {
                    filteredStudents = filteredStudents.filter { it.toString().lowercase().contains(query.lowercase()) } as ArrayList<Student>
                }
                val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
                recyclerView.adapter = StudentAdapter(filteredStudents, showAbsent)
                recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filteredStudents = students.filterByAbsence(showAbsent)
                if (newText != null) {
                    filteredStudents = filteredStudents.filter { it.toString().lowercase().contains(newText.lowercase()) } as ArrayList<Student>
                }
                val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
                recyclerView.adapter = StudentAdapter(filteredStudents, showAbsent)
                recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                return true
            }
        })

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.adapter = StudentAdapter(filteredStudents, showAbsent)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}

class StudentAdapter(private val students: ArrayList<Student>, private val showAbsent: Boolean) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>(), Filterable {
    private var filteredStudents = ArrayList<Student>()
    init {
        filteredStudents = students
    }
    class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nom: TextView = itemView.findViewById(R.id.nom)
        val prenom: TextView = itemView.findViewById(R.id.prenom)
        val photo: ImageView = itemView.findViewById(R.id.studentImg)
        val checkbox: CheckBox = itemView.findViewById(R.id.check)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.student_item, parent, false)
        return StudentViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val currentStudent = filteredStudents[position]
        println("dataset changed")
        holder.nom.text = currentStudent.nom
        holder.prenom.text = currentStudent.prenom
        if (currentStudent.genre == "M") {
            holder.photo.setImageResource(R.drawable.man)
        } else {
            holder.photo.setImageResource(R.drawable.woman)
        }
        if(showAbsent == currentStudent.isAbsent) {
            holder.itemView.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        } else {
//            do not render this student
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
        }
        holder.checkbox.isChecked = currentStudent.isAbsent
        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            currentStudent.isAbsent = isChecked
            students[position].isAbsent = isChecked
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return filteredStudents.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                return FilterResults().apply {
                    if (constraint == null || constraint.isEmpty()) {
                        values = students
                    } else {
                        val filterPattern = constraint.toString().lowercase().trim()
                        values = students.filter { it.toString().lowercase().contains(filterPattern) } as ArrayList<Student>
                    }
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredStudents = results?.values as ArrayList<Student>
                notifyDataSetChanged()
            }
        }
    }
}