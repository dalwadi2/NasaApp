package dev.harshdalwadi.nasaapp.base

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import dev.harshdalwadi.nasaapp.R
import dev.harshdalwadi.nasaapp.utils.PreferenceHelper
import dev.harshdalwadi.nasaapp.utils.Utils
import dev.harshdalwadi.nasaapp.views.activities.MainActivity
import retrofit2.HttpException
import javax.inject.Inject


abstract class BaseFragment<T : ViewDataBinding> : Fragment(), BaseNavigator, RootView, LifecycleObserver, ActivityViewLifeCycleListener {
    private val TAG = BaseFragment::class.java.simpleName

    @Inject
    protected lateinit var helper: PreferenceHelper

    lateinit var mViewDataBinding: T

    private var mRootView: View? = null

    val isNetworkConnected: Boolean
        get() = mainActivity != null && mainActivity!!.isNetworkConnected()

    /**
     * @return layout resource id
     */
    @LayoutRes
    abstract fun findContentView(): Int

    fun getViewDataBinding(): T {
        return mViewDataBinding
    }

    val mainActivity: MainActivity? by lazy {
        activity as MainActivity?
    }

    /**
     * Life cycle observer which observes for activity life cycle event Lifecycle.Event.ON_CREATE.
     * Once onActivityCreated function is invoked form OS, callback is sent to required child fragments
     * by invoking performActionOnActivityViews()
     */
    private val lifecycleObserver: LifecycleObserver = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        fun onActivityCreated() {
            performActionOnActivityViews()
            activity?.lifecycle?.removeObserver(this)
        }
    }

    /**
     * Perform any operations on activity related view in this function.
     */
    override fun performActionOnActivityViews() {

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, findContentView(), container, false)
        mRootView = mViewDataBinding.root

        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onReady()
        setViewModelObservers()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.lifecycle?.addObserver(lifecycleObserver)

        lifecycle.addObserver(this)
    }

    override fun onDetach() {
        super.onDetach()
        lifecycle.removeObserver(this)
    }

    /**
     * @param actionID : Set action means where to redirect
     * @param popUpID : Screen id which is remove from stack
     */
    fun navigateWithClearTop(actionID: Int, popUpID: Int) {
        findNavController().navigate(
            actionID,
            null,
            NavOptions.Builder().setPopUpTo(
                popUpID, true
            ).build()
        )
    }

    fun navigateWithClearTop(directions: NavDirections, popUpID: Int) {
        findNavController().navigate(
            directions,
            NavOptions.Builder().setPopUpTo(
                popUpID, true
            ).build()
        )
    }

    fun navigate(actionID: Int, args: Bundle? = null) {
        findNavController().navigate(actionID, args)
    }

    fun navigate(directions: NavDirections) {
        findNavController().navigate(directions)
    }

    fun navigate(directions: NavDirections, args: Navigator.Extras) {
        findNavController().navigate(directions, args)
    }

    abstract fun onReady()

    abstract fun setViewModelObservers()

    override fun authError(throwable: Throwable?) {
        hideLoader()
        mainActivity?.showSnackBar(getString(R.string.msg_session_expired))
        mainActivity?.exitTheApp()
    }

    override fun serverError(throwable: HttpException) {
        hideLoader()
        mainActivity?.showToast("Server is in maintenance, please try again later")
    }

    override fun handleErrorCode(errorCode: Int?) {
        hideLoader()
    }

    override fun noInternetConnection() {
        hideLoader()
        mainActivity?.showToast("No internet connection")
    }

    override fun showSnackBar(message: String) {
        mainActivity?.showSnackBar(message)
    }

    override fun showErrorSnackBar() {
        mainActivity?.showErrorSnackBar()
    }

    override fun showSnackBar(message: String, showOk: Boolean) {
        mainActivity?.showSnackBar(message, showOk)
    }

    override fun showSnackBar(view: View, message: String, showOk: Boolean) {
        mainActivity?.showSnackBar(view, message, showOk)
    }

    override fun showLoader() {
        mainActivity?.showLoader()
    }

    override fun hideLoader() {
        mainActivity?.hideLoader()
    }

    override fun showKeyBoard() {
        mainActivity?.showKeyBoard()
    }

    override fun hideKeyBoard() {
        mainActivity?.hideKeyBoard()
    }

    override fun showDialogWithOneAction(
        title: String?,
        message: String?,
        positiveButton: String?,
        positiveFunction: (DialogInterface, Int) -> Unit,
    ) {
        mainActivity?.showDialogWithOneAction(title, message, positiveButton, positiveFunction)
    }

    override fun showDialogWithTwoActions(
        title: String?,
        message: String?,
        positiveName: String?,
        negativeName: String?,
        positiveFunction: (DialogInterface, Int) -> Unit,
        negativeFunction: (DialogInterface, Int) -> Unit,
    ) {
        mainActivity?.showDialogWithTwoActions(
            title,
            message,
            positiveName,
            negativeName,
            positiveFunction,
            negativeFunction
        )
    }

    override fun hideDialog() {
        mainActivity?.hideDialog()
    }

    fun TextInputEditText.onEditorAction(userActionId: Int, onNavigate: () -> Unit) {
        setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    onNavigate()
                }
            }
            false
        }
    }

}
