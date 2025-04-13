package com.grebnev.vknewsclient.presentation.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.grebnev.vknewsclient.R
import com.grebnev.vknewsclient.ui.theme.vkContainer
import java.util.Locale

@Composable
fun ErrorScreenWithRetry(
    retry: () -> Unit = {},
    errorMessage: String,
    tint: Color = MaterialTheme.colorScheme.secondary,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ErrorTextBox(
                errorMessage = errorMessage,
                tint = tint,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(10.dp),
                onClick = { retry() },
                colors =
                    ButtonColors(
                        containerColor = vkContainer,
                        contentColor = Color.White,
                        disabledContentColor = ButtonDefaults.buttonColors().disabledContentColor,
                        disabledContainerColor =
                            ButtonDefaults
                                .buttonColors()
                                .disabledContainerColor,
                    ),
            ) {
                Text(
                    text = stringResource(R.string.retry).uppercase(Locale.getDefault()),
                )
            }
        }
    }
}

@Composable
fun ErrorScreenWithLoading(
    errorMessage: String,
    tint: Color = MaterialTheme.colorScheme.secondary,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ErrorTextBox(
                errorMessage = errorMessage,
                tint = tint,
            )
            Spacer(modifier = modifier.height(24.dp))
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun ErrorTextBox(
    errorMessage: String,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = modifier.size(64.dp),
            painter = painterResource(R.drawable.ic_error),
            contentDescription = null,
            tint = tint,
        )
        Spacer(modifier = modifier.height(16.dp))
        Text(
            text = stringResource(R.string.error_occurred),
            style = MaterialTheme.typography.titleLarge,
        )

        Spacer(modifier = modifier.height(8.dp))
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}