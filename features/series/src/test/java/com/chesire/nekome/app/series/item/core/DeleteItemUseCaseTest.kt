@file:OptIn(ExperimentalCoroutinesApi::class)

package com.chesire.nekome.app.series.item.core

import com.chesire.nekome.core.Resource
import com.chesire.nekome.datasource.series.SeriesRepository
import com.chesire.nekome.testing.createSeriesDomain
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DeleteItemUseCaseTest {

    private val seriesRepo = mockk<SeriesRepository>()
    private lateinit var deleteItem: DeleteItemUseCase

    @Before
    fun setup() {
        clearAllMocks()

        deleteItem = DeleteItemUseCase(seriesRepo)
    }

    @Test
    fun `When invoking, On success, Ok is returned`() = runTest {
        val expectedDomain = createSeriesDomain()
        coEvery { seriesRepo.getSeries(any()) } returns expectedDomain
        coEvery { seriesRepo.deleteSeries(expectedDomain) } returns Resource.Success(Unit)

        val result = deleteItem(123)

        assertTrue(result is Ok)
    }

    @Test
    fun `When invoking, On failure, Err is returned`() = runTest {
        val expectedDomain = createSeriesDomain()
        coEvery { seriesRepo.getSeries(any()) } returns expectedDomain
        coEvery { seriesRepo.deleteSeries(expectedDomain) } returns Resource.Error("")

        val result = deleteItem(123)

        assertTrue(result is Err)
    }
}
