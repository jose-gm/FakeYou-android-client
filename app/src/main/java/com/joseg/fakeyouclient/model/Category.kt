package com.joseg.fakeyouclient.model

data class Category(
    val categoryToken: String,
    val modelType: String,
    val maybeSuperCategoryToken: String?,
    val canDirectlyHaveModels: Boolean,
    val canHaveSubcategories: Boolean,
    val canOnlyModsApply: Boolean,
    val name: String,
    val nameForDropdown: String,
    val isModApproved: Boolean?,
    val createdAt: String,
    val updatedAt: String
)

data class ParentCategoryCompat(
    val categoryToken: String,
    val modelType: String,
    val nameForDropdown: String,
    val childrenCategories: List<ChildCategoryCompact>
)

data class ChildCategoryCompact(
    val categoryToken: String,
    val modelType: String,
    val maybeSuperCategoryToken: String?,
    val nameForDropdown: String
)