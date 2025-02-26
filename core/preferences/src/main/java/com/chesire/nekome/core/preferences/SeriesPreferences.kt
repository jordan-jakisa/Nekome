package com.chesire.nekome.core.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.chesire.nekome.core.flags.UserSeriesStatus
import com.chesire.nekome.core.preferences.flags.SortOption
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "series")

/**
 * Provides a wrapper around the [SharedPreferences] to aid with getting and setting values into it.
 */
class SeriesPreferences @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    @ApplicationContext private val context: Context
) {

    private val _rateOnCompletionKey = context.getString(R.string.key_rate_on_completion)
    private val rateOnCompletionKey = booleanPreferencesKey(_rateOnCompletionKey)
    private val sortPreferenceKey = intPreferencesKey("preference.sort")
    private val filterPreferenceKey = stringPreferencesKey("preference.filter")
    private val filterAdapter by lazy {
        Moshi.Builder()
            .build()
            .adapter<Map<Int, Boolean>>(
                Types.newParameterizedType(
                    Map::class.java,
                    Int::class.javaObjectType,
                    Boolean::class.javaObjectType
                )
            )
    }
    private val defaultFilter by lazy {
        filterAdapter.toJson(
            UserSeriesStatus
                .values()
                .filterNot { it == UserSeriesStatus.Unknown }
                .associate {
                    it.index to (it.index == 0)
                }
        )
    }

    /**
     * Returns a [Flow] of the filter value.
     */
    val filter: Flow<Map<Int, Boolean>> = context.dataStore.data.map { preferences ->
        filterAdapter.fromJson(preferences[filterPreferenceKey] ?: defaultFilter) ?: emptyMap()
    }

    /**
     * Returns a [Flow] of the sort value.
     */
    val sort: Flow<SortOption> = context.dataStore.data.map { preferences ->
        SortOption.forIndex(preferences[sortPreferenceKey] ?: SortOption.Default.index)
    }

    /* TODO: Update this from the settings screen and use this instead.
    context.dataStore.data.map { preferences ->
        preferences[rateOnCompletionKey] ?: false
    }
    */
    /**
     * Returns a flow for if a rating dialog should be shown when the series on completing a series.
     */
    val rateSeriesOnCompletion: Flow<Boolean> = flowOf(rateSeriesOnCompletionPreference)

    /**
     * Update the [sort] value with a new [SortOption].
     */
    suspend fun updateSort(value: SortOption) {
        context.dataStore.edit { preferences ->
            preferences[sortPreferenceKey] = value.index
        }
    }

    /**
     * Update the [filter] value with new data.
     */
    suspend fun updateFilter(value: Map<Int, Boolean>) {
        context.dataStore.edit { preferences ->
            preferences[filterPreferenceKey] = filterAdapter.toJson(value)
        }
    }

    /**
     * Preference value for if a rating dialog should be displayed on completing a series.
     */
    @Deprecated("Delete when settings can update the flag")
    var rateSeriesOnCompletionPreference: Boolean
        get() = sharedPreferences.getBoolean(_rateOnCompletionKey, false)
        set(value) = sharedPreferences.edit {
            putBoolean(_rateOnCompletionKey, value)
        }
}
