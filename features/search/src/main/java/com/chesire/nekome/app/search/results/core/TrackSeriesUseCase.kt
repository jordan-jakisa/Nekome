package com.chesire.nekome.app.search.results.core

import com.chesire.nekome.core.Resource
import com.chesire.nekome.core.flags.SeriesType
import com.chesire.nekome.core.preferences.ApplicationPreferences
import com.chesire.nekome.datasource.series.SeriesRepository
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrackSeriesUseCase @Inject constructor(
    private val seriesRepo: SeriesRepository,
    private val settings: ApplicationPreferences
) {

    suspend operator fun invoke(
        seriesId: Int,
        seriesType: SeriesType
    ): Result<Unit, Unit> {
        val response = withContext(Dispatchers.IO) {
            when (seriesType) {
                SeriesType.Anime -> seriesRepo.addAnime(seriesId, settings.defaultSeriesState)
                SeriesType.Manga -> seriesRepo.addManga(seriesId, settings.defaultSeriesState)
                else -> error("Unknown SeriesType: $seriesType")
            }
        }

        return if (response is Resource.Success) {
            Ok(Unit)
        } else {
            Err(Unit)
        }
    }
}
