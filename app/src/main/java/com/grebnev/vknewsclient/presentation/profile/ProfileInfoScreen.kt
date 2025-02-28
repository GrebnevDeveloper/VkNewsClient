package com.grebnev.vknewsclient.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.grebnev.vknewsclient.R
import com.grebnev.vknewsclient.domain.entity.ProfileInfo
import com.grebnev.vknewsclient.presentation.base.ErrorScreenWithRetry
import com.grebnev.vknewsclient.presentation.base.LoadingIndicator
import com.grebnev.vknewsclient.presentation.getApplicationComponent
import com.grebnev.vknewsclient.ui.theme.VkContainer

@Composable
fun ProfileInfoScreen(
    onLogout: () -> Unit
) {
    val component = getApplicationComponent()
    val viewModel: ProfileInfoViewModel = viewModel(factory = component.getViewModelFactory())
    val screenState = viewModel.screenState.collectAsState(ProfileInfoScreenState.Initial)

    ProfileInfoScreenContent(
        screenState = screenState,
        onLogout = onLogout,
        viewModel = viewModel
    )
}

@Composable
private fun ProfileInfoScreenContent(
    screenState: State<ProfileInfoScreenState>,
    onLogout: () -> Unit,
    viewModel: ProfileInfoViewModel
) {
    when (val currentState = screenState.value) {
        is ProfileInfoScreenState.Profile -> {
            ProfileInfo(
                profileInfo = currentState.profileInfo,
                onLogout = { onLogout() }
            )
        }

        is ProfileInfoScreenState.Error -> {
            ErrorScreenWithRetry(
                retry = { viewModel.refreshedProfileInfo() },
                errorMessage = currentState.message
            )
        }

        is ProfileInfoScreenState.Loading -> {
            LoadingIndicator()
        }

        is ProfileInfoScreenState.Initial -> {}
    }
}

@Composable
private fun ProfileInfo(
    profileInfo: ProfileInfo,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 16.dp,
                vertical = 5.dp
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = profileInfo.avatarUrl,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            contentDescription = "Avatar profile"
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row {
            Text(
                fontSize = 28.sp,
                text = profileInfo.firstName,
                color = MaterialTheme.colorScheme.onSecondary
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                fontSize = 28.sp,
                text = profileInfo.lastName,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(10.dp),
            onClick = { onLogout() },
            colors = ButtonColors(
                containerColor = VkContainer,
                contentColor = Color.White,
                disabledContentColor = ButtonDefaults.buttonColors().disabledContentColor,
                disabledContainerColor = ButtonDefaults.buttonColors().disabledContainerColor
            )
        ) {
            Text(
                text = stringResource(R.string.logout)
            )
        }
    }
}