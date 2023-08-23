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
    val updatedAt: String,
    val subCategories: List<Category>?
)

data class CategoryCompact(
    val categoryToken: String,
    val modelType: String,
    val maybeSuperCategoryToken: String?,
    val nameForDropdown: String,
    val subCategories: List<CategoryCompact>?
)