package vn.edu.hust.studentman

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StudentAdapter(
  val students: List<StudentModel>,
  private val onEditClick: (StudentAdapter, StudentModel, Int) -> Unit,
  private val onDeleteClick: (StudentAdapter, StudentModel, Int) -> Unit
): RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {
  class StudentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val textStudentName: TextView = itemView.findViewById(R.id.text_student_name)
    val textStudentId: TextView = itemView.findViewById(R.id.text_student_id)
    val imageEdit: ImageView = itemView.findViewById(R.id.image_edit)
    val imageRemove: ImageView = itemView.findViewById(R.id.image_remove)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
    val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_student_item,
       parent, false)
    return StudentViewHolder(itemView)
  }

  override fun getItemCount(): Int = students.size

  override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
    val student = students[position]

    holder.textStudentName.text = student.studentName
    holder.textStudentId.text = student.studentId

    // Edit
    holder.imageEdit.setOnClickListener {
      onEditClick(this, student, position)
    }

    // Delete
    holder.imageRemove.setOnClickListener {
      onDeleteClick(this, student, position)
    }
  }
}