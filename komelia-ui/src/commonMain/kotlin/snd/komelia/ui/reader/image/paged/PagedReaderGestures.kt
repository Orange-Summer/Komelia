package snd.komelia.ui.reader.image.paged

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/** Observe before the image's pan detector, so a page swipe cannot also pan or click. */
internal suspend fun PointerInputScope.detectPageSwipes(
    canSwipe: () -> Boolean,
    onSwipe: (left: Boolean) -> Unit,
) {
    val minimumDistance = 48.dp.toPx().coerceAtLeast(viewConfiguration.touchSlop)
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
        if (down.type != PointerType.Touch || down.isConsumed || !canSwipe()) return@awaitEachGesture

        val swipe = PageSwipeGesture(viewConfiguration.touchSlop, minimumDistance)
        do {
            val event = awaitPointerEvent(PointerEventPass.Initial)
            // Once a second finger joins, the entire gesture belongs to pinch zoom.
            if (event.changes.size != 1) return@awaitEachGesture
            val change = event.changes.single()
            if (change.id != down.id || change.isConsumed) return@awaitEachGesture

            swipe.moveTo(change.position - down.position)
            if (swipe.isHorizontal) change.consume()
            if (!change.pressed) {
                swipe.swipeLeft()?.let(onSwipe)
            }
        } while (event.changes.any { it.pressed })
    }
}

/** Lock the axis after touch slop; navigation is decided only when the finger is lifted. */
internal class PageSwipeGesture(
    private val touchSlop: Float,
    private val minimumDistance: Float,
) {
    private var displacement = Offset.Zero
    private var axisLocked = false
    var isHorizontal = false
        private set

    fun moveTo(displacement: Offset) {
        this.displacement = displacement
        if (!axisLocked && displacement.getDistance() > touchSlop) {
            axisLocked = true
            isHorizontal = abs(displacement.x) > abs(displacement.y)
        }
    }

    fun swipeLeft(): Boolean? =
        if (isHorizontal && abs(displacement.x) >= minimumDistance &&
            abs(displacement.x) > abs(displacement.y)
        ) displacement.x < 0f else null
}
