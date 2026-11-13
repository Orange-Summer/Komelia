package snd.komelia.ui.reader.image.paged

import androidx.compose.ui.geometry.Offset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PageSwipeGestureTest {
    private fun gesture() = PageSwipeGesture(touchSlop = 8f, minimumDistance = 48f)

    @Test
    fun horizontalSwipesReportTheirDirection() {
        val left = gesture().apply { moveTo(Offset(-60f, 5f)) }
        val right = gesture().apply { moveTo(Offset(60f, -5f)) }
        assertEquals(true, left.swipeLeft())
        assertEquals(false, right.swipeLeft())
    }

    @Test
    fun tapsAndShortDragsDoNotTurnPages() {
        val swipe = gesture()
        swipe.moveTo(Offset(3f, 2f))
        assertFalse(swipe.isHorizontal)
        assertNull(swipe.swipeLeft())
        swipe.moveTo(Offset(30f, 2f))
        assertTrue(swipe.isHorizontal)
        assertNull(swipe.swipeLeft())
    }

    @Test
    fun verticalPanCannotBecomeAPageSwipe() {
        val swipe = gesture()
        swipe.moveTo(Offset(2f, 20f))
        swipe.moveTo(Offset(80f, 30f))
        assertFalse(swipe.isHorizontal)
        assertNull(swipe.swipeLeft())
    }

    @Test
    fun returningToStartCancelsNavigation() {
        val swipe = gesture()
        swipe.moveTo(Offset(-90f, 0f))
        swipe.moveTo(Offset(-10f, 0f))
        assertNull(swipe.swipeLeft())
    }

    @Test
    fun diagonalGestureDoesNotTurnPages() {
        val swipe = gesture()
        swipe.moveTo(Offset(-20f, 0f))
        swipe.moveTo(Offset(-60f, 80f))
        assertNull(swipe.swipeLeft())
    }

    @Test
    fun distanceIsMeasuredFromStartAcrossMultipleEvents() {
        val swipe = gesture()
        for (distance in 1..48) swipe.moveTo(Offset(-distance.toFloat(), 0f))
        assertEquals(true, swipe.swipeLeft())
    }
}
