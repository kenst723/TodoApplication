package app.sato.ken.todoapp_with_fragment.Fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import app.sato.ken.todoapp_with_fragment.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}