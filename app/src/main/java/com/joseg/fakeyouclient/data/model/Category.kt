package com.joseg.fakeyouclient.data.model

import com.joseg.fakeyouclient.model.Category
import com.joseg.fakeyouclient.network.model.NetworkCategory

fun List<NetworkCategory>.asCategories(): List<Category> =
    this
        .filter { it.can_have_subcategories || it.maybe_super_category_token == null }
        .map { category ->
            Category(
                categoryToken = category.category_token,
                modelType = category.model_type,
                maybeSuperCategoryToken = category.maybe_super_category_token,
                canDirectlyHaveModels = category.can_directly_have_models,
                canHaveSubcategories = category.can_have_subcategories,
                canOnlyModsApply = category.can_only_mods_apply,
                name = category.name,
                nameForDropdown = category.name_for_dropdown,
                isModApproved = category.is_mod_approved,
                createdAt = category.created_at,
                updatedAt = category.updated_at,
                subCategories = this.filter { it.can_directly_have_models && it.maybe_super_category_token == category.category_token }
                    .map {
                        Category(
                            categoryToken = it.category_token,
                            modelType = it.model_type,
                            maybeSuperCategoryToken = it.maybe_super_category_token,
                            canDirectlyHaveModels = it.can_directly_have_models,
                            canHaveSubcategories = it.can_have_subcategories,
                            canOnlyModsApply = it.can_only_mods_apply,
                            name = it.name,
                            nameForDropdown = it.name_for_dropdown,
                            isModApproved = category.is_mod_approved,
                            createdAt = category.created_at,
                            updatedAt = category.updated_at,
                            subCategories = null
                        )
                    }
            )
        }