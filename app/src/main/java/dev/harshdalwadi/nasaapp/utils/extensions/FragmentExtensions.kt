package clover.companion.app.extensions

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * This function creates the viewModel and scopes it to the given fragment in the parameter "fragmentToWhichViewModelToBeScoped".
 * If the given fragment in the parameter "fragmentToWhichViewModelToBeScoped"  is not present in the current fragment stack
 * then the new viewModel which is scoped to current fragment is returned.
 *
 * @param fragmentName -  Fragment for which the given viewModel is scoped.
 * @param viewModel - ViewModel which needs to be instantiated.
 */
inline fun <reified VM : ViewModel> Fragment.fragmentScopedViewModel(fragmentToWhichViewModelToBeScoped: Class<out Fragment>): Lazy<VM> {
    val viewModelStoreProducer = {
        parentFragment?.childFragmentManager?.fragments?.firstOrNull {
            it.javaClass.canonicalName == fragmentToWhichViewModelToBeScoped.canonicalName
        }?.viewModelStore ?: this.viewModelStore
    }
    return createViewModelLazy(VM::class, viewModelStoreProducer)
}

/**
 * This function is to get ViewModel scoped to given navGraph ID. Given navGraphID will be searched in parent fragment nav controller backstack and
 * viewModel will be scoped to that graph.
 *
 * If for fragment, parentFragment instance is null then given "parentGraphId" will be ignored and ViewModel will be scoped to current navController graphID.
 *
 * @param parentGraphId : graphID for which viewModel needs to be scoped.
 *
 * Note :
 * @throws IllegalArgumentException – if the NavGraph is not on the back stack
 */
inline fun <reified VM : ViewModel> Fragment.parentNavGraphScopedViewModel(@IdRes parentGraphId: Int): Lazy<VM> {
    val storeProducer: () -> ViewModelStore = {
        if (parentFragment != null && parentFragment?.parentFragment != null) {
            try {
                requireParentFragment().requireParentFragment().findNavController().getViewModelStoreOwner(parentGraphId)
            } catch (illegalArgumentException: IllegalArgumentException) {
                findNavController().getViewModelStoreOwner(findNavController().graph.id)
            }
        } else {
            findNavController().getViewModelStoreOwner(findNavController().graph.id)
        }.viewModelStore
    }
    return createViewModelLazy(VM::class, storeProducer)
}

/**
 * Launches a new coroutine and repeats `block` every time the Fragment's viewLifecycleOwner
 * is in and out of `minActiveState` lifecycle state.
 */
inline fun Fragment.launchAndRepeatWithViewLifecycle(
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(minActiveState) {
            block()
        }
    }
}
