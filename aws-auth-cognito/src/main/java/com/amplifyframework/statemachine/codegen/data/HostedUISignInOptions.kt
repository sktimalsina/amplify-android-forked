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

package com.amplifyframework.statemachine.codegen.data

sealed class HostedUISignInOptions(
    val scopes: List<String>?,
    val idpIdentifier: String?,
    val federationProviderName: String?,
    val signInQueryParameters: Map<String, String>,
    val signOutQueryParameters: Map<String, String>,
    val tokenQueryParameters: Map<String, String>,
    val browserPackage: String?,
    val appClient: String,
    val appSecret: String?,
    val domain: String,
    val signInRedirectURI: String,
    val signOutRedirectURI: String
) {
    class WebSignInOptions(
        scopes: List<String>?,
        idpIdentifier: String?,
        federationProviderName: String?,
        signInQueryParameters: Map<String, String>,
        signOutQueryParameters: Map<String, String>,
        tokenQueryParameters: Map<String, String>,
        browserPackage: String?,
        appClient: String,
        appSecret: String?,
        domain: String,
        signInRedirectURI: String,
        signOutRedirectURI: String,
    ) : HostedUISignInOptions(
        scopes,
        idpIdentifier,
        federationProviderName,
        signInQueryParameters,
        signOutQueryParameters,
        tokenQueryParameters,
        browserPackage,
        appClient,
        appSecret,
        domain,
        signInRedirectURI,
        signOutRedirectURI
    )

    class SocialWebSignInOptions(
        val identityProvider: String?,
        scopes: List<String>?,
        idpIdentifier: String?,
        federationProviderName: String?,
        signInQueryParameters: Map<String, String>,
        signOutQueryParameters: Map<String, String>,
        tokenQueryParameters: Map<String, String>,
        browserPackage: String?,
        appClient: String,
        appSecret: String?,
        domain: String,
        signInRedirectURI: String,
        signOutRedirectURI: String,
    ) : HostedUISignInOptions(
        scopes,
        idpIdentifier,
        federationProviderName,
        signInQueryParameters,
        signOutQueryParameters,
        tokenQueryParameters,
        browserPackage,
        appClient,
        appSecret,
        domain,
        signInRedirectURI,
        signOutRedirectURI
    )
}
