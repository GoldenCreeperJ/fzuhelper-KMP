package ui.compose.Authentication

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Assembly(
    modifier: Modifier
){
    val pageState = rememberPagerState {
        2
    }
    val scope = rememberCoroutineScope()
    HorizontalPager(
        state = pageState,
        modifier = modifier,
        userScrollEnabled = false
    ){
        when(it){
            0->{
                Login(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)

                ) {
                    scope.launch {
                        pageState.animateScrollToPage(1)
                    }
                }
            }
            1->{
                Register(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                ) {
                    scope.launch {
                        pageState.animateScrollToPage(0)
                    }
                }
            }
        }
    }
}