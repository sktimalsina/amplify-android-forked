package com.amplifyframework.storage.s3.operation

import com.amplifyframework.auth.AuthCredentialsProvider
import com.amplifyframework.core.Consumer
import com.amplifyframework.storage.StorageException
import com.amplifyframework.storage.StorageItem
import com.amplifyframework.storage.operation.StorageListAsyncOperation
import com.amplifyframework.storage.result.StorageListResult
import com.amplifyframework.storage.s3.configuration.AWSS3StoragePluginConfiguration
import com.amplifyframework.storage.s3.request.AWSS3StorageListRequest
import com.amplifyframework.storage.s3.service.StorageService
import com.amplifyframework.storage.s3.utils.S3Keys
import java.time.Instant
import java.util.Date
import java.util.concurrent.ExecutorService

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

class AWSS3StorageListAsyncOperation<R, T, E>(
    request: AWSS3StorageListRequest,
    private val storageService: StorageService,
    private val executorService: ExecutorService,
    private val authCredentialsProvider: AuthCredentialsProvider,
    private val awsS3StoragePluginConfiguration: AWSS3StoragePluginConfiguration
) : StorageListAsyncOperation<AWSS3StorageListRequest, StorageListResult, StorageException>(request) {

    private var nextToken: String? = null
    private var isTruncated: Boolean = true

    override fun next(onSuccess: Consumer<StorageListResult>, onError: Consumer<StorageException>) {
        executorService.submit {
            awsS3StoragePluginConfiguration.getAWSS3PluginPrefixResolver(authCredentialsProvider).resolvePrefix(
                request.accessLevel,
                request.targetIdentityId,
                { prefix: String ->
                    try {
                        val serviceKey = prefix + request.path
                        val result =
                            storageService.listFiles(serviceKey, prefix, request.maxKeys, nextToken)
                        val items = mutableListOf<StorageItem>()
                        result.contents?.forEach { value ->
                            val key = value.key
                            val lastModified = value.lastModified
                            val eTag = value.eTag
                            if (key != null && lastModified != null && eTag != null) {
                                items += StorageItem(
                                    S3Keys.extractAmplifyKey(key, prefix),
                                    value.size,
                                    Date.from(Instant.ofEpochMilli(lastModified.epochSeconds)),
                                    eTag,
                                    null
                                )
                            }
                        }
                        isTruncated = result.isTruncated
                        nextToken = result.nextContinuationToken
                        onSuccess.accept(StorageListResult.fromItems(items))
                    } catch (exception: Exception) {
                        onError.accept(
                            StorageException(
                                "Something went wrong with your AWS S3 Storage list operation",
                                exception,
                                "See attached exception for more information and suggestions"
                            )
                        )
                    }
                },
                onError
            )
        }
    }

    override fun hasNext(): Boolean = isTruncated
}
