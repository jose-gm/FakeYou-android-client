package com.joseg.fakeyouclient.network.model

data class NetworkCategory(
    val category_token: String,
    val model_type: String,
    val maybe_super_category_token: String?,
    val can_directly_have_models: Boolean,
    val can_have_subcategories: Boolean,
    val can_only_mods_apply: Boolean,
    val name: String,
    val name_for_dropdown: String,
    val is_mod_approved: Boolean?,
    val is_synthetic: Boolean,
    val should_be_sorted: Boolean,
    val created_at: String,
    val updated_at: String
)

data class NetworkCategories(
    val success: Boolean,
    val categories: List<NetworkCategory>
)
