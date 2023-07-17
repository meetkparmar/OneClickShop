package com.meet.project.oneclickshop.ui.fragments

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.meet.project.oneclickshop.R
import com.meet.project.oneclickshop.network.models.ProductDetail
import com.meet.project.oneclickshop.ui.theme.ThemeColor
import com.meet.project.oneclickshop.ui.theme.ThemeTypography
import com.meet.project.oneclickshop.utils.ErrorScreen
import com.meet.project.oneclickshop.utils.getScreenWidth
import com.meet.project.oneclickshop.utils.roundTo2Decimal

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ProductsListScreen(
    viewModel: ProductViewModel,
    addProductClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ThemeColor.PrimaryLight)
    ) {
        Column {
            val keyboardController = LocalSoftwareKeyboardController.current
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                value = viewModel.searchedText,
                onValueChange = { viewModel.searchedTextChange(it) },
                label = { Text(stringResource(id = R.string.search_product)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }
                ),
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "search_icon"
                    )
                },
                maxLines = 1,
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = ThemeColor.Secondary,
                    focusedIndicatorColor = ThemeColor.Secondary,
                    backgroundColor = ThemeColor.PrimaryLight
                )
            )

            if (viewModel.productListScreenLoading) {
                LoadingScreen()
            } else if (viewModel.errorScreenForProductList) {
                ErrorScreen(msg = viewModel.errorMessageForProductList)
            } else {
                ProductListContent(viewModel = viewModel)
            }
        }

        Surface(
            modifier = Modifier
                .padding(24.dp)
                .size(48.dp)
                .align(Alignment.BottomEnd)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) { addProductClick() },
            color = ThemeColor.Secondary,
            shape = RoundedCornerShape(50),
            shadowElevation = 2.dp
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = "add_product",
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.Center)
                    .padding(8.dp),
                tint = ThemeColor.PrimaryLight
            )
        }
    }
}

@Composable
fun ProductListContent(
    viewModel: ProductViewModel,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        viewModel.searchedProductsList.forEachIndexed { index, item ->
            item(key = "ProductItem_$index") {
                ProductItem(item = item)
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = ThemeColor.Secondary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.loading),
                style = ThemeTypography.subtitle1,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ProductItem(item: ProductDetail) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        color = ThemeColor.Primary,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (item.image.isNullOrEmpty()) {
                Image(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(shape = RoundedCornerShape(8.dp)),
                    painter = painterResource(id = R.drawable.gift),
                    contentDescription = "default-image",
                    contentScale = ContentScale.Crop
                )
            } else {
                AsyncImage(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(shape = RoundedCornerShape(8.dp)),
                    model = item.image,
                    contentDescription = "product_image",
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier
                    .widthIn(max = getScreenWidth() - 144.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = item.product_name?.replace("'", "").orEmpty(),
                    style = ThemeTypography.h6,
                    color = ThemeColor.Secondary,
                    maxLines = 1
                )
                Text(
                    text = stringResource(
                        id = R.string.product_type_detail,
                        item.product_type?.replace("'", "").orEmpty()
                    ),
                    style = ThemeTypography.caption,
                    color = ThemeColor.Secondary,
                    maxLines = 1
                )
                Text(
                    text = if (item.tax == 0.0) "Price - ₹${item.price.roundTo2Decimal()} + 0% Tax"
                    else "Price - ₹${item.price.roundTo2Decimal()} + ${item.tax.roundTo2Decimal()}% Taxes",
                    style = ThemeTypography.caption,
                    color = ThemeColor.Secondary,
                    maxLines = 1
                )
            }
        }
    }
}
