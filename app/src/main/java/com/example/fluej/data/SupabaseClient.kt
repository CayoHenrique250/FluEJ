package com.example.fluej.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

val supabase = createSupabaseClient(
    supabaseUrl = "https://jufmpckqtxkjptaqnkpy.supabase.co",
    supabaseKey = "sb_publishable_6pk-SGIvp5SM5B2ytpR7gA_ElomdJW9"
) {
    install(Postgrest)
}