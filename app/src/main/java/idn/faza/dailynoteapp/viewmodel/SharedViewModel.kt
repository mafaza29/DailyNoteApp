package idn.faza.dailynoteapp.viewmodel

import android.app.Application
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import idn.faza.dailynoteapp.R
import idn.faza.dailynoteapp.model.Priority
import idn.faza.dailynoteapp.model.ToDoData

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    val emptyDatabase: MutableLiveData<Boolean> = MutableLiveData(false)
    fun checkIfDatabaseEmpty(toDoData: List<ToDoData>) {
        emptyDatabase.value = toDoData.isEmpty()
    }
    val listener: AdapterView.OnItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            when (position) {
                0 -> {
                    (parent?.getChildAt(0) as TextView)
                        .setTextColor(
                            ContextCompat
                                .getColor(application, R.color.red)
                        )
                }
                1 -> {
                    (parent?.getChildAt(0) as TextView)
                        .setTextColor(
                            ContextCompat
                                .getColor(application, R.color.yellow)
                        )
                }
                2 -> {
                    (parent?.getChildAt(0) as TextView)
                        .setTextColor(
                            ContextCompat
                                .getColor(application, R.color.green)
                        )
                }
            }
        }
    }

    fun verifyDataFromUser(title: String, description: String): Boolean {
        return !(title.isEmpty() || description.isEmpty())
    }

    fun parsePriority(mPriority: String): Priority {
        return when (mPriority) {
            "High Priority" -> {
                Priority.HIGH
            }
            "Medium Priority" -> {
                Priority.MEDIUM
            }
            "LowPriority" -> {
                Priority.LOW
            }
            else -> Priority.LOW
        }
    }
}