package org.danceofvalkyries.notion.api

import notion.api.rest.response.NotionDbResponse
import org.danceofvalkyries.notion.api.rest.response.NotionPageResponse
import org.danceofvalkyries.utils.printMeasure

class NotionDataBaseApiTimeMeasurePerfomanceDecorator(
    private val notionDataBaseApi: NotionDataBaseApi
) : NotionDataBaseApi {

    override suspend fun getDescription(): NotionDbResponse {
        return printMeasure(
            message = "getDescription"
        ) { notionDataBaseApi.getDescription() }
    }

    override suspend fun getContent(): List<NotionPageResponse> {
        return printMeasure(
            message = "getContent",
        ) { notionDataBaseApi.getContent() }
    }
}