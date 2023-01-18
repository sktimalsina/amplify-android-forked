package com.amplifyframework.storage.s3

import android.content.Context
import android.util.Log
import android.util.Pair
import androidx.annotation.RawRes
import androidx.test.core.app.ApplicationProvider
import com.amplifyframework.auth.AuthPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.logging.AndroidLoggingPlugin
import com.amplifyframework.logging.LogLevel
import com.amplifyframework.storage.StorageAccessLevel
import com.amplifyframework.storage.StorageException
import com.amplifyframework.storage.StorageItem
import com.amplifyframework.storage.operation.StorageListAsyncOperation
import com.amplifyframework.storage.options.StorageListOptions
import com.amplifyframework.storage.options.StorageUploadFileOptions
import com.amplifyframework.storage.result.StorageListResult
import com.amplifyframework.storage.s3.request.AWSS3StorageListRequest
import com.amplifyframework.testutils.Resources
import com.amplifyframework.testutils.Sleep
import com.amplifyframework.testutils.random.RandomTempFile
import com.amplifyframework.testutils.sync.SynchronousAuth
import com.amplifyframework.testutils.sync.SynchronousStorage
import java.io.File
import org.json.JSONException
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test

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

class AWSS3StorageListAsyncTest {

    companion object {
        private val TEST_DIR_NAME = "TEST_DIR_NAME"
        private const val CONFIGURATION_NAME = "amplifyconfiguration"
        private const val CREDENTIALS_RESOURCE_NAME = "credentials"
        private const val COGNITO_CONFIGURATION_TIMEOUT = 10 * 1000L
        private const val UPLOAD_SIZE = 100L
        private lateinit var synchronousAuth: SynchronousAuth
        private lateinit var awsS3StoragePlugin: AWSS3StoragePlugin
        private lateinit var storage: SynchronousStorage

        @BeforeClass
        @JvmStatic
        fun setupBefore() {
            val context = ApplicationProvider.getApplicationContext<Context>()
            @RawRes val resourceId = Resources.getRawResourceId(context, CONFIGURATION_NAME)
            Amplify.Auth.addPlugin(AWSCognitoAuthPlugin() as AuthPlugin<*>)
            awsS3StoragePlugin = AWSS3StoragePlugin()
            Amplify.addPlugin(awsS3StoragePlugin)
            Amplify.Logging.addPlugin(AndroidLoggingPlugin(LogLevel.DEBUG))
            // Get a handle to storage
            val asyncDelegate = TestStorageCategory.create(
                context,
                Resources.getRawResourceId(
                    context, CONFIGURATION_NAME
                )
            )
            storage = SynchronousStorage.delegatingTo(asyncDelegate)
            Amplify.configure(context)
            Sleep.milliseconds(COGNITO_CONFIGURATION_TIMEOUT)
            synchronousAuth = SynchronousAuth.delegatingTo(Amplify.Auth)
            uploadTestFiles()
        }

        private fun readCredentialsFromResource(context: Context, @RawRes resourceId: Int): Pair<String, String>? {
            val resource = Resources.readAsJson(context, resourceId)
            var userCredentials: Pair<String, String>? = null
            return try {
                val credentials = resource.getJSONArray("credentials")
                for (index in 0 until credentials.length()) {
                    val credential = credentials.getJSONObject(index)
                    val username = credential.getString("username")
                    val password = credential.getString("password")
                    userCredentials = Pair(username, password)
                }
                userCredentials
            } catch (jsonReadingFailure: JSONException) {
                throw RuntimeException(jsonReadingFailure)
            }
        }

        @Throws(Exception::class)
        private fun uploadTestFiles() {
            val context = ApplicationProvider.getApplicationContext<Context>()
            // Create a temporary file to upload
            val uploadFile: File = RandomTempFile(UPLOAD_SIZE)
            val uploadName = "TEMP_FILE_KEY"
            val uploadPath = uploadFile.absolutePath
            val uploadKey = "$TEST_DIR_NAME/$uploadName"
            @RawRes val resourceId = Resources.getRawResourceId(context, CREDENTIALS_RESOURCE_NAME)
            val userAndPasswordPair = readCredentialsFromResource(context, resourceId)
            // Upload as GUEST
            // synchronousAuth.signOut();

            // Upload as user one
            synchronousAuth.signOut()
            synchronousAuth.signIn(
                userAndPasswordPair!!.first,
                userAndPasswordPair.second
            )
            repeat(20) {
                val options = StorageUploadFileOptions.builder()
                    .accessLevel(StorageAccessLevel.PUBLIC)
                    .build()
                storage.uploadFile(
                    uploadKey + it,
                    uploadFile,
                    options
                )
            }
        }
    }

    @Test
    fun listAsync() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        @RawRes val resourceId = Resources.getRawResourceId(context, CREDENTIALS_RESOURCE_NAME)
        val userAndPasswordPair = readCredentialsFromResource(context, resourceId)
        synchronousAuth.signOut()
        synchronousAuth.signIn(userAndPasswordPair!!.first, userAndPasswordPair.second)
        val listOptions = StorageListOptions.builder()
            .accessLevel(StorageAccessLevel.PUBLIC)
            .setMaxKeys(15)
            .build()
        val operation =
            awsS3StoragePlugin.list(TEST_DIR_NAME, listOptions)
        val result = mutableListOf<StorageItem>()
        readListFromOperation(
            operation,
            result
        )
        Sleep.milliseconds(5 * 1000)
        Assert.assertEquals(20, result.size)
    }

    private fun readListFromOperation(
        operation:
        StorageListAsyncOperation<AWSS3StorageListRequest, StorageListResult, StorageException>,
        list: MutableList<StorageItem>
    ) {
        operation.next({ it ->
            list.addAll(it.items)
            if (operation.hasNext())
                readListFromOperation(operation, list)
        }, {
            Log.e("AsyncTest", it.toString())
        })

    }
}
