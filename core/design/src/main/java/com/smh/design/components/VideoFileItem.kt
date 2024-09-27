package com.smh.design.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.smh.design.R
import com.smh.design.extension.formatByteSize
import com.smh.design.theme.KPlayerTheme

@Composable
fun VideoFileItem(
    modifier: Modifier = Modifier,
    image: String,
    folderName: String,
    size: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AsyncImage(
            model = image,
            contentDescription = "VideoThumbnail",
            placeholder = painterResource(id = R.drawable.placeholder_default),
            error = painterResource(id = R.drawable.placeholder_default),
            modifier = Modifier
                .clip(RoundedCornerShape(size = 10.dp))
                .width(100.dp)
                .height(70.dp),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.height(70.dp)
        ) {
            Text(
                text = folderName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = size.formatByteSize(),
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview
@Composable
private fun VideoFileItemPreview() {
    KPlayerTheme {
        Surface {
            VideoFileItem(
                folderName = "Folder Name",
                image = "https://www.example.com/image.jpg",
                size = 1024 * 1024 * 100,
                onClick = {}
            )
        }
    }
}