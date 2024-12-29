package vn.edu.hust.studentman

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
  lateinit var db: SQLiteDatabase
  var students: MutableList<StudentModel> = mutableListOf()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val path = filesDir.path + "/mydb"
    db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY)

    db.execSQL("""
        CREATE TABLE IF NOT EXISTS students (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            studentName TEXT NOT NULL,
            studentId TEXT NOT NULL UNIQUE
        )
    """)

    // Lấy dữ liệu tu db
    val cursor = db.rawQuery("SELECT studentName, studentId FROM students", null)
    if (cursor.moveToFirst()) {
      do {
        val name = cursor.getString(0)
        val id = cursor.getString(1)
        students.add(StudentModel(name, id))
      } while (cursor.moveToNext())
    }
    cursor.close()

    val studentAdapter = StudentAdapter(
      students,
      onEditClick = { adapter, student, position ->
        val dialogView = LayoutInflater.from(this).inflate(R.layout.layout_dialog_add, null)
        val editName = dialogView.findViewById<EditText>(R.id.editText_name)
        val editId = dialogView.findViewById<EditText>(R.id.editText_id)

        editName.setText(student.studentName)
        editId.setText(student.studentId)

        AlertDialog.Builder(this)
          .setTitle("Chỉnh sửa sinh viên")
          .setView(dialogView)
          .setPositiveButton("Lưu") { _, _ ->
            val updatedName = editName.text.toString().trim()
            val updatedId = editId.text.toString().trim()

            if (updatedName.isEmpty() || updatedId.isEmpty()) {
              Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
              return@setPositiveButton
            }

            // Cập nhật trong SQLite
            db.execSQL(
              "UPDATE students SET studentName = ?, studentId = ? WHERE studentId = ?",
              arrayOf(updatedName, updatedId, student.studentId)
            )

            students[position] = student.copy(studentName = updatedName, studentId = updatedId)
            adapter.notifyDataSetChanged()
            Toast.makeText(this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show()
          }
          .setNegativeButton("Hủy", null)
          .show()
      },
      onDeleteClick = { adapter, student, position ->
        AlertDialog.Builder(this)
          .setTitle("Xóa sinh viên")
          .setMessage("Bạn có chắc chắn muốn xóa sinh viên ${student.studentName} không?")
//          .setPositiveButton("Xóa") { _, _ ->
//            // Lưu lại sinh viên trước khi xóa để hoàn tác
//            val removedStudent = students[position]
//            students.removeAt(position)
//            adapter.notifyDataSetChanged()
//
//            Snackbar.make(
//              findViewById(R.id.recycler_view_students),
//              "Đã xóa sinh viên!",
//              Snackbar.LENGTH_LONG
//            ).setAction("Hoàn tác") {
//              // Hoàn tác xóa sinh viên
//              students.add(position, removedStudent)
//              adapter.notifyDataSetChanged()
//              Snackbar.make(
//                findViewById(R.id.recycler_view_students),
//                "Đã hoàn tác xóa sinh viên ${removedStudent.studentName}!",
//                Snackbar.LENGTH_SHORT
//              ).show()
//            }.show()
//          }
          .setPositiveButton("Xóa") { _, _ ->
            val removedStudent = students[position]

            // Xóa khỏi SQLite
            db.execSQL("DELETE FROM students WHERE studentId = ?", arrayOf(removedStudent.studentId))

            students.removeAt(position)
            adapter.notifyDataSetChanged()
            Toast.makeText(this, "Đã xóa sinh viên!", Toast.LENGTH_SHORT).show()
          }
          .setNegativeButton("Hủy", null)
          .show()
      }

    )

    findViewById<RecyclerView>(R.id.recycler_view_students).run {
      adapter = studentAdapter
      layoutManager = LinearLayoutManager(this@MainActivity)
    }

    // Them sinh vien
    findViewById<Button>(R.id.btn_add_new).setOnClickListener {
      val dialogView = LayoutInflater.from(this).inflate(R.layout.layout_dialog_add, null)
      val editName = dialogView.findViewById<EditText>(R.id.editText_name)
      val editId = dialogView.findViewById<EditText>(R.id.editText_id)

        AlertDialog.Builder(this)
        .setTitle("Thêm sinh viên mới")
        .setView(dialogView)
        .setPositiveButton("Thêm") { _, _ ->
          val name = editName.text.toString().trim()
          val id = editId.text.toString().trim()

          if (name.isEmpty() || id.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            return@setPositiveButton
          }

          // Lưu vào SQLite
          db.execSQL("INSERT INTO students (studentName, studentId) VALUES (?, ?)", arrayOf(name, id))
          students.add(StudentModel(name, id))
          Toast.makeText(this, "Thêm sinh viên mới thành công!", Toast.LENGTH_SHORT).show()
          studentAdapter.notifyDataSetChanged()
        }
        .setNegativeButton("Hủy", null)
        .show()
    }

  }

  override fun onStop() {
    db.close()
    super.onStop()
  }
}