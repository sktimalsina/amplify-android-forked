package com.amplifyframework.storage.operation

import com.amplifyframework.AmplifyException
import com.amplifyframework.core.async.AmplifyAsyncOperation
import com.amplifyframework.core.category.CategoryType

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
abstract class StorageListAsyncOperation<R, T, E : AmplifyException>(
    request: R
) : AmplifyAsyncOperation<R, T, E>(CategoryType.STORAGE, request)
