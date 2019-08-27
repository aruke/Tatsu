package org.rionlabs.tatsu.data

import org.junit.Assert.assertEquals
import org.junit.Test
import org.rionlabs.tatsu.data.model.TimerState
import org.rionlabs.tatsu.data.model.TimerType

class ConvertersTest {

    @Test
    fun `test TimerState to String`() {
        assertEquals("IDLE", Converters.toString(TimerState.IDLE))
        assertEquals("RUNNING", Converters.toString(TimerState.RUNNING))
        assertEquals("PAUSED", Converters.toString(TimerState.PAUSED))
        assertEquals("FINISHED", Converters.toString(TimerState.FINISHED))
        assertEquals("CANCELLED", Converters.toString(TimerState.CANCELLED))
    }

    @Test
    fun `test string to TimerState`() {
        assertEquals(TimerState.IDLE, Converters.toTimerState("IDLE"))
        assertEquals(TimerState.RUNNING, Converters.toTimerState("RUNNING"))
        assertEquals(TimerState.PAUSED, Converters.toTimerState("PAUSED"))
        assertEquals(TimerState.FINISHED, Converters.toTimerState("FINISHED"))
        assertEquals(TimerState.CANCELLED, Converters.toTimerState("CANCELLED"))
    }

    @Test
    fun `test TimerType to String`() {
        assertEquals("WORK", Converters.toString(TimerType.WORK))
        assertEquals("BREAK", Converters.toString(TimerType.BREAK))
    }

    @Test
    fun `test String to TimerType`() {
        assertEquals(TimerType.WORK, Converters.toTimerType("WORK"))
        assertEquals(TimerType.BREAK, Converters.toTimerType("BREAK"))
    }
}