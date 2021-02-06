package app.sato.ken.todoapp_with_fragment.Fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import app.sato.ken.todoapp_with_fragment.R

class Tab05Fragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.root_preferences)
    }
}