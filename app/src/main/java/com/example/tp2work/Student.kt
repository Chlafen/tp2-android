package com.example.tp2work

class Student(var nom: String, var prenom: String, var genre: String, var isAbsent: Boolean) {
    override fun toString(): String {
        return "$nom $prenom"
    }
}

class Students {
    private var students = ArrayList<Student>()
    //singleton
    companion object {
        private var instance: Students? = null
        fun getInstance(): Students {
            if (instance == null) {
                instance = Students()
            }
            return instance as Students
        }
    }

    fun addStudent(student: Student) {
        students.add(student)
    }
    fun size(): Int {
        return students.size
    }
    fun generateStudents() {
        addStudent(Student("Amine","Bouchnak","M",false))
        addStudent(Student("Ines","Besrour","F",false))
        addStudent(Student("Imen","Kaabachi","F",true))
        addStudent(Student("Yassine", "Rabboudi", "M", true))
        addStudent(Student("Mohamed","Bouchnak","M",false))
        addStudent(Student("Jamie","Bella","F",true))
        addStudent(Student("Maria", "Doe", "F", true))
        addStudent(Student("John", "Doe", "M", false))
    }


    fun getStudents(): ArrayList<Student> {
        return students
    }

    fun contains(query: String) : Boolean {
        for (student in students) {
            if (student.toString().lowercase().contains(query.lowercase())) {
                return true
            }
        }
        return false
    }

    fun filterByAbsence(isAbsent: Boolean) : ArrayList<Student> {
        val filteredStudents = ArrayList<Student>()
        for (student in students) {
            if (student.isAbsent == isAbsent) {
                filteredStudents.add(student)
            }
        }
        return filteredStudents
    }
}