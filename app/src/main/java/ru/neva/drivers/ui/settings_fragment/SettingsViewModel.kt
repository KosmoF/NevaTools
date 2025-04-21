package ru.neva.drivers.ui.settings_fragment

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.neva.drivers.fptr.FptrHolder
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val fptrHolder: FptrHolder
): ViewModel() {
    // TODO: Implement the ViewModel
}