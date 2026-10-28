package com.example.fluej.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json

val supabase = createSupabaseClient(
    supabaseUrl = "https://jufmpckqtxkjptaqnkpy.supabase.co",
    supabaseKey = "sb_publishable_6pk-SGIvp5SM5B2ytpR7gA_ElomdJW9"
) {
    defaultSerializer = KotlinXSerializer(Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    })
    install(Auth)
    install(Postgrest)
}