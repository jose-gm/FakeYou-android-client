package com.joseg.fakeyouclient.data.model

import com.joseg.fakeyouclient.model.CategoryCompact
import com.joseg.fakeyouclient.network.model.NetworkCategories
import com.joseg.fakeyouclient.network.model.NetworkCategory

fun NetworkCategories.asCategoriesCompact(): List<CategoryCompact> =
    categories
        .filter { it.can_have_subcategories || it.maybe_super_category_token == null }
        .map { category ->
            CategoryCompact(
                categoryToken = category.category_token,
                modelType = category.model_type,
                maybeSuperCategoryToken = category.maybe_super_category_token,
                nameForDropdown = category.name_for_dropdown,
                subCategories = categories
                    .filter { it.can_directly_have_models && it.maybe_super_category_token == category.category_token }
                    .map {
                        CategoryCompact(
                            categoryToken = it.category_token,
                            modelType = it.model_type,
                            maybeSuperCategoryToken = it.maybe_super_category_token,
                            nameForDropdown = it.name_for_dropdown,
                            subCategories = null
                        )
                    }
            )
        }

fun List<NetworkCategory>.asCategoriesCompact(): List<CategoryCompact> =
    this
        .filter { it.can_have_subcategories || it.maybe_super_category_token == null }
        .map { categoryCompact ->
            CategoryCompact(
                categoryToken = categoryCompact.category_token,
                modelType = categoryCompact.model_type,
                maybeSuperCategoryToken = categoryCompact.maybe_super_category_token,
                nameForDropdown = categoryCompact.name_for_dropdown,
                subCategories = this.filter { it.can_directly_have_models && it.maybe_super_category_token == categoryCompact.category_token }
                    .map {
                        CategoryCompact(
                            categoryToken = it.category_token,
                            modelType = it.model_type,
                            maybeSuperCategoryToken = it.maybe_super_category_token,
                            nameForDropdown = it.name_for_dropdown,
                            subCategories = null
                        )
                    }
            )
        }