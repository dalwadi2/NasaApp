package dev.harshdalwadi.nasaapp.views.fragments

import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import clover.companion.app.extensions.launchAndRepeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import dev.harshdalwadi.nasaapp.R
import dev.harshdalwadi.nasaapp.adapters.ImageListAdapter
import dev.harshdalwadi.nasaapp.base.BaseFragment
import dev.harshdalwadi.nasaapp.databinding.FragmentImageListBinding
import dev.harshdalwadi.nasaapp.models.RespNasaDataItem
import dev.harshdalwadi.nasaapp.utils.GridSpacingItemDecoration
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
    val spanCount = 3

    private val decorator: GridSpacingItemDecoration by lazy {
        val spanCount = spanCount
        val spacing = resources.getDimensionPixelSize(R.dimen.margin_default_16dp)
        val includeEdge = false
        GridSpacingItemDecoration(spanCount, spacing, includeEdge)
    }

    override fun onReady() {
        mBinding = getViewDataBinding()

        imageListAdapter = ImageListAdapter { position: Int, nasaDataItem: RespNasaDataItem? ->

        }

        mBinding.run {
            rvLocationList.apply {
                initialize(manager = GridLayoutManager(context, spanCount)) {
                    imageListAdapter
                }
                addItemDecoration(decorator)
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