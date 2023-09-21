package com.joseg.fakeyouclient.data.testdouble.model

import com.joseg.fakeyouclient.common.enums.LanguageTag
import com.joseg.fakeyouclient.model.UserRatings
import com.joseg.fakeyouclient.model.VoiceModel

object VoiceModelDummies {
    val dummyModelToken = "TM:7wbtjphx8h8v"
    val dummyIneferenceText = "This is a use of the voice"

    val dummy1 = VoiceModel(
        modelToken  = "TM:bysebgf36tkg",
        ttsModelType = "tacotron2",
        creatorUserToken = "U:7S161Q96MG530",
        creatorUsername = 	"zombie",
        creatorDisplayName = "zombie",
        creatorGravatarHash = "c9f26e22a4d10bb1b75e4d8a84c85660",
        title = "Arthur C. Clarke (901ep)",
        ietfLanguageTag = "en-US",
        ietfPrimaryLanguageSubtag = LanguageTag.parse("en"),
        isFrontPageFeatured = false,
        isTwitchFeatured = false,
        maybeSuggestedUniqueBotCommand = null,
        creatorSetVisibility = "public",
        userRatings = UserRatings(
            positiveCount = 157,
            negativeCount = 112,
            totalCount = 269
        ),
        categoryTokens = listOf(
            "CAT:46m8yaq2ceg",
            "CAT:gty64wem67f",
            "CAT:jhskand3g24"
        ),
        createdAt = "2022-04-15T08:34:03Z",
        updatedAt =  "2023-07-30T13:19:47Z"
    )

    val dummy2 = VoiceModel(
        modelToken  = "TM:bysebgf36tkg",
        ttsModelType = "tacotron2",
        creatorUserToken = "U:F6S52625WQ8C2",
        creatorUsername = 	"jacoblenstar",
        creatorDisplayName = "Jacoblenstar",
        creatorGravatarHash = "c9f26e22a4d10bb1b75e4d8a84c85660",
        title = "Abraham Simpson (Dan Castellaneta)",
        ietfLanguageTag = "en-US",
        ietfPrimaryLanguageSubtag = LanguageTag.parse("en"),
        isFrontPageFeatured = false,
        isTwitchFeatured = false,
        maybeSuggestedUniqueBotCommand = null,
        creatorSetVisibility = "public",
        userRatings = UserRatings(
            positiveCount = 31,
            negativeCount = 41,
            totalCount = 72
        ),
        categoryTokens = listOf("CAT:377189hqsbz"),
        createdAt = "2021-04-11T08:34:03Z",
        updatedAt =  "2021-05-37T13:19:47Z"
    )

    val dummyList = listOf(
        VoiceModel(
            modelToken  = "TM:bysebgf36tkg",
            ttsModelType = "tacotron2",
            creatorUserToken = "U:7S161Q96MG530",
            creatorUsername = 	"zombie",
            creatorDisplayName = "zombie",
            creatorGravatarHash = "c9f26e22a4d10bb1b75e4d8a84c85660",
            title = "Arthur C. Clarke (901ep)",
            ietfLanguageTag = "en-US",
            ietfPrimaryLanguageSubtag = LanguageTag.parse("en"),
            isFrontPageFeatured = false,
            isTwitchFeatured = false,
            maybeSuggestedUniqueBotCommand = null,
            creatorSetVisibility = "public",
            userRatings = UserRatings(
                positiveCount = 157,
                negativeCount = 112,
                totalCount = 269
            ),
            categoryTokens = listOf(
                "CAT:46m8yaq2ceg",
                "CAT:gty64wem67f",
                "CAT:jhskand3g24"
            ),
            createdAt = "2022-04-15T08:34:03Z",
            updatedAt =  "2023-07-30T13:19:47Z"
        ),
        VoiceModel(
            modelToken  = "TM:bysebgf36tkg",
            ttsModelType = "tacotron2",
            creatorUserToken = "U:F6S52625WQ8C2",
            creatorUsername = 	"jacoblenstar",
            creatorDisplayName = "Jacoblenstar",
            creatorGravatarHash = "c9f26e22a4d10bb1b75e4d8a84c85660",
            title = "Abraham Simpson (Dan Castellaneta)",
            ietfLanguageTag = "en-US",
            ietfPrimaryLanguageSubtag = LanguageTag.parse("en"),
            isFrontPageFeatured = false,
            isTwitchFeatured = false,
            maybeSuggestedUniqueBotCommand = null,
            creatorSetVisibility = "public",
            userRatings = UserRatings(
                positiveCount = 31,
                negativeCount = 41,
                totalCount = 72
            ),
            categoryTokens = listOf("CAT:377189hqsbz"),
            createdAt = "2021-04-11T08:34:03Z",
            updatedAt =  "2021-05-37T13:19:47Z"
        ),
        VoiceModel(
            modelToken  = "TM:48sva4f7gjjk",
            ttsModelType = "tacotron2",
            creatorUserToken = "U:PQENDNCCJV19N",
            creatorUsername = 	"vox_populi",
            creatorDisplayName = "Vox_populi",
            creatorGravatarHash = "d1a4fe8523ecb541f7d3e4af98574f90",
            title = "Amy Rose (España)",
            ietfLanguageTag = "es-ES",
            ietfPrimaryLanguageSubtag = LanguageTag.parse("es"),
            isFrontPageFeatured = false,
            isTwitchFeatured = false,
            maybeSuggestedUniqueBotCommand = "amyesp",
            creatorSetVisibility = "public",
            userRatings = UserRatings(
                positiveCount = 45,
                negativeCount = 17,
                totalCount = 62
            ),
            categoryTokens = listOf("CAT:jtdwpy6ptrn"),
            createdAt = "2022-04-08T00:26:49Z",
            updatedAt =  "2023-09-14T14:30:53Z"
        ),
        VoiceModel(
            modelToken  = "TM:r2tesbs7by7a",
            ttsModelType = "tacotron2",
            creatorUserToken = "U:JYTXMQBDP5Z29",
            creatorUsername = 	"carldakwason",
            creatorDisplayName = "Carldakwason",
            creatorGravatarHash = "f4029d48568240e4a6ce28a06e7864c2",
            title = "Cranky Kong (Uncle Al)",
            ietfLanguageTag = "en",
            ietfPrimaryLanguageSubtag = LanguageTag.parse("en"),
            isFrontPageFeatured = false,
            isTwitchFeatured = false,
            maybeSuggestedUniqueBotCommand = null,
            creatorSetVisibility = "public",
            userRatings = UserRatings(
                positiveCount = 4,
                negativeCount = 3,
                totalCount = 7
            ),
            categoryTokens = listOf("CAT:j4ty8xzdkmq"),
            createdAt = "2022-04-08T00:26:49Z",
            updatedAt =  "2023-09-14T14:30:53Z"
        ),
        VoiceModel(
            modelToken  = "TM:vw9dgqatgsrx",
            ttsModelType = "tacotron2",
            creatorUserToken = "U:JYTXMQBDP5Z29",
            creatorUsername = 	"carldakwason",
            creatorDisplayName = "Carldakwason",
            creatorGravatarHash = "f4029d48568240e4a6ce28a06e7864c2",
            title = "Candy Kong (Donkey Kong Country, Joy Tanner)",
            ietfLanguageTag = "en",
            ietfPrimaryLanguageSubtag = LanguageTag.parse("en"),
            isFrontPageFeatured = false,
            isTwitchFeatured = false,
            maybeSuggestedUniqueBotCommand = null,
            creatorSetVisibility = "public",
            userRatings = UserRatings(
                positiveCount = 0,
                negativeCount = 1,
                totalCount = 1
            ),
            categoryTokens = listOf("CAT:j4ty8xzdkmq"),
            createdAt = "2022-04-08T00:26:49Z",
            updatedAt =  "2023-09-14T14:30:53Z"
        )
    )
}