package idn.faza.dailynoteapp.fragment

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import idn.faza.dailynoteapp.R
import idn.faza.dailynoteapp.SwipeToDelete
import idn.faza.dailynoteapp.adapter.ListAdapter
import idn.faza.dailynoteapp.databinding.FragmentListBinding
import idn.faza.dailynoteapp.model.ToDoData
import idn.faza.dailynoteapp.room.ToDoDatabase
import idn.faza.dailynoteapp.viewmodel.SharedViewModel
import idn.faza.dailynoteapp.viewmodel.ToDoViewModel
import jp.wasabeef.recyclerview.animators.LandingAnimator
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.fragment_list.view.*

class ListFragment : Fragment(), SearchView.OnQueryTextListener {

    private val mTodoViewModel: ToDoViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private val listAdapter: ListAdapter by lazy { ListAdapter() }
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.mSharedViewModel = mSharedViewModel

        setupRecyclerView()
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_list, container, false)

        view.fab.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_addFragment)
        }


        val rvTodo = view.rv_todo
        rvTodo.apply {
            layoutManager = StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL)
            adapter = listAdapter
        }

        mTodoViewModel.getAllData.observe(viewLifecycleOwner, Observer { data ->
            mSharedViewModel.checkIfDatabaseEmpty(data)
            listAdapter.setData(data)
        })

        mSharedViewModel.emptyDatabase.observe(viewLifecycleOwner, Observer {
            showEmptyDatabaseViews(it)
        })

        // ngesetting menu
        setHasOptionsMenu(true)

        return view
    }

    private fun showEmptyDatabaseViews(emptyDatabase: Boolean) {
        if (emptyDatabase) {
            img_no_data.visibility = View.VISIBLE
            tv_no_data.visibility = View.VISIBLE
        }else{
            img_no_data.visibility = View.INVISIBLE
            tv_no_data.visibility = View.INVISIBLE
        }
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeToDelete() {

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem = listAdapter.dataList[viewHolder.adapterPosition]
                //Delete Item
                mTodoViewModel.deleteItem(deletedItem)
                listAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                restoreDeleteData(viewHolder.itemView, deletedItem)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun setupRecyclerView() {
        val rvTodo = binding.rvTodo
        rvTodo.apply {
            layoutManager = StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL)
            adapter = listAdapter
            itemAnimator = LandingAnimator().apply {
                addDuration = 300
            }
        }

        swipeToDelete(rvTodo)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_fragment_menu, menu)

        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete_all -> confirmDeleteAllData()
            R.id.menu_prioity_high -> mTodoViewModel.sortByHighPriority.observe(this, Observer {
                listAdapter.setData(it)
            })
        }
        return super.onOptionsItemSelected(item)
    }

    private fun restoreDeleteData(view: View, deleteItem: ToDoData) {
        val snackbar = Snackbar.make(view, "Deleted: '${deleteItem.title}'", Snackbar.LENGTH_LONG)
        snackbar.setAction("Undo") {
            mTodoViewModel.insertData(deleteItem)
        }
        snackbar.show()
    }

    private fun searchThroughDatabase(query: String?) {
        val searchQuery = "%$query%"

        mTodoViewModel.searchDatabase(searchQuery).observe(this, Observer { list ->
            list?.let {
                listAdapter.setData(it)
            }
        })
    }

    private fun confirmDeleteAllData() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete Everything")
            .setMessage("Are you sure want to delete everything")
            .setPositiveButton("Yes") { _, _ ->
                mTodoViewModel.deleteAlldata()
                Toast.makeText(
                    requireContext(),
                    "Succesfully Removed Everything",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("No", null)
            .create()
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchThroughDatabase(query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query != null) {
            searchThroughDatabase(query)
        }
        return true
    }
}