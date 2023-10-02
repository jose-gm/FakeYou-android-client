package com.joseg.fakeyouclient.data.testdouble.model

import com.joseg.fakeyouclient.model.Audio
import java.util.UUID

object AudioDummies {
    val dummy1 = Audio(
        id = UUID.randomUUID().toString(),
        voiceModelName = "Sonic the hedgehog",
        inferenceText = "at super sonic speed",
        url = "thi is an url",
        createdAt = "yesterday",
        sample = intArrayOf(0,0,3,5,5,6,0,0)
    )

    val dummy2 = Audio(
        id = UUID.randomUUID().toString(),
        voiceModelName = "Superman",
        inferenceText = "Kryptonite is my weakness",
        url = "thi is another url",
        createdAt = "yesterday",
        sample = intArrayOf(0,0,7,1,3,6,0)
    )

    val dummy3 = Audio(
        id = UUID.randomUUID().toString(),
        voiceModelName = "Batman",
        inferenceText = "I'm batman",
        url = "thi is another url",
        createdAt = "1/1/2023",
        sample = intArrayOf(0,0,7,1,3,6,0)
    )

    val dummy4 = Audio(
        id = UUID.randomUUID().toString(),
        voiceModelName = "Albert Einstein",
        inferenceText = "E=mc power 2",
        url = "thi is another url",
        createdAt = "1/1/2023",
        sample = intArrayOf(0,0,0,0,2,5,3)
    )
}