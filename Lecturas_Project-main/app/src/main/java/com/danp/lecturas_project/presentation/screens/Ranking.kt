package com.danp.lecturas_project.presentation.screens

import android.annotation.SuppressLint
import android.graphics.Paint
import androidx.compose.animation.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.R
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import androidx.room.Room
import com.danp.lecturas_project.LoginForm
import com.danp.lecturas_project.SignUpForm
import com.danp.lecturas_project.database.LecturasDataBase
import com.danp.lecturas_project.database.LecturasEntity
import com.danp.lecturas_project.datastore.Preferencias
import com.danp.lecturas_project.navigation.Destinations
import com.danp.lecturas_project.pager.MainViewModel
import com.danp.lecturas_project.ui.theme.Purple200
import com.danp.lecturas_project.ui.theme.Purple500
import com.danp.lecturas_project.ui.theme.Purple700
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Ranking(navController: NavHostController) {
    val tabItems = listOf("Ranking General")
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()


    Column {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = Purple500,
            modifier = Modifier
                .padding(5.dp)
                .background(Color.Transparent)
                .clip(RoundedCornerShape(30.dp)),
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier
                        .pagerTabIndicatorOffset(
                            pagerState, tabPositions
                        )
                        .width(0.dp)
                        .height(0.dp)
                )
            }
        ) {
            tabItems.forEachIndexed { index, title ->
                val color = remember { Animatable(Purple700) }
                LaunchedEffect(pagerState.currentPage == index) {
                    color.animateTo(
                        if (pagerState.currentPage == index)
                            Color.White else Purple500
                    )
                }

                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(
                            text = title,
                            style = if (pagerState.currentPage == index) {
                                TextStyle(
                                    color = Purple700,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Black
                                )
                            } else {
                                TextStyle(
                                    color = Color.White,
                                    fontSize = 15.sp,
                                )
                            }
                        )
                    },
                    modifier = Modifier.background(
                        color = color.value,
                        shape = RoundedCornerShape(30.dp)
                    )
                )
            }
        }

        HorizontalPager(
            count = tabItems.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .background(Purple200)
        ) { page ->
            if (page == 0){
                Column (
                    modifier = Modifier
                        //.fillMaxSize()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    RankingTotal()
                }

            }

        }
    }
}




@Composable
fun RankingTotal() {
    val viewModel = viewModel<MainViewModel>()
    val state = viewModel.state
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(state.items.size) { i ->
            val item = state.items[i]
            if (i >= state.items.size - 1 && !state.endReached && !state.isLoading) {
                viewModel.loadNextItems()
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, bottom = 40.dp)
            ) {

                Text(
                    text = item.titulo,
                    style = TextStyle(color = Color.Black, fontSize = 21.sp, fontWeight = FontWeight.Black)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row (
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(16.dp)
                ) {
                    Text(text = item.usuario,
                        modifier = Modifier.padding(end = 20.dp),
                        style = TextStyle(color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.Light)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(text = item.puntaje.toString(),
                        modifier = Modifier.padding(start = 20.dp),
                        style = TextStyle(color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.Light))
                }
            }
        }
        item {
            if (state.isLoading) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp, bottom = 40.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
