package com.rendersoncs.report.view.introduction

import android.os.Build
import androidx.annotation.FloatRange
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.rendersoncs.report.R
import com.rendersoncs.report.model.LoaderIntro
import com.rendersoncs.report.model.OnBoardingItems

@RequiresApi(Build.VERSION_CODES.M)
@OptIn(ExperimentalPagerApi::class)
@Composable
fun IntroductionTest2(
    onButtonClicked: () -> Unit
) {
    val items = ArrayList<OnBoardingItems>()

    items.add(
        OnBoardingItems(
            "Bem-vindo ao nosso aplicativo de relatórios!",
            "Simplifique o processo de criação de relatórios e otimize sua produtividade. Com nosso aplicativo, você pode facilmente registrar e acompanhar inspeções, garantindo um trabalho eficiente e organizado.",
            R.raw.gears,
        )
    )

    items.add(
        OnBoardingItems(
            "Title 2",
            "Lorem ipsum is a placeholder text commonly used to demonstrate the visual form of a document or a typeface.",
            R.raw.takepicture,
        )
    )

    items.add(
        OnBoardingItems(
            "Crie relatórios precisos e detalhados",
            "Utilize nossa lista de verificação intuitiva para registrar os resultados das inspeções. Avalie cada item, e adicione comentários e fotos para fornecer informações adicionais.",
            R.raw.gears,
        )
    )
    val pagerState = rememberPagerState(
        pageCount = items.size,
        //initialOffscreenLimit = 2,
        infiniteLoop = false,
        initialPage = 0
    )

    OnBoardingPager(
        onButtonClicked,
        item = items,
        pagerState = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
    )
}

@RequiresApi(Build.VERSION_CODES.M)
@ExperimentalPagerApi
@Composable
fun OnBoardingPager(
    onButtonClicked: () -> Unit,
    item: List<OnBoardingItems>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState
            ) { page ->
                Column(
                    modifier = Modifier
                        .padding(60.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    /* Image(
                         painter = painterResource(id = item[page].image),
                         contentDescription = item[page].title,
                         modifier = Modifier
                             .height(250.dp)
                             .fillMaxWidth()
                     )*/
                    LoaderIntro(
                        modifier = Modifier
                            .size(200.dp)
                            .fillMaxWidth()
                            .align(alignment = Alignment.CenterHorizontally), item[page].image
                    )
                    Text(
                        text = item[page].title,
                        modifier = Modifier.padding(top = 50.dp),
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        //style = MaterialTheme.typography.body2,
                    )

                    Text(
                        text = item[page].description,
                        modifier = Modifier.padding(top = 30.dp, start = 20.dp, end = 20.dp),
                        color = Color.Black,
                        textAlign = TextAlign.Center
                        //style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            PagerIndicator(item.size, pagerState.currentPage)
        }
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomSection(
                onButtonClicked,
                pagerState.currentPage
            )
        }
    }
}

@ExperimentalPagerApi
@Composable
fun rememberPagerState(
    pageCount: Int,
    initialPage: Int = 0,
    @FloatRange(from = 0.0, to = 1.0) initialPageOffset: Float = 0f,
    initialOffscreenLimit: Int = 1,
    infiniteLoop: Boolean = false
): PagerState = rememberSaveable(
    saver = PagerState.Saver
) {
    PagerState(
        pageCount = pageCount,
        currentPage = initialPage,
        currentPageOffset = initialPageOffset,
        offscreenLimit = initialOffscreenLimit,
        infiniteLoop = infiniteLoop
    )
}

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun PagerIndicator(
    size: Int,
    currentPage: Int
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.padding(top = 60.dp)
    ) {
        repeat(size) {
            Indicator(isSelected = it == currentPage)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun Indicator(isSelected: Boolean) {
    val width = animateDpAsState(targetValue = if (isSelected) 25.dp else 10.dp)

    Box(
        modifier = Modifier
            .padding(1.dp)
            .height(10.dp)
            .width(width.value)
            .clip(CircleShape)
            .background(
                if (isSelected) Color(LocalContext.current.getColor(R.color.colorPrimary)) else Color(
                    LocalContext.current.getColor(R.color.blue_disabled)
                )
            )
    )
}

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun BottomSection(
    onButtonClicked: () -> Unit,
    currentPager: Int) {
    Row(
        modifier = Modifier
            .padding(bottom = 20.dp)
            .fillMaxWidth(),
        horizontalArrangement = when (currentPager) {
            1 -> {
                Arrangement.SpaceBetween
            }

            0 -> {
                Arrangement.End
            }

            else -> Arrangement.Center
        }
    ) {
        when (currentPager) {
            2 -> {
                OutlinedButton(
                    onClick = { onButtonClicked.invoke() },
                    shape = RoundedCornerShape(25),
                    border = BorderStroke(1.dp, Color(LocalContext.current.getColor(R.color.colorPrimary)))
                ) {
                    Text(
                        text = "Get Started",
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 40.dp),
                        color = Color(LocalContext.current.getColor(R.color.colorPrimary))
                    )
                }
            }

            1 -> {
                SkipNextButton(text = "Skip", modifier = Modifier.padding(start = 20.dp))
                SkipNextButton(text = "Next", modifier = Modifier.padding(end = 20.dp))
            }

            else -> {
                SkipNextButton(text = "Next", modifier = Modifier.padding(end = 20.dp))
            }
        }
    }
}

@Composable
fun SkipNextButton(text: String, modifier: Modifier) {
    Text(
        text = text,
        color = Color.Black,
        modifier = modifier,
        fontSize = 18.sp,
        style = MaterialTheme.typography.body2,
        fontWeight = FontWeight.Medium
    )
}