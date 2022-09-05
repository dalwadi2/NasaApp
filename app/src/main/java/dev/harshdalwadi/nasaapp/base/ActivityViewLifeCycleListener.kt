package dev.harshdalwadi.nasaapp.base

/**
 * This interface is to give call back to fragment as soon as activity's onCreate is invoked.
 *
 * In edge case scenarios like when don't keep activities is turned on, first fragments onViewCreated is being invoked and then
 * activity onCreate is invoked. Hence trying to access activity view in onViewCreated of fragment is making activity view null.
 *
 * To solve the above scenario use this interface in base fragment, register for activity life cycle observer and give a callback to
 * child fragments in lifecycle event callback @OnLifecycleEvent(Lifecycle.Event.ON_CREATE) to avoid activity views going null in
 * fragments onViewCreated.
 *
 */
interface ActivityViewLifeCycleListener {

    /**
     * Invoke this function when life cycle event of activity Lifecycle.Event.ON_CREATE is called.
     */
    fun performActionOnActivityViews()
}