package dev.harshdalwadi.nasaapp.views.fragments

import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import clover.companion.app.extensions.launchAndRepeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import dev.harshdalwadi.nasaapp.R
import dev.harshdalwadi.nasaapp.adapters.ImageListAdapter
import dev.harshdalwadi.nasaapp.base.BaseFragment
import dev.harshdalwadi.nasaapp.databinding.FragmentImageListBinding
import dev.harshdalwadi.nasaapp.models.RespNasaDataItem
import dev.harshdalwadi.nasaapp.utils.ItemOffsetDecoration
import dev.harshdalwadi.nasaapp.utils.extensions.customCollect
import dev.harshdalwadi.nasaapp.utils.extensions.initialize
import dev.harshdalwadi.nasaapp.viewModels.ImageListViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch


@FlowPreview
@AndroidEntryPoint
class ImageListFragment : BaseFragment<FragmentImageListBinding>() {
    override fun findContentView() = R.layout.fragment_image_list

    private val masterNasaList: MutableList<RespNasaDataItem> = mutableListOf()
    private lateinit var mBinding: FragmentImageListBinding
    private val viewModel: ImageListViewModel by hiltNavGraphViewModels(R.id.nav_graph)
    private lateinit var imageListAdapter: ImageListAdapter
    private val spanCount = 3

    override fun onReady() {
        mBinding = getViewDataBinding()

        imageListAdapter = ImageListAdapter { position: Int, nasaDataItem: RespNasaDataItem? ->
            navigate(ImageListFragmentDirections.actionImageListFragmentToImageDetailWithSliderFragment(position))
        }

        mBinding.run {
            rvLocationList.apply {
                val mLayoutManager = StaggeredGridLayoutManager(spanCount, RecyclerView.VERTICAL)
                mLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
                initialize(manager = mLayoutManager) {
                    imageListAdapter
                }
                setHasFixedSize(false)
                addItemDecoration(ItemOffsetDecoration(context, R.dimen.item_offset))
            }
        }

    }


    override fun setViewModelObservers() {
        viewModel.run {
            launchAndRepeatWithViewLifecycle {
                launch {
                    nasaImageModel.customCollect(
                        baseFragment = this@ImageListFragment,
                        onSuccess = ::handleLocationModel,
                    )
                }
            }
        }
    }

    private fun handleLocationModel(appResponse: List<RespNasaDataItem>) {
        appResponse.let { response ->
            if (response.isNotEmpty()) {
                masterNasaList.clear()

                response.let {
                    masterNasaList.addAll(it)
                    imageListAdapter.updateList(masterNasaList)
                }
            }
        }
    }

    override fun networkError(throwable: Throwable?) {
        throwable?.printStackTrace()
        showErrorSnackBar()
    }
}