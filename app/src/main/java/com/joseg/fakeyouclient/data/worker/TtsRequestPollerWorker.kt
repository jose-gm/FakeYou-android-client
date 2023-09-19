package com.joseg.fakeyouclient.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.joseg.fakeyouclient.R
import com.joseg.fakeyouclient.common.enums.TtsRequestStatusType
import com.joseg.fakeyouclient.common.notification.Notifications
import com.joseg.fakeyouclient.common.utils.AlphanumericGenerator
import com.joseg.fakeyouclient.data.repository.AudioRepository
import com.joseg.fakeyouclient.data.repository.implementation.BaseAudioRepository
import com.joseg.fakeyouclient.domain.PollTtsRequestStateUseCase
import com.joseg.fakeyouclient.model.Audio
import com.joseg.fakeyouclient.model.TtsRequestState
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.UUID
import java.util.concurrent.TimeUnit

@HiltWorker
class TtsRequestPollerWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val pollTtsRequestStateUseCase: PollTtsRequestStateUseCase,
    private val audioRepository: AudioRepository,
    private val ttsRequestNotificationManager: TtsRequestNotificationManager
) : CoroutineWorker(context, workerParameters) {

    private val workManager = WorkManager.getInstance(context)

    companion object {
        const val TAG = "TtsRequestPollerWorker"
        const val INFERENCE_JOB_TOKEN_KEY = "inference_job_token_key"
        const val VOICE_MODEL_KEY = "voice_model_key"

        fun start(
            context: Context,
            inferenceJobToken: String,
            voiceModelName: String
        ) {
            val workManager = WorkManager.getInstance(context)

            val request = OneTimeWorkRequestBuilder<TtsRequestPollerWorker>()
                .setId(UUID.randomUUID())
                .addTag(TAG)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    10000L,
                    TimeUnit.MILLISECONDS
                )
                .setInputData(workDataOf(
                    INFERENCE_JOB_TOKEN_KEY to inferenceJobToken,
                    VOICE_MODEL_KEY to voiceModelName
                ))
                .build()

            workManager.enqueue(request)
        }
    }

    override suspend fun doWork(): Result {
        val inferenceJobToken = inputData.getString(INFERENCE_JOB_TOKEN_KEY) ?: ""
        val voiceModelName = inputData.getString(VOICE_MODEL_KEY) ?: ""

        try {
            setForeground(getForegroundInfo())
        } catch (e: IllegalStateException) {
            Log.d("ttsRequestState", e.stackTraceToString())
        }

        return withContext(Dispatchers.IO) {
            try {
                pollTtsRequestStateUseCase(inferenceJobToken)
                    .collect { state ->
                        when (state.status) {
                            TtsRequestStatusType.PENDING,
                            TtsRequestStatusType.STARTED,
                            TtsRequestStatusType.ATTEMPT_FAILED -> {
                                ttsRequestNotificationManager.submitNotification(
                                    inferenceJobToken,
                                    state.title,
                                    state.status,
                                    id.toString()
                                )
                                ttsRequestNotificationManager.updateNotifications()
                            }

                            TtsRequestStatusType.COMPLETE_FAILURE,
                            TtsRequestStatusType.DEAD -> {
                                ttsRequestNotificationManager.removeNotification(inferenceJobToken)
                                ttsRequestNotificationManager.submitNotification(
                                    inferenceJobToken,
                                    state.title,
                                    state.status,
                                    id.toString()
                                )
                                ttsRequestNotificationManager.updateNotifications()
                            }

                            TtsRequestStatusType.COMPLETE_SUCCESS -> {
                                audioRepository.insert(createAudio(state))
                                ttsRequestNotificationManager.removeNotification(inferenceJobToken)
                                ttsRequestNotificationManager.submitNotification(
                                    inferenceJobToken,
                                    state.title,
                                    state.status,
                                    id.toString()
                                )
                                ttsRequestNotificationManager.updateNotifications()
                            }
                        }
                    }
            } catch (e: Throwable) {
                when (e) {
                    is UnknownHostException -> {
                        Log.d("ttsRequestState", e.stackTraceToString())

                        ttsRequestNotificationManager.submitRetryNotification(
                            notificationId = inferenceJobToken,
                            title = voiceModelName
                        )
                        ttsRequestNotificationManager.updateNotifications()
                        Result.failure()
                    }
                    is CancellationException -> {

                        ttsRequestNotificationManager.removeNotificationStateFromCache(inferenceJobToken)
                        ttsRequestNotificationManager.updateNotifications()
                        Result.failure()
                    }
                    is SocketTimeoutException -> {
                        Log.d("ttsRequestState", e.stackTraceToString())

                        ttsRequestNotificationManager.submitRetryNotification(
                            notificationId = inferenceJobToken,
                            title = voiceModelName
                        )
                        ttsRequestNotificationManager.updateNotifications()
                        Result.failure()
                    }
                    is HttpException -> {
                        Log.d("ttsRequestState", e.stackTraceToString())

                        ttsRequestNotificationManager.submitRetryNotification(
                            notificationId = inferenceJobToken,
                            title = voiceModelName
                        )
                        ttsRequestNotificationManager.updateNotifications()
                        Result.failure()
                    }
                    else -> {
                        Log.d("ttsRequestState", e.stackTraceToString())

                        ttsRequestNotificationManager.removeNotification(inferenceJobToken)
                        ttsRequestNotificationManager.submitNotification(
                            notificationId = inferenceJobToken,
                            title = voiceModelName,
                            status = TtsRequestStatusType.COMPLETE_FAILURE,
                            workerId = id.toString()
                        )
                        ttsRequestNotificationManager.updateNotifications()
                        Result.failure()
                    }
                }
            }

            Result.success()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val inferenceJobToken = inputData.getString(INFERENCE_JOB_TOKEN_KEY) ?: ""
        val voiceModelName = inputData.getString(VOICE_MODEL_KEY) ?: ""
        val intent = workManager.createCancelPendingIntent(id)

        val notificationBuilder = Notifications.buildNotification(
            context,
            Notifications.TTS_REQUEST_STATUS_CHANNEL
        ) {
            setContentTitle(voiceModelName)
            setContentText(context.getString(R.string.notification_tts_request_status_content_pending))
            setSmallIcon(R.drawable.ic_logo_temp)
            setOngoing(true)
            addAction(0, context.getString(R.string.notification_action_cancel), intent)
        }
        ttsRequestNotificationManager.submitNotification(
            inferenceJobToken,
            voiceModelName,
            TtsRequestStatusType.PENDING,
            id.toString()
        )

        return ForegroundInfo(inferenceJobToken.hashCode(), notificationBuilder.build())
    }

    private fun createAudio(ttsRequestState: TtsRequestState): Audio = Audio(
        id = AlphanumericGenerator.generate(34),
        voiceModelName = ttsRequestState.title,
        inferenceText = ttsRequestState.rawInferenceText,
        url = ttsRequestState.maybePublicBucketWavAudioPath ?: "",
        createdAt = ttsRequestState.createdAt
    )
}