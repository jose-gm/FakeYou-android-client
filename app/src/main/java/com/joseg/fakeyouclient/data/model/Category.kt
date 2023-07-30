package com.joseg.fakeyouclient.data.model

import com.joseg.fakeyouclient.model.Category
import com.joseg.fakeyouclient.model.ChildCategoryCompact
import com.joseg.fakeyouclient.model.ParentCategoryCompat
import com.joseg.fakeyouclient.network.model.NetworkCategories
import com.joseg.fakeyouclient.network.model.NetworkCategory

fun NetworkCategory.asCategory() = Category(
    categoryToken = category_token,
    modelType = model_type,
    maybeSuperCategoryToken = maybe_super_category_token,
    canDirectlyHaveModels = can_directly_have_models,
    canHaveSubcategories = can_have_subcategories,
    canOnlyModsApply = can_only_mods_apply,
    name = name,
    nameForDropdown = name_for_dropdown,
    isModApproved = is_mod_approved,
    createdAt = created_at,
    updatedAt = updated_at
)

fun NetworkCategories.asCategoriesParentCompact(): List<ParentCategoryCompat> =
    categories
        .filter { it.can_have_subcategories }
        .map { parentCategory ->
            ParentCategoryCompat(
                categoryToken = parentCategory.category_token,
                modelType = parentCategory.model_type,
                nameForDropdown = parentCategory.name_for_dropdown,
                childrenCategories = categories
                    .filter { it.can_directly_have_models && it.maybe_super_category_token == parentCategory.category_token }
                    .map {
                        ChildCategoryCompact(
                            categoryToken = parentCategory.category_token,
                            modelType = parentCategory.model_type,
                            maybeSuperCategoryToken = parentCategory.maybe_super_category_token,
                            nameForDropdown = parentCategory.name_for_dropdown,
                        )
                    }
            )
        }