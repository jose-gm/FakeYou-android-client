package com.joseg.fakeyouclient.data.testdouble.model

import com.joseg.fakeyouclient.model.Audio
import java.util.UUID

object AudioDummies {
    val dummy1 = Audio(
        id = UUID.randomUUID().toString(),
        voiceModelName = "Sonic the hedgehog",
        inferenceText = "at super sonic speed",
        url = "thi is an url",
        createdAt = "yesterday"
    )

    val dummy2 = Audio(
        id = UUID.randomUUID().toString(),
        voiceModelName = "Superman",
        inferenceText = "Kryptonite is my weakness",
        url = "thi is another url",
        createdAt = "yesterday"
    )
}