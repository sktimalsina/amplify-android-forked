package com.amplifyframework.logging.cloudwatch

import android.util.Log
import com.amplifyframework.logging.LogLevel
import com.amplifyframework.logging.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
class CloudWatchLogger(
    private val namespace: String,
    private val logLevel: LogLevel,
    private val cloudwatchLogEventRecorder: CloudwatchLogEventRecorder,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : Logger {

    private val coroutineScope = CoroutineScope(dispatcher)

    override fun getThresholdLevel(): LogLevel {
        return logLevel
    }

    override fun getNamespace(): String {
        return namespace
    }

    override fun error(message: String?) {
        if (logLevel.above(LogLevel.ERROR)) {
            return
        }
        val event = CloudWatchLogEvent(System.currentTimeMillis(), "error/$namespace: $message")
        //Log.e(namespace, message.toString())
        coroutineScope.launch {
            cloudwatchLogEventRecorder.saveLogEvent(event)
        }
    }

    override fun error(message: String?, error: Throwable?) {
        if (logLevel.above(LogLevel.ERROR)) {
            return
        }
        val event = CloudWatchLogEvent(System.currentTimeMillis(), "error/$namespace: $message")
        //Log.e(namespace, message.toString())
        coroutineScope.launch {
            cloudwatchLogEventRecorder.saveLogEvent(event)
        }
    }

    override fun warn(message: String?) {
        if (logLevel.above(LogLevel.WARN)) {
            return
        }
        val event = CloudWatchLogEvent(System.currentTimeMillis(), "warn/$namespace: $message")
        //Log.w(namespace, message.toString())
        coroutineScope.launch {
            cloudwatchLogEventRecorder.saveLogEvent(event)
        }
    }

    override fun warn(message: String?, issue: Throwable?) {
        if (logLevel.above(LogLevel.WARN)) {
            return
        }
        val event = CloudWatchLogEvent(System.currentTimeMillis(), "warn/$namespace: $message")
        //Log.w(namespace, message.toString())
        coroutineScope.launch {
            cloudwatchLogEventRecorder.saveLogEvent(event)
        }
    }

    override fun info(message: String?) {
        if (logLevel.above(LogLevel.INFO)) {
            return
        }
        val event = CloudWatchLogEvent(System.currentTimeMillis(), "info/$namespace: $message")
        //Log.i(namespace, message.toString())
        coroutineScope.launch {
            cloudwatchLogEventRecorder.saveLogEvent(event)
        }
    }

    override fun debug(message: String?) {
        if (logLevel.above(LogLevel.DEBUG)) {
            return
        }
        val event = CloudWatchLogEvent(System.currentTimeMillis(), "debug/$namespace: $message")
        ////Log.d(namespace, message.toString())
        coroutineScope.launch {
            cloudwatchLogEventRecorder.saveLogEvent(event)
        }
    }

    override fun verbose(message: String?) {
        if (logLevel.above(LogLevel.VERBOSE)) {
            return
        }
        val event = CloudWatchLogEvent(System.currentTimeMillis(), "verbose/$namespace: $message")
        //Log.v(namespace, message.toString())
        coroutineScope.launch {
            cloudwatchLogEventRecorder.saveLogEvent(event)
        }
    }

}