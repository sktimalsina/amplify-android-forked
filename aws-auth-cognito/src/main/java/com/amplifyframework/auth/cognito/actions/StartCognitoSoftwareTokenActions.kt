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
package com.amplifyframework.auth.cognito.actions

import aws.sdk.kotlin.services.cognitoidentityprovider.associateSoftwareToken
import aws.sdk.kotlin.services.cognitoidentityprovider.model.ChallengeNameType
import aws.sdk.kotlin.services.cognitoidentityprovider.model.VerifySoftwareTokenResponseType
import aws.sdk.kotlin.services.cognitoidentityprovider.respondToAuthChallenge
import aws.sdk.kotlin.services.cognitoidentityprovider.verifySoftwareToken
import com.amplifyframework.auth.cognito.AuthEnvironment
import com.amplifyframework.auth.cognito.helpers.SignInChallengeHelper
import com.amplifyframework.statemachine.Action
import com.amplifyframework.statemachine.StateMachineEvent
import com.amplifyframework.statemachine.codegen.actions.StartSoftwareTokenSetupActions
import com.amplifyframework.statemachine.codegen.data.AssociateSoftwareTokenData
import com.amplifyframework.statemachine.codegen.data.AuthChallenge
import com.amplifyframework.statemachine.codegen.events.SetupSoftwareTokenEvent

internal object StartCognitoSoftwareTokenActions : StartSoftwareTokenSetupActions {


    override fun startSoftwareTokenSetup(event: SetupSoftwareTokenEvent.EventType, challenge: AuthChallenge) =
        Action<AuthEnvironment>("AssociateSoftwareToken") { id, dispatcher ->
            logger.verbose("$id Starting execution")
            dispatcher.send(SetupSoftwareTokenEvent(SetupSoftwareTokenEvent.EventType.AssociateSoftwareToken(challenge)))
        }

    override fun associateSoftwareToken(
        event: SetupSoftwareTokenEvent.EventType, challenge: AuthChallenge
    ): Action = Action<AuthEnvironment>("AssociateSoftwareToken") { id, dispatcher ->
        logger.verbose("$id Starting execution")
        val evt = try {
            val response = cognitoAuthService.cognitoIdentityProviderClient?.associateSoftwareToken {
                this.session = challenge.session
            }
            response?.let {
                SetupSoftwareTokenEvent(
                    SetupSoftwareTokenEvent.EventType.WaitForAnswer(
                        AssociateSoftwareTokenData(it.secretCode, it.session)
                    )
                )
            } ?: SetupSoftwareTokenEvent(
                SetupSoftwareTokenEvent.EventType.ThrowError(Exception("Software token setup failed"))
            )
        } catch (e: Exception) {
            SetupSoftwareTokenEvent(
                SetupSoftwareTokenEvent.EventType.ThrowError(e)
            )
        }
        dispatcher.send(evt)
    }

    override fun verifySoftwareTokenSetup(
        event: SetupSoftwareTokenEvent.EventType,
        verifySoftwareTokenUserCode: String?,
        username: String?,
        session: String?
    ): Action = Action<AuthEnvironment>("VerifySoftwareTokenSetup") { id, dispatcher ->
        logger.verbose("$id Starting execution")
        val evt = try {
            val response = cognitoAuthService.cognitoIdentityProviderClient?.verifySoftwareToken {
                userCode = verifySoftwareTokenUserCode
                this.session = session
            }

            response?.let {
                when (it.status) {
                    is VerifySoftwareTokenResponseType.Success -> {
                        SetupSoftwareTokenEvent(
                            SetupSoftwareTokenEvent.EventType.RespondToAuthChallenge(
                                username, it.session
                            )
                        )
                    }
                    else -> {
                        SetupSoftwareTokenEvent(
                            SetupSoftwareTokenEvent.EventType.ThrowError(Exception("Software token verification failed"))
                        )
                    }
                }
            } ?: SetupSoftwareTokenEvent(
                SetupSoftwareTokenEvent.EventType.ThrowError(Exception("Software token verification failed"))
            )
        } catch (exception: Exception) {
            SetupSoftwareTokenEvent(
                SetupSoftwareTokenEvent.EventType.ThrowError(exception)
            )
        }
        dispatcher.send(evt)
    }

    override fun respondToAuthChallenge(
        event: SetupSoftwareTokenEvent.EventType,
        username: String?,
        session: String?
    ): Action = Action<AuthEnvironment>("RespondToAuthChallenge") { id, dispatcher ->
        logger.verbose("$id Starting execution")
        val evt = try {
            val challengeResponses = mutableMapOf<String, String>()

            if (!username.isNullOrEmpty()) {
                challengeResponses["USERNAME"] = username
            }
            val response = cognitoAuthService.cognitoIdentityProviderClient?.respondToAuthChallenge {
                this.session = session
                this.challengeResponses = challengeResponses
                challengeName = ChallengeNameType.MfaSetup
                clientId = configuration.userPool?.appClient
            }

            response?.let {
                SignInChallengeHelper.evaluateNextStep(
                    username = username ?: "",
                    challengeNameType = response.challengeName,
                    session = response.session,
                    challengeParameters = response.challengeParameters,
                    authenticationResult = response.authenticationResult
                )
            } ?: SetupSoftwareTokenEvent(
                SetupSoftwareTokenEvent.EventType.ThrowError(Exception("Software token verification failed"))
            )
        } catch (exception: Exception) {
            SetupSoftwareTokenEvent(
                SetupSoftwareTokenEvent.EventType.ThrowError(exception)
            )
        }
        dispatcher.send(evt)
    }
}