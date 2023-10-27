package ui.compose.Ribbon

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Ribbon(
    modifier: Modifier
){
    Column(
        modifier = modifier,
    ) {
        Carousel(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f)
                .clip(RoundedCornerShape(10.dp))
        )
        LazyVerticalGrid(
            modifier = Modifier.padding(top = 10.dp).weight(1f).fillMaxWidth().clip(RoundedCornerShape(10.dp)),
            columns = GridCells.Fixed(5)
        ){
            items(100){
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.7f)
                        .padding(10.dp)
                ){
                    KamelImage(
                        resource = asyncPainterResource("https://pic1.zhimg.com/v2-fddbd21f1206bcf7817ddec207ad2340_b.jpg"),
                        null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .wrapContentSize(Alignment.Center)
                            .fillMaxSize(0.7f)
                            .clip(RoundedCornerShape(10)),
                        contentScale = ContentScale.FillBounds
                    )
                    Text(
                        "sssssssssssssss",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Carousel(
    modifier: Modifier = Modifier,
    refreshCarousel:()->Unit = {},
) {

    val pageState = rememberPagerState(
        initialPage = 0,
    ) {
        10
    }

    val coroutineScope= rememberCoroutineScope()

    LaunchedEffect(pageState.currentPage){
        while (true){
            delay(4000)
            coroutineScope.launch {
                pageState.animateScrollToPage((pageState.currentPage+1)%10)
            }
        }
    }
    HorizontalPager(
        state = pageState,
        modifier = modifier.background(Color.Blue)
    ){
        KamelImage(
            resource = asyncPainterResource("https://pic1.zhimg.com/v2-fddbd21f1206bcf7817ddec207ad2340_b.jpg"),
            null,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}