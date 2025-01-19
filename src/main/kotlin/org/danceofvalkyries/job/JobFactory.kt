package org.danceofvalkyries.job

import org.danceofvalkyries.environment.Environment
import org.danceofvalkyries.job.telegram_listener.ListenToTelegramEvensJob
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

        val jobs = listOf(
            NotifierJob(dispatchers, environment),
            ListenToTelegramEvensJob(dispatchers, environment),
            SandBoxJob(dispatchers, environment),
            UpdateCacheJob(dispatchers, environment),
        ).associateBy { it.type }

        return jobs[jobKindArgument] ?: error("Unknown Job kind argument: $jobKindArgument. Known kinds: ${jobs.keys}")
    }
}