package io.codelabs.digitutor.view

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialcab.attached.AttachedCab
import com.afollestad.materialcab.attached.destroy
import com.afollestad.materialcab.attached.isActive
import com.afollestad.materialcab.attached.isDestroyed
import com.afollestad.materialcab.createCab
import io.codelabs.digitutor.R
import io.codelabs.digitutor.core.base.BaseActivity
import io.codelabs.digitutor.core.datasource.remote.FirebaseDataSource
import io.codelabs.digitutor.core.util.AsyncCallback
import io.codelabs.digitutor.core.util.OnClickListener
import io.codelabs.digitutor.data.model.Subject
import io.codelabs.digitutor.databinding.ActivitySubjectBinding
import io.codelabs.digitutor.view.adapter.SubjectAdapter
import io.codelabs.recyclerview.GridItemDividerDecoration
import io.codelabs.recyclerview.SlideInItemAnimator
import io.codelabs.sdk.util.toast


class AddSubjectActivity : BaseActivity(), OnClickListener<Subject> {

    private lateinit var binding: ActivitySubjectBinding
    private lateinit var adapter: SubjectAdapter
    private var cab: AttachedCab? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_subject)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        adapter = SubjectAdapter(this, this)
        binding.subjectsGrid.adapter = adapter
        binding.subjectsGrid.layoutManager = LinearLayoutManager(this)
        binding.subjectsGrid.setHasFixedSize(true)
        binding.subjectsGrid.itemAnimator = SlideInItemAnimator()
        binding.subjectsGrid.addItemDecoration(GridItemDividerDecoration(this, R.dimen.divider_height, R.color.divider))
        loadData()
    }

    private fun loadData() {
        FirebaseDataSource.fetchAllSubjects(this, firestore, object : AsyncCallback<MutableList<Subject>?> {
            override fun onSuccess(response: MutableList<Subject>?) {
                if (response != null) adapter.addData(response)
            }

            override fun onComplete() {

            }

            override fun onError(error: String?) {
                toast(error, true)
            }

            override fun onStart() {

            }
        })
    }

    override fun onClick(item: Subject?, isLongClick: Boolean) {
        if (isLongClick){
            initCab()
        } else {
            //todo: handle click action for subject
            toast(item?.name)
        }
    }

    private fun initCab() {
        cab = createCab(R.id.cab_stub) {
            title(literal = getString(R.string.selected_items_hint))
            menu(R.menu.cab_menu)
            titleColor(R.color.text_primary_light)
            popupTheme(R.style.HomeTutorAppTheme_PopupOverlay)
            slideDown()

            onSelection {
                //todo: get selected items here
                true
            }
        }

    }

    override fun onBackPressed() {
        if (cab != null && !cab!!.isDestroyed()) cab.destroy()
        else super.onBackPressed()
    }
}