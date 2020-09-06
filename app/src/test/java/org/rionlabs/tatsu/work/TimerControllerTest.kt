package org.rionlabs.tatsu.work

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.rionlabs.tatsu.data.dao.TimerDao

class TimerControllerTest {

    @Mock
    lateinit var timerDao: TimerDao

    val coroutineScope = CoroutineScope(Dispatchers.Unconfined)

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun `verify that timer controller emits NONE timer when no timer is found in database`() {
        val timerController = TimerController(coroutineScope, timerDao)
    }

}
