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

import com.amplifyframework.auth.cognito.AuthEnvironment
import com.amplifyframework.auth.cognito.helpers.JWTParser
import com.amplifyframework.statemachine.codegen.data.HostedUISignInOptions
import com.amplifyframework.statemachine.Action
import com.amplifyframework.statemachine.codegen.actions.HostedUIActions
import com.amplifyframework.statemachine.codegen.data.SignInMethod
import com.amplifyframework.statemachine.codegen.data.SignedInData
import com.amplifyframework.statemachine.codegen.events.AuthenticationEvent
import com.amplifyframework.statemachine.codegen.events.HostedUIEvent
import java.util.*

object HostedUICognitoActions : HostedUIActions {

    override fun showHostedUI(event: HostedUIEvent.EventType.ShowHostedUI) =
        Action<AuthEnvironment>("InitHostedUIAuth") { id, dispatcher ->
            logger?.verbose("$id Starting execution")
            try {
                if (hostedUIClient == null) throw Exception() // TODO: error messaging

                hostedUIClient.launchCustomTabs(
                    event.hostedUISignInData.callingActivity,
                    event.hostedUISignInData.options
                )
            } catch (e: Exception) {
                val errorEvent = HostedUIEvent(HostedUIEvent.EventType.ThrowError(e))
                logger?.verbose("$id Sending event ${errorEvent.type}")
                dispatcher.send(errorEvent)
                val evt = AuthenticationEvent(AuthenticationEvent.EventType.CancelSignIn())
                logger?.verbose("$id Sending event ${evt.type}")
                dispatcher.send(evt)
            }
        }

    override fun fetchHostedUISignInToken(event: HostedUIEvent.EventType.FetchToken, options: HostedUISignInOptions) =
        Action<AuthEnvironment>("InitHostedUITokenFetch") { id, dispatcher ->
            logger?.verbose("$id Starting execution")
            val evt = try {
                if (hostedUIClient == null) throw Exception() // TODO: error messaging

                val token = hostedUIClient.fetchToken(event.uri, options)
                val userId = token.accessToken?.let { JWTParser.getClaim(it, "sub") } ?: ""
                val username = token.accessToken?.let { JWTParser.getClaim(it, "username") } ?: ""

                val signedInData = SignedInData(
                    userId,
                    username,
                    Date(),
                    SignInMethod.SOCIAL,
                    token
                )
                val tokenFetchedEvent = HostedUIEvent(HostedUIEvent.EventType.TokenFetched)
                logger?.verbose("$id Sending event ${tokenFetchedEvent.type}")
                dispatcher.send(tokenFetchedEvent)

                AuthenticationEvent(AuthenticationEvent.EventType.SignInCompleted(signedInData))
            } catch (e: Exception) {
                val errorEvent = HostedUIEvent(HostedUIEvent.EventType.ThrowError(e))
                logger?.verbose("$id Sending event ${errorEvent.type}")
                dispatcher.send(errorEvent)

                AuthenticationEvent(AuthenticationEvent.EventType.CancelSignIn())
            }
            logger?.verbose("$id Sending event ${evt.type}")
            dispatcher.send(evt)
        }

}
