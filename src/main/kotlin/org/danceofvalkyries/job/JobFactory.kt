package org.danceofvalkyries.job

import job.telegram_listener.ListenToTelegramEvensJob
import org.danceofvalkyries.environment.Environment
import org.danceofvalkyries.utils.DispatchersImpl

interface JobFactory {
    fun create(): Job
}

fun JobFactory(args: Array<String>): JobFactory {
    return JobFactoryImpl(args)
}

private class JobFactoryImpl(
    private val args: Array<String>
) : JobFactory {

    override fun create(): Job {
        val jobKindArgument = args.getOrNull(0) ?: error("Job kind argument must be set!")
        val environmentArgument = args.getOrNull(1) ?: error("Environment kind argument must be set!")

        val environment = Environment(environmentArgument)
        val dispatchers = DispatchersImpl()

        return when (jobKindArgument) {
            "notifier" -> NotifierJob(dispatchers, environment)
            "button_listener" -> ListenToTelegramEvensJob(dispatchers, environment)
            "sand_box" -> SandBoxJob(dispatchers, environment)
            "update_cache" -> UpdateCacheJob(dispatchers, environment)
            else -> error("Unknown Job kind argument: $jobKindArgument")
        }
    }
}