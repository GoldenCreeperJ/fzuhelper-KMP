package ui.compose.Manage


import ImagePickerFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import asImageBitmap
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import config.BaseUrlConfig
import dev.icerock.moko.resources.compose.painterResource
import getPlatformContext
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.flow.filter
import org.example.library.MR
import org.koin.compose.koinInject
import ui.root.TokeJump
import ui.setting.SettingTransitions
import util.compose.EasyToast
import util.compose.rememberToastState
import util.compose.toastBindNetworkResult
import util.network.CollectWithContent
import util.network.NetworkResult


class MangeRibbonVoyagerScreen():Screen{
    @Composable
    override fun Content() {
        Navigator(WorkWithExistingCarouselsVoyagerScreen()){ navigator ->
            Column {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ){
                    SettingTransitions(navigator = navigator)
                }
                BottomNavigation(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                ) {
                    BottomNavigationItem(
                        selected = navigator.lastItem is WorkWithExistingCarouselsVoyagerScreen,
                        icon = {

                        },
                        onClick = {
                            if (navigator.lastItem !is WorkWithExistingCarouselsVoyagerScreen){
                                navigator.replaceAll(WorkWithExistingCarouselsVoyagerScreen())
                            }
                        },
                        label = {
                            Text("管理已有轮播图")
                        }
                    )

                    BottomNavigationItem(
                        selected = navigator.lastItem is WorkWithExistingCarouselsVoyagerScreen,
                        icon = {

                        },
                        onClick = {
                            if (navigator.lastItem !is PostNewRibbonVoyagerScreen){
                                navigator.replaceAll(PostNewRibbonVoyagerScreen())
                            }
                        },
                        label = {
                            Text("管理已有轮播图")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RibbonImageShow(
    ribbonUrl: String,
    delete:()->Unit
){
    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(1f)
            .wrapContentHeight(),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
        ){
            KamelImage(
                resource = asyncPainterResource("${BaseUrlConfig.RibbonImage}/${ribbonUrl}"),
                modifier = Modifier
                    .aspectRatio(2f)
                    .fillMaxHeight(),
                contentDescription = "",
                contentScale = ContentScale.FillBounds
            )
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding( top = 10.dp )
            ) {
                Button(
                    onClick = {
                        delete.invoke()
                    }
                ) {
                    Text("删除该轮播图")
                }
            }
        }
    }
}

class WorkWithExistingCarouselsVoyagerScreen():Screen{
    @Composable
    override fun Content() {
        val manageViewModel = koinInject<ManageViewModel>()
        val toastState = rememberToastState()
        LaunchedEffect(Unit){
            manageViewModel.getRibbonData()
        }
        toastState.toastBindNetworkResult(manageViewModel.ribbonDelete.collectAsState())
        Box(modifier = Modifier.fillMaxSize()){
            Column (
                modifier = Modifier
                    .fillMaxSize()
            ){
                manageViewModel.ribbonList.collectAsState().CollectWithContent(
                    success = { ribbonData ->
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            items(ribbonData.size){ index ->
                                ribbonData[index].let { ribbonData ->
                                    RibbonImageShow(ribbonUrl = ribbonData.Image, delete = {
                                        manageViewModel.deleteRibbon(ribbonData.Image)
                                    })
                                }
                            }
                        }
                    },
                    loading = {
                        Box(modifier = Modifier.fillMaxSize()){
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    },
                    error = {
                        Box(modifier = Modifier.fillMaxSize()){
                            Text(modifier = Modifier.align(Alignment.Center).clickable {
                                manageViewModel.getRibbonData()
                            }, text = "加载失败")
                        }
                    }
                )
            }
            EasyToast(toastState)
        }
    }
}

class PostNewRibbonVoyagerScreen():Screen{
    @Composable
    override fun Content(){
        val manageViewModel = koinInject<ManageViewModel>()
        val imageByteArray = remember {
            mutableStateOf<ByteArray?>(null)
        }
        val imagePicker = ImagePickerFactory(context = getPlatformContext()).createPicker()
        imagePicker.registerPicker(
            onImagePicked = {
                imageByteArray.value = it
            }
        )
        Box(modifier = Modifier.fillMaxSize().padding(10.dp)){
            LaunchedEffect(Unit){
                manageViewModel.openImageAdd
                    .filter {
                        it is NetworkResult.Success<String>
                    }.collect{
                        imageByteArray.value = null
                    }
            }
            val toastState = rememberToastState()
            toastState.toastBindNetworkResult(manageViewModel.openImageDelete.collectAsState())
            manageViewModel.ribbonImageAdd.collectAsState().CollectWithContent(
                content = {
                    imageByteArray.value?: Icon(
                        painter = painterResource(MR.images.image),
                        "",
                        modifier = Modifier
                            .size(50.dp)
                            .clickable {
                                imagePicker.pickImage()
                            }
                    )
                    imageByteArray.value?.let {
                        Column (
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            Image(
                                bitmap = it.asImageBitmap(),
                                "",
                                modifier = Modifier
                                    .fillMaxWidth(1f)
                                    .aspectRatio(0.5f),
                                contentScale = ContentScale.FillBounds
                            )
                            Row (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(20.dp,Alignment.CenterHorizontally)
                            ){
                                Button(
                                    onClick = {
                                        imagePicker.pickImage()
                                    }
                                ){
                                    Text("重选")
                                }
                                val currentTokenJump = remember {
                                    mutableStateOf(TokeJump.Post)
                                }
                                val textForSend = remember{
                                    mutableStateOf("")
                                }
                                Column {
                                    TokeJump.values().forEach { tokenJump ->
                                        val isSelect = remember {
                                            derivedStateOf {
                                                currentTokenJump.value == tokenJump
                                            }
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier
                                                .wrapContentSize()
                                                .padding(10.dp)
                                        ){
                                            Checkbox(
                                                checked = isSelect.value,
                                                onCheckedChange = {
                                                    currentTokenJump.value = tokenJump
                                                },
                                                modifier = Modifier
                                                    .padding(end = 10.dp)
                                            )
                                            AnimatedVisibility(
                                                isSelect.value,
                                                modifier = Modifier
                                                    .weight(1f)
                                            ){
                                                TextField(
                                                    value = textForSend.value,
                                                    onValueChange = {
                                                        textForSend.value = it
                                                    },
                                                    isError = tokenJump.verifyFunction.invoke(textForSend.value)
                                                )
                                            }
                                        }
                                    }
                                }
                                Button(
                                    onClick = {
                                        if(imageByteArray.value == null){
                                            toastState.addWarnToast("轮播图不得为空")
                                            return@Button
                                        }
                                        if (currentTokenJump.value.verifyFunction(textForSend.value)){
                                            toastState.addWarnToast("url不符合规范")
                                        }
                                        imageByteArray.value?.let {
                                            manageViewModel.addRibbonImage(it,textForSend.value)
                                        }
                                    },
                                    modifier = Modifier,
                                    contentPadding = PaddingValues(horizontal = 40.dp)
                                ){
                                    Text("添加")
                                }
                            }
                        }
                    }
                },
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }
                }
            )
            EasyToast(toastState)
        }
    }
}