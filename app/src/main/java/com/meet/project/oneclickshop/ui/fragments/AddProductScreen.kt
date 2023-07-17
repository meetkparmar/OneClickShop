package com.meet.project.oneclickshop.ui.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.meet.project.oneclickshop.R
import com.meet.project.oneclickshop.network.models.ProductDetail
import com.meet.project.oneclickshop.ui.theme.ThemeColor
import com.meet.project.oneclickshop.ui.theme.ThemeTypography
import com.meet.project.oneclickshop.utils.ErrorScreen
import com.meet.project.oneclickshop.utils.getScreenWidth
import com.meet.project.oneclickshop.utils.roundTo2Decimal

@Composable
fun AddProductScreen(
    viewModel: ProductViewModel,
    onAddProductClick: () -> Unit,
    addImageClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ThemeColor.PrimaryLight)
    ) {
        if (viewModel.errorScreenForAddProduct) {
            ErrorScreen(msg = viewModel.errorMessageForAddProduct)
        } else if (viewModel.productAdded && viewModel.addProductResponse?.product_details != null) {
            ProductDetailContent(
                item = viewModel.addProductResponse?.product_details!!,
            )
        } else {
            AddProductContent(
                viewModel = viewModel,
                onAddProductClick = onAddProductClick,
                addImageClick = addImageClick
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddProductContent(
    viewModel: ProductViewModel,
    onAddProductClick: () -> Unit,
    addImageClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item(key = "AddProductContent") {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    value = viewModel.productName,
                    onValueChange = { viewModel.productNameTextChange(it) },
                    label = { Text(stringResource(id = R.string.product_name)) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    maxLines = 1,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = ThemeColor.Secondary,
                        focusedIndicatorColor = ThemeColor.Secondary,
                        backgroundColor = ThemeColor.PrimaryLight
                    )
                )
                ExposedDropdownMenuBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    expanded = viewModel.expanded,
                    onExpandedChange = {
                        viewModel.expanded = !viewModel.expanded
                    }
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        value = viewModel.selectedType,
                        onValueChange = { viewModel.productNameTextChange(it) },
                        label = { Text(stringResource(id = R.string.product_type)) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = viewModel.expanded
                            )
                        },
                        maxLines = 1,
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = ThemeColor.Secondary,
                            focusedIndicatorColor = ThemeColor.Secondary,
                            backgroundColor = ThemeColor.PrimaryLight
                        ),
                        readOnly = true
                    )
                    ExposedDropdownMenu(
                        expanded = viewModel.expanded,
                        onDismissRequest = {
                            viewModel.expanded = false
                        }
                    ) {
                        viewModel.productTypeList.forEach { selectionOption ->
                            DropdownMenuItem(
                                onClick = {
                                    viewModel.selectedType = selectionOption
                                    viewModel.expanded = false
                                }
                            ) {
                                Text(text = selectionOption)
                            }
                        }
                    }
                }
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    value = viewModel.price,
                    onValueChange = { viewModel.priceTextChange(it) },
                    label = { Text(stringResource(id = R.string.price)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    maxLines = 1,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = ThemeColor.Secondary,
                        focusedIndicatorColor = ThemeColor.Secondary,
                        backgroundColor = ThemeColor.PrimaryLight
                    ),
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    value = viewModel.taxes,
                    onValueChange = { viewModel.taxesTextChange(it.ifEmpty { "0" }) },
                    label = { Text(stringResource(id = R.string.taxes)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    maxLines = 1,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = ThemeColor.Secondary,
                        focusedIndicatorColor = ThemeColor.Secondary,
                        backgroundColor = ThemeColor.PrimaryLight
                    ),
                )

                if (viewModel.showImage) {
                    if (viewModel.imageAdded) {
                        var imgBitmap: Bitmap? = null

                        if (viewModel.image?.exists() == true) {
                            imgBitmap = BitmapFactory.decodeFile(viewModel.image?.absolutePath)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            painter = rememberImagePainter(data = imgBitmap),
                            contentDescription = "Image",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        OutlinedButton(
                            onClick = { addImageClick() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 16.dp)
                                .align(Alignment.BottomCenter),
                            shape = RoundedCornerShape(12.dp),
                            enabled = true,
                            border = BorderStroke(1.dp, ThemeColor.Secondary),
                            colors = androidx.compose.material.ButtonDefaults.buttonColors(
                                contentColor = ThemeColor.Secondary,
                                backgroundColor = ThemeColor.PrimaryLight
                            )
                        ) {
                            if (viewModel.addImageLoading) {
                                CircularProgressIndicator(
                                    color = ThemeColor.PrimaryLight,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text(
                                    text = stringResource(id = R.string.add_image),
                                    style = ThemeTypography.button,
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .background(Color.Transparent),
                                    color = ThemeColor.Secondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
        if (viewModel.addProductLoading) {
            CircularProgressIndicator(
                color = ThemeColor.Secondary,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.BottomCenter)
            )
        } else {
            Button(
                onClick = { onAddProductClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 16.dp)
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(12.dp),
                enabled = true,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = ThemeColor.Secondary,
                    contentColor = ThemeColor.PrimaryLight
                )
            ) {
                Text(
                    text = stringResource(id = R.string.add_product),
                    style = ThemeTypography.button,
                    modifier = Modifier
                        .wrapContentSize()
                        .background(Color.Transparent),
                    color = ThemeColor.PrimaryLight,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ProductDetailContent(
    item: ProductDetail,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 72.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(32.dp))
            if (item.image.isNullOrEmpty()) {
                Image(
                    modifier = Modifier
                        .size(getScreenWidth() - 64.dp)
                        .clip(shape = RoundedCornerShape(16.dp)),
                    painter = painterResource(id = R.drawable.gift),
                    contentDescription = "default_image",
                    contentScale = ContentScale.Crop
                )
            } else {
                AsyncImage(
                    modifier = Modifier
                        .size(getScreenWidth() - 64.dp)
                        .clip(shape = RoundedCornerShape(16.dp)),
                    model = item.image,
                    contentDescription = "product_image",
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = item.product_name?.replace("'", "").orEmpty(),
                modifier = Modifier.padding(horizontal = 16.dp),
                style = ThemeTypography.h4,
                color = ThemeColor.Secondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(
                    id = R.string.product_type_detail,
                    item.product_type?.replace("'", "").orEmpty()
                ),
                modifier = Modifier.padding(horizontal = 16.dp),
                style = ThemeTypography.subtitle1,
                color = ThemeColor.Secondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (item.tax == 0.0) "Price - ₹${item.price.roundTo2Decimal()} + 0% Tax"
                else "Price - ₹${item.price.roundTo2Decimal()} + ${item.tax.roundTo2Decimal()}% Taxes",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = ThemeTypography.subtitle1,
                color = ThemeColor.Secondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.total, item.totalPrice().roundTo2Decimal()),
                modifier = Modifier.padding(horizontal = 16.dp),
                style = ThemeTypography.subtitle1,
                color = ThemeColor.Secondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.congratulations_text),
                style = ThemeTypography.subtitle1,
                color = ThemeColor.Secondary,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
