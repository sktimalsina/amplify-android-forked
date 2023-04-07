package com.amplifyframework.auth.result

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
data class AuthSetupSoftwareTokenResult(
    val nextStep: AuthSetupSoftwareTokenStep
) {
    val isSoftwareTokenSetupComplete = when(nextStep) {
        is AuthSetupSoftwareTokenStep.VerifySoftwareToken -> false
        is AuthSetupSoftwareTokenStep.Done -> true
    }
}

sealed class AuthSetupSoftwareTokenStep {
    data class VerifySoftwareToken(val setupSoftwareTokenResult: SetupSoftwareTokenResult): AuthSetupSoftwareTokenStep()
    object Done: AuthSetupSoftwareTokenStep()
}

data class SetupSoftwareTokenResult(
    val secretCode: String,
    val setupUri: String
)