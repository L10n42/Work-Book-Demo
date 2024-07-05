package com.kappdev.wordbook.core.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.navigation.NavBackStackEntry

fun AnimatedContentTransitionScope<NavBackStackEntry>.slideInLeft(): EnterTransition {
    return slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
        animationSpec = slideSpec()
    )
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.slideInRight(): EnterTransition {
    return slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
        animationSpec = slideSpec()
    )
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.slideOutRight(): ExitTransition {
    return slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
        animationSpec = slideSpec()
    )
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.slideOutLeft(): ExitTransition {
    return slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
        animationSpec = slideSpec()
    )
}


fun AnimatedContentTransitionScope<NavBackStackEntry>.popIn(): EnterTransition {
    return scaleIn(
        tween(POP_DURATION, easing = FastOutLinearInEasing), initialScale = 0.72f
    ) + fadeIn(tween(POP_DURATION, easing = LinearOutFastInEasing))
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.popOut(): ExitTransition {
    return scaleOut(
        tween(POP_DURATION, easing = FastEasing), targetScale = 0.72f
    ) + fadeOut(tween(POP_DURATION, easing = FastEasing))
}


fun AnimatedContentTransitionScope<NavBackStackEntry>.navigatingTowardsAny(vararg screens: Screen): Boolean {
    val target = getTargetRoute()
    screens.forEach {
        if (target?.startsWith(it.route) == true) return true
    }
    return false
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.navigatingFromAny(vararg screens: Screen): Boolean {
    val initial = getInitialRoute()
    screens.forEach {
        if (initial?.startsWith(it.route) == true) return true
    }
    return false
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.navigatingTowards(screen: Screen): Boolean {
    return getTargetRoute()?.startsWith(screen.route) ?: false
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.getTargetRoute(): String? {
    return this.targetState.destination.route
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.navigatingFrom(screen: Screen): Boolean {
    return getInitialRoute()?.startsWith(screen.route) ?: false
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.getInitialRoute(): String? {
    return this.initialState.destination.route
}


private fun <T> slideSpec() = tween<T>(SLIDE_DURATION, easing = FastOutSlowInEasing)

private val LinearOutFastInEasing: Easing = CubicBezierEasing(0f, 0f, 1f, 0.6f)
private val FastEasing: Easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)

private const val POP_DURATION = 300
private const val SLIDE_DURATION = 500