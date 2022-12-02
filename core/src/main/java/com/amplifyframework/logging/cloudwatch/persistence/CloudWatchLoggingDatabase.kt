package com.amplifyframework.logging.cloudwatch.persistence

import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import com.amplifyframework.logging.cloudwatch.CloudWatchLogEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
 * Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

internal class CloudWatchLoggingDatabase(
    private val context: Context,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val logEvents = 10
    private val logEventsId = 20
    private val cloudWatchDatabaseHelper = CloudWatchDatabaseHelper(context)
    private val database = cloudWatchDatabaseHelper.writableDatabase
    private val basePath = "cloudwatchlogevents"
    private val contentUri: Uri
    private val uriMatcher: UriMatcher

    init {
        val authority = context.applicationContext.packageName
        contentUri = Uri.parse("content://$authority/$basePath")
        uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        // The Uri of LOG_EVENTS is for all records in the LogEventTable table.
        uriMatcher.addURI(authority, basePath, logEvents)
        // the URI of log_event_id is for a single record
        uriMatcher.addURI(authority, "$basePath/#", logEventsId)
    }

    fun closeDB() {
        cloudWatchDatabaseHelper.close()
    }

    suspend fun saveLogEvent(event: CloudWatchLogEvent): Uri {
        return withContext(coroutineDispatcher) {
            insertEvent(contentUri, event)
        }
    }

    suspend fun queryAllEvents(): List<CloudWatchLogEvent> {
        return withContext(coroutineDispatcher) {
            val cloudWatchLogEvents = mutableListOf<CloudWatchLogEvent>()
            val cursor = query(contentUri, null, null, null, null, null)
            cursor.use {
                if (!it.moveToFirst())
                    return@use
                do {
                    val id = it.getInt(LogEventTable.COLUMNINDEX.ID.index)
                    val timestamp = it.getLong(LogEventTable.COLUMNINDEX.TIMESTAMP.index)
                    val message = it.getString(LogEventTable.COLUMNINDEX.MESSAGE.index)
                    cloudWatchLogEvents.add(CloudWatchLogEvent(timestamp.toLong(), message, id))
                } while (cursor.moveToNext())
            }
            cloudWatchLogEvents
        }
    }

    suspend fun bulkDelete(eventIds: List<Int>) {
        return withContext(coroutineDispatcher) {
            val uri = contentUri
            val whereClause = "${LogEventTable.COLUMN_ID} in (${eventIds.joinToString(",")})"
            database.delete(
                LogEventTable.TABLE_LOG_EVENT,
                whereClause,
                null
            )
        }
    }

    private suspend fun insertEvent(uri: Uri, event: CloudWatchLogEvent): Uri {
        val contentValues = ContentValues()
        contentValues.put(LogEventTable.COLUMN_TIMESTAMP, event.timestamp)
        contentValues.put(LogEventTable.COLUMN_MESSAGE, event.message)
        val id = database.insertOrThrow(LogEventTable.TABLE_LOG_EVENT, null, contentValues)
        return Uri.parse("$basePath/$id")
    }

    private fun query(
        uri: Uri,
        projection: Array<String?>? = null,
        selection: String? = null,
        selectionArgs: Array<String?>? = null,
        sortOrder: String? = null,
        limit: String? = null
    ): Cursor {
        val queryBuilder = SQLiteQueryBuilder()
        queryBuilder.tables = LogEventTable.TABLE_LOG_EVENT
        return queryBuilder.query(
            database,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder,
            limit
        )
    }

}