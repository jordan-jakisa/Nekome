package com.chesire.malime

interface MalModelInteractionListener<in T> {
    fun onImageClicked(model: T)
    fun onPlusOneClicked(model: T, callback: () -> Unit)
    fun onNegativeOneClicked(model: T, callback: () -> Unit)
}