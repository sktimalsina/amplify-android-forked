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

package com.amplifyframework.auth.cognito

import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.result.AuthSignInResult
import com.amplifyframework.core.Consumer
import com.amplifyframework.statemachine.StateMachineEvent
import com.amplifyframework.statemachine.codegen.data.AuthConfiguration
import com.amplifyframework.statemachine.codegen.data.SignedOutData
import com.amplifyframework.statemachine.codegen.events.AuthenticationEvent
import com.amplifyframework.statemachine.codegen.states.AuthState
import com.amplifyframework.statemachine.codegen.states.AuthenticationState
import com.amplifyframework.statemachine.codegen.states.SignUpState
import io.mockk.*
import org.json.JSONException
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.test.assertTrue

class ExampleAWSCognitoAuthPluginTest {

    private lateinit var authPlugin: AWSCognitoAuthPlugin

    private var authConfiguration = mockk<AuthConfiguration>()
    private var authStateMachine = mockk<AuthStateMachine>(relaxed = true)
    private var credentialStoreStateMachine = mockk<CredentialStoreStateMachine>(relaxed = true)

    @Before
    @Throws(AmplifyException::class, JSONException::class)
    fun setup() {
        every { authStateMachine.listen(any(), any()) } returns UUID.randomUUID()
        authPlugin = AWSCognitoAuthPlugin()
        authPlugin.internalConfigure(authConfiguration, authStateMachine, credentialStoreStateMachine)
    }

    @Test
    fun signInErrorIfNotConfigured() {
        // GIVEN
        val onSuccess = mockk<Consumer<AuthSignInResult>>()
        val onError = mockk<Consumer<AuthException>>(relaxed = true)
        val expectedAuthError = AuthException(
            "Sign in failed.",
            "Cognito User Pool not configured. Please check amplifyconfiguration.json file."
        )
        val currentAuthState = mockk<AuthState> {
            every { authNState } returns AuthenticationState.NotConfigured()
        }
        every { authStateMachine.getCurrentState(captureLambda()) } answers {
            lambda<(AuthState) -> Unit>().invoke(currentAuthState)
        }

        // WHEN
        authPlugin.signIn("username", "password", onSuccess, onError)

        // THEN
        verify(exactly = 0) {onSuccess.accept(any()) }
        verify { onError.accept(expectedAuthError) }
    }

    @Test
    fun signInErrorIfInvalidState() {
        // GIVEN
        val onSuccess = mockk<Consumer<AuthSignInResult>>()
        val onError = mockk<Consumer<AuthException>>(relaxed = true)
        val expectedAuthError = AuthException.InvalidStateException()
        val currentAuthState = mockk<AuthState> {
            every { authNState } returns AuthenticationState.Configured()
        }
        every { authStateMachine.getCurrentState(captureLambda()) } answers {
            lambda<(AuthState) -> Unit>().invoke(currentAuthState)
        }

        // WHEN
        authPlugin.signIn("username", "password", onSuccess, onError)

        // THEN
        verify { onError.accept(expectedAuthError) }
        verify(exactly = 0) {onSuccess.accept(any()) }
    }

    @Test
    fun signInSendsResetIfAlreadySigningUp() {
        // GIVEN
        val onSuccess = mockk<Consumer<AuthSignInResult>>()
        val onError = mockk<Consumer<AuthException>>()
        val eventSlot = CapturingSlot<StateMachineEvent>()
        val currentAuthState = mockk<AuthState> {
            every { authNState } returns AuthenticationState.SigningUp(SignUpState.NotStarted())
        }
        every { authStateMachine.getCurrentState(captureLambda()) } answers {
            lambda<(AuthState) -> Unit>().invoke(currentAuthState)
        }
        every { authStateMachine.send(capture(eventSlot)) } returns Unit

        // WHEN
        authPlugin.signIn("username", "password", onSuccess, onError)

        // THEN
        verify(exactly = 0) {onSuccess.accept(any()) }
        verify(exactly = 0) {onError.accept(any()) }
        assertTrue { eventSlot.isCaptured }
        val capturedEvent = eventSlot.captured
        assertTrue(capturedEvent is AuthenticationEvent && capturedEvent.eventType is AuthenticationEvent.EventType.ResetSignUp)
    }

    @Test
    fun signInPassesRequestIfInValidState() {
        // Given
        val expectedUsername = "user1"
        val expectedPassword = "pass123"
        val onSuccess = mockk<Consumer<AuthSignInResult>>()
        val onError = mockk<Consumer<AuthException>>(relaxed = true)
        val eventSlot = CapturingSlot<StateMachineEvent>()
        val currentAuthState = mockk<AuthState> {
            every { authNState } returns AuthenticationState.SignedOut(SignedOutData())
        }
        every { authStateMachine.getCurrentState(captureLambda()) } answers {
            lambda<(AuthState) -> Unit>().invoke(currentAuthState)
        }
        every { authStateMachine.send(capture(eventSlot)) } returns Unit

        // When
        authPlugin.signIn(expectedUsername, expectedPassword, onSuccess, onError)

        // Then
        verify(exactly = 0) {onSuccess.accept(any()) }
        verify(exactly = 0) {onSuccess.accept(any()) }
        // Would like to ideally verify handoff to the next step but current set up may need to be changed for that
    }
}
