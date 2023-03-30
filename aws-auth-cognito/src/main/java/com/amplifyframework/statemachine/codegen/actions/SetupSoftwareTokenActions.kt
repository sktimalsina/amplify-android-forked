package com.amplifyframework.statemachine.codegen.actions

import com.amplifyframework.statemachine.Action
import com.amplifyframework.statemachine.codegen.data.AuthChallenge
import com.amplifyframework.statemachine.codegen.events.SetupSoftwareTokenEvent

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
internal interface StartSoftwareTokenSetupActions {
    fun startSoftwareTokenSetup(
        event: SetupSoftwareTokenEvent.EventType,
        challenge: AuthChallenge
    ): Action

    fun associateSoftwareToken(
        event: SetupSoftwareTokenEvent.EventType,
        challenge: AuthChallenge
    ): Action

    fun verifySoftwareTokenSetup(
        event: SetupSoftwareTokenEvent.EventType,
        verifySoftwareTokenUserCode: String?,
        username: String?,
        session: String?
    ): Action

    fun respondToAuthChallenge(
        event: SetupSoftwareTokenEvent.EventType,
        username: String?,
        session: String?
    ): Action


}