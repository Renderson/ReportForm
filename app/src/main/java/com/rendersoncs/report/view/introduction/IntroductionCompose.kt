package com.rendersoncs.report.view.introduction

import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.rendersoncs.report.R
import com.rendersoncs.report.model.OnBoardingItems

@OptIn(ExperimentalPagerApi::class)
@Composable
fun IntroductionScreenComposable() {

    val onBoardingPages = remember {
        listOf(
            OnBoardingItems(
                "Teste 1",//context.getString(R.string.onboarding_first_title),
                "Teste description page 1",//context.getString(R.string.onboarding_first_description),
                R.raw.gears
            ),
            OnBoardingItems(
                "Teste 2",//context.getString(R.string.onboarding_second_title),
                "Teste description page 2",//context.getString(R.string.onboarding_second_description),
                R.raw.gears
            ),
            /*OnBoardingItems(
                context.getString(R.string.onboarding_third_title),
                context.getString(R.string.onboarding_third_description),
                R.drawable.onboarding_3
            ),
            OnBoardingItems(
                context.getString(R.string.onboarding_fourth_title),
                context.getString(R.string.onboarding_fourth_description),
                R.drawable.onboarding_4
            )*/
        )
    }

    val pagerState = rememberPagerState(pageCount = onBoardingPages.size)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
            ) { page ->
                val onBoardingPage = onBoardingPages[page]
                OnBoardingPage(
                    onBoardingItem = onBoardingPage
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxSize()
                .height(150.dp)
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .align(Alignment.BottomCenter)
            ) {
                Text(
                    text = "NEXT",
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 22.dp, start = 18.dp, end = 18.dp)
                )
            }
        }
    }
}

@Composable
fun OnBoardingPage(onBoardingItem: OnBoardingItems) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AndroidView(
            modifier = Modifier.size(300.dp),
            factory = { ctx ->
                ImageView(ctx).apply {
                    val drawable = ContextCompat.getDrawable(ctx, onBoardingItem.image)
                    setImageDrawable(drawable)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }
            })
        Text(
            text = onBoardingItem.title,
            textAlign = TextAlign.Center,
            color = Color.Black,
            style = TextStyle(fontWeight = FontWeight.Bold),
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 22.dp, start = 18.dp, end = 18.dp)
        )
        Text(
            text = onBoardingItem.description,
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            color = Color.Black,
            modifier = Modifier.padding(top = 6.dp, start = 18.dp, end = 18.dp)
        )
    }
}

@Preview
@Composable
private fun PreviewIntroductionScreenComposable() {
    IntroductionScreenComposable()
}