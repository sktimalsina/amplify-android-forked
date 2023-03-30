/*
 * Copyright 2023 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.amplifyframework.statemachine.codegen.events

import com.amplifyframework.auth.AuthSession
import com.amplifyframework.statemachine.StateMachineEvent
import com.amplifyframework.statemachine.codegen.data.AssociateSoftwareTokenData
import com.amplifyframework.statemachine.codegen.data.AuthChallenge
import java.lang.Exception
import java.util.Date

internal class SetupSoftwareTokenEvent(val eventType: EventType, override val time: Date? = null) :
        StateMachineEvent {

     sealed class EventType {
         data class AssociateSoftwareToken(val authChallenge: AuthChallenge): EventType()
         data class WaitForAnswer(val associateSoftwareTokenData: AssociateSoftwareTokenData): EventType()
         data class VerifyChallengeAnswer(val answer: String?): EventType()
         data class RespondToAuthChallenge(val username: String?, val session: String?): EventType()
         data class ThrowError(val exception: Exception) : EventType()
     }

    override val type: String = eventType.javaClass.simpleName
}