package com.nadarm.yogiyo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.PagerSnapHelper
import com.nadarm.yogiyo.R
import com.nadarm.yogiyo.databinding.FragmentMainFoodBinding
import com.nadarm.yogiyo.ui.adapter.AutoScrollCircularListAdapter
import com.nadarm.yogiyo.ui.adapter.BaseListAdapter
import com.nadarm.yogiyo.ui.model.Ad
import com.nadarm.yogiyo.ui.model.BaseItem
import com.nadarm.yogiyo.ui.model.GridListItem
import com.nadarm.yogiyo.ui.model.HorizontalListItem
import com.nadarm.yogiyo.ui.viewModel.AutoScrollAdViewModel
import com.nadarm.yogiyo.ui.viewModel.FoodCategoryViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class MainFoodFragment : BaseFragment() {

    private lateinit var binding: FragmentMainFoodBinding

//    @Inject
//    lateinit var mainAdapter: RecyclerView.Adapter<ViewHolder>
//
//    @Inject
//    lateinit var topAdAdapter: ListAdapter<BaseItem, ViewHolder>
//
//    @Inject
//    lateinit var foodCategoryAdapter: ListAdapter<BaseItem, ViewHolder>

    @Inject
    lateinit var topAdVm: AutoScrollAdViewModel.ViewModelImpl
    @Inject
    lateinit var foodCategoryVm: FoodCategoryViewModel.ViewModelImpl
    @Inject
    lateinit var bottomAdVm: AutoScrollAdViewModel.ViewModelImpl

    private val mainAdapter: BaseListAdapter = BaseListAdapter()
    private val topAdAdapter: AutoScrollCircularListAdapter by lazy {
        AutoScrollCircularListAdapter(delegate = topAdVm)
    }
    private val foodCategoryAdapter: BaseListAdapter by lazy {
        BaseListAdapter(delegate = foodCategoryVm)
    }
    private val bottomAdAdapter: AutoScrollCircularListAdapter by lazy {
        AutoScrollCircularListAdapter(delegate = bottomAdVm)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_food, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        val topAdSnapHelper = PagerSnapHelper()
        val bottomAdSnapHelper = PagerSnapHelper()

        binding.mainAdapter = mainAdapter

        mainAdapter.submitList(
            listOf(
                HorizontalListItem(topAdAdapter, topAdSnapHelper),
                BaseItem.BlankItem,
                GridListItem(foodCategoryAdapter),
                BaseItem.BlankItem,
                HorizontalListItem(bottomAdAdapter, bottomAdSnapHelper),
                BaseItem.BlankItem
            )
        )

        topAdVm.outputs.adItemList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                this.submitList(it, topAdAdapter)
            }
            .addTo(compositeDisposable)

        foodCategoryVm.outputs.foodCategoryList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                this.submitList(it, foodCategoryAdapter)
            }
            .addTo(compositeDisposable)

        bottomAdVm.outputs.adItemList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                this.submitList(it, bottomAdAdapter)
            }
            .addTo(compositeDisposable)

        topAdVm.inputs.setAdType(Ad.top)
        bottomAdVm.inputs.setAdType(Ad.bottom)

    }

    private fun submitList(newList: List<BaseItem>, adapter: BaseListAdapter) {
        adapter.submitList(newList)
    }
}
