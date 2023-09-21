package com.joseg.fakeyouclient.data.testdouble.model

import com.joseg.fakeyouclient.model.Category

object CategoryDummies {
    val dummy1 = Category(
        categoryToken = "CAT:akt2tam28ja",
        modelType = "tts",
        maybeSuperCategoryToken = null,
        canDirectlyHaveModels = false,
        canHaveSubcategories = true,
        canOnlyModsApply = true,
        name = "Video Games",
        nameForDropdown = "Games",
        isModApproved = true,
        isSynthetic = false,
        shouldBeSorted = true,
        createdAt = "2022-07-14T15:41:05Z",
        updatedAt = "2022-07-14T15:42:10Z",
        subCategories = null
    )

    val dummy2 = Category(
        categoryToken = "CAT:n4r2taXK8pp",
        modelType = "tts",
        maybeSuperCategoryToken = null,
        canDirectlyHaveModels = false,
        canHaveSubcategories = true,
        canOnlyModsApply = true,
        name = "Comics",
        nameForDropdown = "Comics",
        isModApproved = true,
        isSynthetic = false,
        shouldBeSorted = true,
        createdAt = "2021-03-14T15:41:05Z",
        updatedAt = "2021-03-04T19:42:10Z",
        subCategories = null
    )

    val dummyWithSubCategory1 = Category(
        categoryToken = "CAT:akt2tam28ja",
        modelType = "tts",
        maybeSuperCategoryToken = null,
        canDirectlyHaveModels = false,
        canHaveSubcategories = true,
        canOnlyModsApply = true,
        name = "Video Games",
        nameForDropdown = "Games",
        isModApproved = true,
        isSynthetic = false,
        shouldBeSorted = true,
        createdAt = "2022-07-14T15:41:05Z",
        updatedAt = "2022-07-14T15:42:10Z",
        subCategories = listOf(
            Category(
                categoryToken = "CAT:xm6jbgyxk9s",
                modelType = "tts",
                maybeSuperCategoryToken = "CAT:akt2tam28ja",
                canDirectlyHaveModels = true,
                canHaveSubcategories = false,
                canOnlyModsApply = false,
                name = " Bayonetta",
                nameForDropdown = " Bayonetta",
                isModApproved = true,
                isSynthetic = false,
                shouldBeSorted = true,
                createdAt = "2022-07-14T15:41:05Z",
                updatedAt = "2022-07-14T15:42:10Z",
                subCategories = null
            ),
            Category(
                categoryToken = "CAT:j4ty8xzdkmq",
                modelType = "tts",
                maybeSuperCategoryToken = "CAT:akt2tam28ja",
                canDirectlyHaveModels = true,
                canHaveSubcategories = false,
                canOnlyModsApply = false,
                name = "Donkey Kong series",
                nameForDropdown = "Donkey Kong series",
                isModApproved = true,
                isSynthetic = false,
                shouldBeSorted = true,
                createdAt = "2022-07-14T15:41:05Z",
                updatedAt = "2022-07-14T15:42:10Z",
                subCategories = null
            )
        )
    )

    val dummyList = listOf(
        Category(
            categoryToken = "CAT:akt2tam28ja",
            modelType = "tts",
            maybeSuperCategoryToken = null,
            canDirectlyHaveModels = false,
            canHaveSubcategories = true,
            canOnlyModsApply = true,
            name = "Video Games",
            nameForDropdown = "Games",
            isModApproved = true,
            isSynthetic = false,
            shouldBeSorted = true,
            createdAt = "2022-07-14T15:41:05Z",
            updatedAt = "2022-07-14T15:42:10Z",
            subCategories = null
        ),
        Category(
            categoryToken = "CAT:n4r2taXK8pp",
            modelType = "tts",
            maybeSuperCategoryToken = null,
            canDirectlyHaveModels = false,
            canHaveSubcategories = true,
            canOnlyModsApply = true,
            name = "Comics",
            nameForDropdown = "Comics",
            isModApproved = true,
            isSynthetic = false,
            shouldBeSorted = true,
            createdAt = "2021-03-14T15:41:05Z",
            updatedAt = "2021-03-04T19:42:10Z",
            subCategories = null
        ),
        Category(
            categoryToken = "CAT:xm6jbgyxk9s",
            modelType = "tts",
            maybeSuperCategoryToken = "CAT:akt2tam28ja",
            canDirectlyHaveModels = true,
            canHaveSubcategories = false,
            canOnlyModsApply = false,
            name = " Bayonetta",
            nameForDropdown = " Bayonetta",
            isModApproved = true,
            isSynthetic = false,
            shouldBeSorted = true,
            createdAt = "2022-07-14T15:41:05Z",
            updatedAt = "2022-07-14T15:42:10Z",
            subCategories = null
        ),
        Category(
            categoryToken = "CAT:j4ty8xzdkmq",
            modelType = "tts",
            maybeSuperCategoryToken = "CAT:akt2tam28ja",
            canDirectlyHaveModels = true,
            canHaveSubcategories = false,
            canOnlyModsApply = false,
            name = "Donkey Kong series",
            nameForDropdown = "Donkey Kong series",
            isModApproved = true,
            isSynthetic = false,
            shouldBeSorted = true,
            createdAt = "2022-07-14T15:41:05Z",
            updatedAt = "2022-07-14T15:42:10Z",
            subCategories = null
        )
    )
}