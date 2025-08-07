package com.smartlawyer.ui.screens.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smartlawyer.R
import com.smartlawyer.navigation.Screens
import kotlinx.coroutines.launch

data class IntroPage(
    val title: String,
    val description: String,
    val imageRes: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntroScreen(navController: NavController) {
    val pages = listOf(
        IntroPage(
            title = "مرحبا بك في سمارت لوير",
            description = "إدارة القضايا والعملاء بطريقة ذكية وسهلة.",
            imageRes = R.drawable.intro1
        ),
        IntroPage(
            title = "تتبع القضايا",
            description = "تابع سير القضايا والمواعيد بسهولة.",
            imageRes = R.drawable.intro2
        ),
        IntroPage(
            title = "تنظيم العملاء",
            description = "إدارة كاملة لبيانات عملائك ومحاميّك.",
            imageRes = R.drawable.intro3
        )
    )

    val pagerState = rememberPagerState { pages.size }
    val coroutineScope = rememberCoroutineScope()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            bottomBar = {
                BottomAppBar(
                    actions = {
                        if (pagerState.currentPage < pages.lastIndex) {
                            TextButton(onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pages.lastIndex)
                                }
                            }) {
                                Text("تخطي")
                            }
                        }
                    },
                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            coroutineScope.launch {
                                if (pagerState.currentPage < pages.lastIndex) {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                } else {
                                    navController.navigate(Screens.Login.route) { // FIXED: Use correct route
                                        popUpTo(Screens.Intro.route) { inclusive = true } // FIXED: Use correct route
                                    }
                                }
                            }
                        }) {
                            Text(if (pagerState.currentPage == pages.lastIndex) "ابدأ" else "التالي")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) { page ->
                    val intro = pages[page]
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = intro.imageRes),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = intro.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = intro.description,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Dots Indicator
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(pages.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(if (isSelected) 12.dp else 8.dp)
                                .background(
                                    color = if (isSelected)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }
        }
    }
}
