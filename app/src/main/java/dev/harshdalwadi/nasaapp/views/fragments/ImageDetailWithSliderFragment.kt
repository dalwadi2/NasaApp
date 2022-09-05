package dev.harshdalwadi.nasaapp.views.fragments

import androidx.core.view.isVisible
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.viewpager.widget.ViewPager
import clover.companion.app.extensions.launchAndRepeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import dev.harshdalwadi.nasaapp.R
import dev.harshdalwadi.nasaapp.adapters.ImageSliderAdapter
import dev.harshdalwadi.nasaapp.base.BaseFragment
import dev.harshdalwadi.nasaapp.databinding.FragmentImageDetailWithSliderBinding
import dev.harshdalwadi.nasaapp.models.RespNasaDataItem
import dev.harshdalwadi.nasaapp.utils.extensions.customCollect
import dev.harshdalwadi.nasaapp.utils.extensions.safeClickListener
import dev.harshdalwadi.nasaapp.viewModels.ImageListViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ImageDetailWithSliderFragment : BaseFragment<FragmentImageDetailWithSliderBinding>() {
    override fun findContentView() = R.layout.fragment_image_detail_with_slider

    private var selectedPosition: Int = 0
    private val masterNasaList: MutableList<RespNasaDataItem> = mutableListOf()
    private lateinit var mBinding: FragmentImageDetailWithSliderBinding
    private val viewModel: ImageListViewModel by hiltNavGraphViewModels(R.id.nav_graph)


    override fun onReady() {
        mBinding = getViewDataBinding()

        selectedPosition = ImageDetailWithSliderFragmentArgs.fromBundle(requireArguments()).selectedPosition

        mBinding.run {
            viewpagerImages.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int,
                ) {
                }

                override fun onPageSelected(position: Int) {
                    hideShowButtons(position)
                }

                override fun onPageScrollStateChanged(state: Int) {

                }
            })
            btnNext.safeClickListener {
                viewpagerImages.setCurrentItem(++viewpagerImages.currentItem, true)

            }
            btnPrev.safeClickListener {
                viewpagerImages.setCurrentItem(--viewpagerImages.currentItem, true)
            }
        }
    }

    private fun FragmentImageDetailWithSliderBinding.hideShowButtons(position: Int) {
        btnNext.isVisible = position != masterNasaList.size - 1
        btnPrev.isVisible = position != 0
    }


    override fun setViewModelObservers() {
        viewModel.run {
            launchAndRepeatWithViewLifecycle {
                launch {
                    nasaImageModel.customCollect(
                        baseFragment = this@ImageDetailWithSliderFragment,
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

                    mBinding.run {
                        viewpagerImages.adapter = ImageSliderAdapter(requireContext(), masterNasaList)
                        viewpagerImages.setCurrentItem(selectedPosition, true)
                        hideShowButtons(selectedPosition)
                    }
                }
            }
        }
    }

    override fun networkError(throwable: Throwable?) {
        throwable?.printStackTrace()
        showErrorSnackBar()
    }
}