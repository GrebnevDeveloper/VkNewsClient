package com.grebnev.vknewsclient.presentation.main.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.grebnev.vknewsclient.R
import com.vk.id.AccessToken
import com.vk.id.VKIDAuthFail
import com.vk.id.auth.VKIDAuthUiParams
import com.vk.id.onetap.common.OneTapStyle
import com.vk.id.onetap.common.button.style.OneTapButtonCornersStyle
import com.vk.id.onetap.compose.onetap.OneTap

@Composable
fun VkIdAuthScreen(
    onSuccessAuth: (accessToken: AccessToken) -> Unit,
    onFailureAuth: (fail: VKIDAuthFail) -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                modifier = Modifier.size(100.dp),
                painter = painterResource(R.drawable.vk_v2),
                contentDescription = "Vk logo",
            )
            Spacer(modifier = Modifier.height(100.dp))
            OneTap(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                style =
                    if (isSystemInDarkTheme()) {
                        OneTapStyle.Dark(cornersStyle = OneTapButtonCornersStyle.Rounded)
                    } else {
                        OneTapStyle.Light(cornersStyle = OneTapButtonCornersStyle.Rounded)
                    },
                onAuth = { _, accessToken: AccessToken ->
                    onSuccessAuth(accessToken)
                },
                onFail = { _, fail: VKIDAuthFail ->
                    onFailureAuth(fail)
                },
                authParams =
                    VKIDAuthUiParams {
                        scopes = setOf("wall", "friends")
                    },
            )
        }
    }
}