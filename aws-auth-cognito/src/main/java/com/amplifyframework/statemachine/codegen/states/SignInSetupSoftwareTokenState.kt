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
package com.amplifyframework.statemachine.codegen.states

import com.amplifyframework.statemachine.State
import com.amplifyframework.statemachine.StateMachineEvent
import com.amplifyframework.statemachine.StateMachineResolver
import com.amplifyframework.statemachine.StateResolution
import com.amplifyframework.statemachine.codegen.actions.StartSoftwareTokenSetupActions
import com.amplifyframework.statemachine.codegen.data.AssociateSoftwareTokenData
import com.amplifyframework.statemachine.codegen.data.AuthChallenge
import com.amplifyframework.statemachine.codegen.data.SignInData
import com.amplifyframework.statemachine.codegen.events.SetupSoftwareTokenEvent
import com.amplifyframework.statemachine.codegen.events.SignInEvent


internal sealed class SignInSetupSoftwareTokenState: State {
    data class NotStarted(val id: String = "") : SignInSetupSoftwareTokenState()

    data class AssociateSoftwareToken(val challenge: AuthChallenge) : SignInSetupSoftwareTokenState()

    data class WaitingForAnswer(val associateSoftwareTokenData: AssociateSoftwareTokenData, val challenge: AuthChallenge) :
        SignInSetupSoftwareTokenState()

    data class Verifying(
        val verifySoftwareTokenUserCode: String?,
        val session: String?
    ) : SignInSetupSoftwareTokenState()

    data class RespondToAuthChallenge(
        val username: String?,
        val session: String?
    ) : SignInSetupSoftwareTokenState()

    data class Done(val id: String = "") : SignInSetupSoftwareTokenState()

    data class Error(val exception: Exception) : SignInSetupSoftwareTokenState()

    class Resolver(private val challengeActions: StartSoftwareTokenSetupActions
    ) : StateMachineResolver<SignInSetupSoftwareTokenState> {
        override val defaultState: SignInSetupSoftwareTokenState = NotStarted()

        override fun resolve(
            oldState: SignInSetupSoftwareTokenState,
            event: StateMachineEvent
        ): StateResolution<SignInSetupSoftwareTokenState> {
            val defaultResolution = StateResolution(oldState)
            val challengeEvent = (event as? SetupSoftwareTokenEvent)?.eventType
            return when (oldState) {
                is NotStarted -> when(challengeEvent) {
                    is SetupSoftwareTokenEvent.EventType.AssociateSoftwareToken -> {
                        val action = challengeActions.associateSoftwareToken(challengeEvent, challengeEvent.authChallenge)
                        StateResolution(SignInSetupSoftwareTokenState.AssociateSoftwareToken(challengeEvent.authChallenge), listOf(action))
                    }
                    else -> defaultResolution
                }
                is AssociateSoftwareToken -> when(challengeEvent) {
                    is SetupSoftwareTokenEvent.EventType.WaitForAnswer -> {
                        StateResolution(SignInSetupSoftwareTokenState.WaitingForAnswer(challengeEvent.associateSoftwareTokenData, oldState.challenge))
                    }
                    else -> defaultResolution
                }
                is WaitingForAnswer -> when(challengeEvent) {
                    is SetupSoftwareTokenEvent.EventType.VerifyChallengeAnswer -> {
                        val action = challengeActions.verifySoftwareTokenSetup(challengeEvent, challengeEvent.answer, oldState.challenge.username, oldState.associateSoftwareTokenData.session)
                        StateResolution(SignInSetupSoftwareTokenState.Verifying(challengeEvent.answer, oldState.associateSoftwareTokenData.session), listOf(action))
                    }
                    else -> defaultResolution
                }
                is Verifying -> when(challengeEvent) {
                    is SetupSoftwareTokenEvent.EventType.RespondToAuthChallenge ->  {
                        val action = challengeActions.respondToAuthChallenge(challengeEvent, challengeEvent.username, challengeEvent.session)
                        StateResolution(SignInSetupSoftwareTokenState.RespondToAuthChallenge(challengeEvent.username, oldState.session), listOf(action))
                    }
                    else -> defaultResolution
                }
                else -> defaultResolution
            }
        }
    }



}