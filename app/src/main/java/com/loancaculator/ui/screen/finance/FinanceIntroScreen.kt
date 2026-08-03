package com.loancaculator.ui.screen.finance

import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loancaculator.R
import com.loancaculator.advertisement.NativeAdSlot
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlinx.coroutines.launch

private data class IntroSlide(
    val title: String,
    val description: String,
    @DrawableRes val previewRes: Int,
    val adPlacement: String,
)

private val introSlides = listOf(
    IntroSlide(
        title = "Financial Calculations for Loans",
        description = "Evaluate various personal loans, home mortgages and investment prospects",
        previewRes = R.drawable.intro_preview_home,
        adPlacement = "native_intro1",
    ),
    IntroSlide(
        title = "Financial Calculators",
        description = "Efficient and accurate interest rate calculators for various types of loans.",
        previewRes = R.drawable.intro_preview_result,
        adPlacement = "native_intro2",
    ),
    IntroSlide(
        title = "Unit Conversion",
        description = "Enables seamless conversion between commonly used units encountered.",
        previewRes = R.drawable.intro_preview_tools,
        adPlacement = "native_intro3",
    ),
    IntroSlide(
        title = "Invest with intelligence",
        description = "Enhance your investment strategy by leveraging analytical tools.",
        previewRes = R.drawable.intro_preview_compare,
        adPlacement = "native_intro4",
    ),
)

private const val previewCropX = 270
private const val previewCropY = 62
private const val previewCropWidth = 540
private const val previewCropHeight = 1100
private const val previewAspectRatio = 540f / 1100f

@Composable
fun FinanceIntroScreen(onFinish: () -> Unit) {
    val pager = rememberPagerState(pageCount = { introSlides.size })
    val scope = rememberCoroutineScope()
    val page = pager.currentPage
    val slide = introSlides[page]
    val adPage = remember { Random.nextInt(introSlides.size) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(342.dp)
                .background(Color(0xFF17B0D4)),
        ) {
            Image(
                painter = painterResource(R.drawable.finance_hero),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.52f,
            )
            HorizontalPager(
                state = pager,
                modifier = Modifier.fillMaxSize(),
            ) { index ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    IntroPreviewImage(
                        imageRes = introSlides[index].previewRes,
                        modifier = Modifier
                            .height(336.dp)
                            .width(336.dp * previewAspectRatio),
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(15.dp))
            Text(
                text = slide.title,
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF263640),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = slide.description,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                color = Color(0xFF8CA0A9),
                fontSize = 17.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
            )
            Row(
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                introSlides.indices.forEach { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .size(if (index == page) 13.dp else 9.dp)
                            .clip(CircleShape)
                            .background(if (index == page) Color(0xFF18AFD3) else Color(0xFFBCD4DC)),
                    )
                }
            }
            Button(
                onClick = {
                    if (page == introSlides.lastIndex) {
                        onFinish()
                    } else {
                        scope.launch { pager.animateScrollToPage(page + 1) }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF18AFD3)),
            ) {
                Text(
                    text = if (page == introSlides.lastIndex) "Start" else "Next",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            if (page == adPage) {
                NativeAdSlot(
                    placement = slide.adPlacement,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(205.dp)
                        .padding(top = 8.dp),
                    isSmall = false,
                )
            } else {
                Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun IntroPreviewImage(
    @DrawableRes imageRes: Int,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val bitmap = remember(imageRes) {
        BitmapFactory.decodeResource(context.resources, imageRes).asImageBitmap()
    }
    Canvas(modifier = modifier) {
        drawImage(
            image = bitmap,
            srcOffset = IntOffset(previewCropX, previewCropY),
            srcSize = IntSize(previewCropWidth, previewCropHeight),
            dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt()),
        )
    }
}
