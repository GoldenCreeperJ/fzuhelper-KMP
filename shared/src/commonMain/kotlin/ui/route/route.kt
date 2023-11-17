package ui.route

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ui.compose.Authentication.Assembly
import ui.compose.Feedback.FeedbackScreen
import ui.compose.Main.MainScreen
import ui.compose.Massage.MassageScreen
import ui.compose.New.NewsDetail
import ui.compose.PERSON.PersonScreen
import ui.compose.QRCode.QRCodeScreen
import ui.compose.Release.ReleasePageScreen
import ui.compose.SplashPage.SplashPage
import ui.util.compose.colorPicker.ColorPicker

@Composable
fun RouteHost(
    modifier: Modifier = Modifier,
    route:RouteState
){

    Crossfade(modifier = modifier, targetState = route.currentPage.value) {
        it?.content?.invoke()
    }
}

interface Route{
    val route: String
    val content : @Composable ( ) -> Unit
    class RouteNewsDetail(
        val id: String,
        override val route: String,
        override val content : @Composable () -> Unit = {
            NewsDetail()
        }
    ): Route

    class Main (
        val id: String ,
        override val route: String = "Main",
        override val content: @Composable (  ) -> Unit = {
            MainScreen()
        }
    ) : Route


    class ReleasePage (
        val token: String,
        override val route: String = "ReleasePage",
        override val content: @Composable (  ) -> Unit = {
            ReleasePageScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp)
            )
        }
    ) : Route

    class Person (
        val isSelf : Boolean = false,
        override val route: String,
        override val content: @Composable () -> Unit = {
           PersonScreen()
        }
    ) : Route

    class Massage private constructor(
        override val route: String,
        override val content: @Composable ( ) -> Unit = {
            MassageScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding( all = 10.dp )
            ) {

            }
        }
    ):Route

    class LoginWithRegister(
        override val route: String = "loginWithRegister",
        override val content: @Composable () -> Unit = {
            Assembly(Modifier.fillMaxSize())
        }
    ):Route

    class Splash(
        override val route: String = "Splash",
        override val content: @Composable () -> Unit = {
            SplashPage(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            )
        }
    ):Route

    class QRCode(
        override val route: String = "QRCode",
        override val content: @Composable () -> Unit = {
            QRCodeScreen(
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    ):Route

    class Feedback(
        override val route: String = "QRCode",
        override val content: @Composable () -> Unit = {
            FeedbackScreen(
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    ):Route

    class Test(
        override val route: String = "Test",
        override val content: @Composable () -> Unit = {
            ColorPicker()
        }
    ):Route
}

class RouteState(start:Route){
    private val route = mutableStateListOf<Route>(start)
    val currentPage = derivedStateOf {
        if(route.isEmpty()){
            return@derivedStateOf null
        }
        route.last()
    }
    val canBack = derivedStateOf {
        if(route.isEmpty()){
            return@derivedStateOf false
        }
        route.size != 1 && route.last() !is Route.LoginWithRegister
    }
    private val scope = CoroutineScope(Job())
    private val mutex = Mutex()
    fun back(){
        if(!mutex.isLocked){
            scope.launch(Dispatchers.Default) {
                mutex.withLock {
                    route.removeLast()
                }
            }
        }
    }
    fun navigateWithPop(newRoute: Route){
        route.removeLastOrNull()
        route.add(newRoute)
    }
    fun navigateWithoutPop(newRoute: Route){
        route.add(newRoute)
    }

    fun reLogin(){
        navigateWithoutPop(Route.LoginWithRegister())
    }
}








