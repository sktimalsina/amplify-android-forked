package com.amplifyframework.auth.result

import com.amplifyframework.auth.AuthCodeDeliveryDetails

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
data class AuthSignInResultV2(
    val nextStep: AuthSignInStepV2
) {

    val isSignedIn = when(nextStep) {
        is AuthSignInStepV2.Done -> true
        else -> false
    }

}

sealed interface AuthSignInStepV2 {
    data class ConfirmSignInWithSMSMFACode(
        val codeDeliveryDetails: AuthCodeDeliveryDetails,
        val additionalInfo: Map<String, String>?
    ) : AuthSignInStepV2()

    data class ConfirmSignInWithCustomChallenge(val additionalInfo: Map<String, String>?) : AuthSignInStepV2
    data class ConfirmSignInWithNewPassword(val additionalInfo: Map<String, String>?) : AuthSignInStepV2
    data class ConfirmSignInWithSoftwareTokenMFACode(val additionalInfo: Map<String, String>?) : AuthSignInStepV2
    data class ConfirmSignInWithSoftwareTokenSetup(val setupSoftwareTokenResult: SetupSoftwareTokenResult, val additionalInfo: Map<String, String>?) : AuthSignInStepV2
    data class ConfirmSignInWithMFASelection(val additionalInfo: Map<String, String>?) : AuthSignInStepV2
    data class ResetPassword(val additionalInfo: Map<String, String>?) : AuthSignInStepV2
    data class ConfirmSignUp(val additionalInfo: Map<String, String>?) : AuthSignInStepV2
    object Done : AuthSignInStepV2

    @AmplifyUnsealedWarning
    object Default: AuthSignInStepV2
}