package com.amplifyframework.core.async

import com.amplifyframework.AmplifyException
import com.amplifyframework.core.Consumer
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
abstract class AmplifyAsyncOperation<R, T, E : AmplifyException>(
    val categoryType: CategoryType,
    val request: R
) {

    /**
     * fetch the next set of result.
     * @param onError callback to return result
     * @param onSuccess callback to return error
     */
    abstract fun next(onSuccess: Consumer<T>, onError: Consumer<E>)

    /**
     * Determine whether next set of result is available.
     * @return boolean to indicate more results are available
     */
    abstract fun hasNext(): Boolean
}
