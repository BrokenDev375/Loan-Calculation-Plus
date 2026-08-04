package com.loancaculator.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager

/**
 * Clears the current text-field focus as soon as the user touches the screen.
 *
 * The pointer event is only observed, not consumed, so the control under the
 * finger still receives the same tap. A tapped text field requests focus again;
 * tapping anywhere else leaves focus cleared and dismisses the keyboard.
 */
fun Modifier.dismissKeyboardOnTap(): Modifier = composed {
    val focusManager = LocalFocusManager.current

    pointerInput(focusManager) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                if (event.changes.any { it.pressed && !it.previousPressed }) {
                    focusManager.clearFocus()
                }
            }
        }
    }
}
