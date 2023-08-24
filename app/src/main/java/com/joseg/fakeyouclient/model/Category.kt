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
    val canDirectlyHaveModels: Boolean,
    val canHaveSubcategories: Boolean,
    val nameForDropdown: String,
    val subCategories: List<CategoryCompact>?
)

fun Category.asCategoryCompact(): CategoryCompact = CategoryCompact(
    categoryToken = this.categoryToken,
    modelType = this.modelType,
    maybeSuperCategoryToken = this.maybeSuperCategoryToken,
    canDirectlyHaveModels = this.canDirectlyHaveModels,
    canHaveSubcategories = this.canHaveSubcategories,
    nameForDropdown = this.nameForDropdown,
    subCategories = this.subCategories?.map { it.asCategoryCompact() }
)

fun List<Category>.asCategoriesCompact(): List<CategoryCompact> =
    this
        .map { it.asCategoryCompact() }