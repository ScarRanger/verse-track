package com.rhinepereira.versetrack.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseConfig {
    const val URL = "https://ntnxomezwllngjrzxjdo.supabase.co"
    const val KEY = "sb_publishable_Y5lmlbxIkLEsQ1DWAVZKeA_wU-yPAbs"

    val client: SupabaseClient by lazy {
        createSupabaseClient(URL, KEY) {
            install(Postgrest)
//            install(GoTrue)
        }
    }
}
