package ui.compose.Massage

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.flow.map

@Composable
fun MassageDetail(
    modifier: Modifier = Modifier
){
    val listState = rememberLazyListState()
//    val preScrollStartOffset by remember { mutableStateOf(listState.firstVisibleItemScrollOffset) }
//    val preItemIndex by remember { mutableStateOf(listState.firstVisibleItemIndex) }
//    val isScrollDown = if (listState.firstVisibleItemIndex > preItemIndex) {
//        //第一个可见item的index大于开始滚动时第一个可见item的index，说明往下滚动了
//        true
//    } else if (listState.firstVisibleItemIndex < preItemIndex) {
//        //第一个可见item的index小于开始滚动时第一个可见item的index，说明往上滚动了
//        false
//    } else {
//        //第一个可见item的index等于开始滚动时第一个可见item的index,对比item offset
//        listState.firstVisibleItemScrollOffset > preScrollStartOffset
//    }

    val isShowTopBar = remember {
        mutableStateOf(true)
    }
    val first  = remember {
        mutableStateOf(1)
    }
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex  }
            .map {
                println(it.toString()+" "+first.value)
                it
            }.collect {
                val data = first.value
                if(data!=it){
                    first.value = it
                }
                isShowTopBar.value = data > it
            }
    }

    Box(modifier){
        Column {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                state = listState,
            ) {
                item {
                    Spacer(modifier = Modifier.height(50.dp))
                }
                items(30) {
                    MassageDetailItem(
                        modifier = Modifier
                            .padding(bottom = 30.dp)
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .animateContentSize()
                    ) {
                        TextWithLink()
                    }
                }

            }
            TextField(
                value = "",
                onValueChange = {},
                label = {
                    Text("回复")
                },
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .height(56.dp)
            )
        }
        AnimatedVisibility(
            visible = isShowTopBar.value,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enter = slideInVertically(),
        ) {
            TopAppBar(
                modifier
            ) {
                Text("FuTALK")
            }
        }
    }
}

@Composable
fun MassageDetailItem(
    modifier: Modifier = Modifier,
    itemContent : @Composable BoxScope.() -> Unit = {}
){
    Row (
        modifier = modifier
    ) {
        KamelImage(
            resource = asyncPainterResource("https://pic1.zhimg.com/v2-fddbd21f1206bcf7817ddec207ad2340_b.jpg"),
            null,
            modifier = Modifier
                .height(50.dp)
                .aspectRatio(1f)
                .clip(CircleShape),
            contentScale = ContentScale.FillBounds
        )
        Box (
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp)
                .wrapContentHeight()
                .clip(RoundedCornerShape(0.dp,10.dp,0.dp,10.dp))
                .background(MaterialTheme.colors.surface)
                .padding(10.dp)
        ){
            itemContent()
        }
    }
}

@Composable
fun TextWithLink(
    modifier: Modifier = Modifier
){
    Column (
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {
        Text("sssssssssssssssssssssssssssssssssssssssssssssssss")
        FloatingActionButton(
            onClick = {},
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                "https://github.com/",
                modifier = Modifier
                    .padding(10.dp)
            )
        }
        Text(
            "2023.10.1",
            modifier = Modifier.padding( top = 10.dp ),
            fontSize = 10.sp
        )
        Text(
            "In FuZhou",
            modifier = Modifier.padding( ),
            fontSize = 10.sp
        )
    }
}