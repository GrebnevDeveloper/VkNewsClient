package com.grebnev.vknewsclient.presentation.news.base

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.grebnev.vknewsclient.R
import com.grebnev.vknewsclient.domain.entity.FeedPost
import com.grebnev.vknewsclient.domain.entity.StatisticItem
import com.grebnev.vknewsclient.domain.entity.StatisticType
import com.grebnev.vknewsclient.ui.theme.darkRed

@Composable
fun PostCard(
    feedPost: FeedPost,
    onCommentsClickListener: (StatisticItem) -> Unit,
    onLikesClickListener: (StatisticItem) -> Unit,
    onSubscribeClickListener: (FeedPost) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card {
        Column(modifier = modifier.padding(8.dp)) {
            PostHeader(feedPost, onSubscribeClickListener)
            Spacer(modifier = modifier.height(10.dp))
            Text(
                text = feedPost.contentText,
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(modifier = modifier.height(10.dp))
            AsyncImage(
                model = feedPost.contentImageUrl,
                modifier =
                    modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
            )
            Spacer(modifier = modifier.height(10.dp))
            Statistics(
                statisticList = feedPost.statisticsList,
                onCommentsClickListener = onCommentsClickListener,
                onLikesClickListener = onLikesClickListener,
                isFavorite = feedPost.isLiked,
            )
        }
    }
}

@Composable
private fun Statistics(
    statisticList: List<StatisticItem>,
    onCommentsClickListener: (StatisticItem) -> Unit,
    onLikesClickListener: (StatisticItem) -> Unit,
    isFavorite: Boolean,
    modifier: Modifier = Modifier,
) {
    Row {
        Row(modifier = modifier.weight(1f)) {
            val viewsItem = statisticList.getItemByType(StatisticType.VIEWS)
            IconWithText(
                iconResId = R.drawable.ic_views_count,
                text = formatStatisticCount(viewsItem.count),
            )
        }
        Row(
            modifier = modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            val sharesItem = statisticList.getItemByType(StatisticType.SHARES)
            IconWithText(
                iconResId = R.drawable.ic_share,
                text = formatStatisticCount(sharesItem.count),
            )
            val commentsItem = statisticList.getItemByType(StatisticType.COMMENTS)
            IconWithText(
                iconResId = R.drawable.ic_comment,
                text = formatStatisticCount(commentsItem.count),
                onItemClickListener = { onCommentsClickListener(commentsItem) },
            )
            val likesItem = statisticList.getItemByType(StatisticType.LIKES)
            IconWithText(
                iconResId = if (isFavorite) R.drawable.ic_like_set else R.drawable.ic_like,
                text = formatStatisticCount(likesItem.count),
                onItemClickListener = { onLikesClickListener(likesItem) },
                tint = if (isFavorite) darkRed else MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@SuppressLint("DefaultLocale")
private fun formatStatisticCount(count: Int): String =
    if (count >= 100_000) {
        String.format("%sK", (count / 1000))
    } else if (count >= 1000) {
        String.format("%.1fK", (count / 1000f))
    } else {
        count.toString()
    }

fun List<StatisticItem>.getItemByType(type: StatisticType): StatisticItem =
    this.find { it.type == type }
        ?: throw IllegalStateException("Not found type from enum class StatisticType")

@Composable
private fun IconWithText(
    iconResId: Int,
    text: String,
    onItemClickListener: (() -> Unit)? = null,
    tint: Color = MaterialTheme.colorScheme.secondary,
) {
    val modifier =
        if (onItemClickListener == null) {
            Modifier
        } else {
            Modifier.clickable {
                onItemClickListener()
            }
        }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(iconResId),
            contentDescription = null,
            tint = tint,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun PostHeader(
    feedPost: FeedPost,
    onSubscribeClickListener: (FeedPost) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier =
            modifier
                .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = feedPost.communityImageUrl,
            modifier =
                modifier
                    .clip(CircleShape)
                    .size(50.dp),
            contentDescription = null,
        )
        Spacer(modifier = modifier.width(5.dp))
        Column(modifier = modifier.weight(1f)) {
            Text(
                text = feedPost.communityName,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = modifier.height(4.dp))
            Text(
                text = feedPost.publicationDate,
                style = MaterialTheme.typography.labelSmall,
            )
        }
        Box {
            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = null,
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            if (!feedPost.isSubscribed) {
                                stringResource(R.string.subscribe)
                            } else {
                                stringResource(R.string.unsubscribe)
                            },
                        )
                    },
                    onClick = { onSubscribeClickListener(feedPost) },
                    leadingIcon = {
                        Icon(
                            painter =
                                painterResource(
                                    if (!feedPost.isSubscribed) {
                                        R.drawable.ic_subscribe
                                    } else {
                                        R.drawable.ic_unsubscribe
                                    },
                                ),
                            contentDescription = null,
                        )
                    },
                )
            }
        }
    }
}