package di

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.navigator.Navigator
import com.liftric.kvault.KVault
import config.BaseUrlConfig
import configureForPlatform
import initStore
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.statement.HttpReceivePipeline
import io.ktor.client.statement.request
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.InternalAPI
import io.ktor.util.pipeline.PipelinePhase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.datetime.Clock
import org.koin.core.module.Module
import org.koin.dsl.module
import repository.EmptyHouseRepository
import repository.FeedbackRepository
import repository.LoginRepository
import repository.ManageRepository
import repository.ModifierInformationRepository
import repository.NewRepository
import repository.PersonRepository
import repository.PostRepository
import repository.ReportRepository
import repository.RibbonRepository
import repository.SplashRepository
import repository.WeatherRepository
import ui.compose.Action.ActionViewModel
import ui.compose.Authentication.AuthenticationViewModel
import ui.compose.EmptyHouse.EmptyHouseVoyagerViewModel
import ui.compose.Feedback.FeedBackViewModel
import ui.compose.Log.LogViewModel
import ui.compose.Manage.ManageViewModel
import ui.compose.ModifierInformation.ModifierInformationViewModel
import ui.compose.Person.PersonViewModel
import ui.compose.Post.PostDetailViewModel
import ui.compose.Post.PostListViewModel
import ui.compose.Release.ReleasePageViewModel
import ui.compose.Report.ReportViewModel
import ui.compose.SplashPage.SplashPageViewModel
import ui.compose.Weather.WeatherViewModel
import ui.root.RootAction
import ui.setting.Setting
import util.compose.Toast
import util.encode.encode
import viewModelDefinition
import kotlin.random.Random
import kotlin.random.nextInt

class LoginClient(
    val client : HttpClient = HttpClient{
        install(ContentNegotiation) {
            json()
        }
        install(
            DefaultRequest
        ){
            url(BaseUrlConfig.BaseUrl)
        }
        install(Logging)
        install(HttpCookies){}
        install(HttpRedirect) {
            checkHttpMethod = false
        }
        configure()
    }
)

class ShareClient(
    val client : HttpClient = HttpClient{
        install(ContentNegotiation) {
            json()
        }
        install(Logging)
        install(HttpRedirect) {
            checkHttpMethod = false
        }
        configure()
    }
)

class WebClient(
    val client : HttpClient = HttpClient{
        install(ContentNegotiation) {
            json()
        }
        install(Logging)
        install(HttpRedirect) {
            checkHttpMethod = false
        }
        configure()
    }
)

class TopBarState(){
    val itemForSelect = mutableStateOf<List<SelectItem>?>(null)
    val expanded = mutableStateOf(false)
    val itemForSelectShow = derivedStateOf {
        itemForSelect.value != null
    }
    fun registerItemForSelect(list:List<SelectItem>?){
        itemForSelect.value = list
    }
    val title = mutableStateOf<String?>(null)

}

data class SelectItem(
    val text : String,
    val click : ()->Unit,
)

data class BackItem(
    val label: String,
    val click: () -> Unit
)

class SystemAction(
    val onBack :() -> Unit,
    val onFinish: () -> Unit
)

@OptIn(InternalAPI::class, DelicateCoroutinesApi::class)
fun appModule(
    rootAction: RootAction,
    systemAction: SystemAction,
    navigator: Navigator,
) = module {
    single {
        rootAction
    }
    single {
        Setting(get())
    }
    single {
        systemAction
    }
    single {
        navigator
    }
    single {
        val client = HttpClient{
            install(ContentNegotiation) {
                json()
            }
            headers {
                val kVault = get<KVault>()
                val token : String? = kVault.string(forKey = "token")
                token?.let {
                    append("Authorization",token)
                }
            }
            install(
                DefaultRequest
            ){
                val kVault = get<KVault>()
                val token : String? = kVault.string(forKey = "token")
                token?.let {
                    headers.append("Authorization",token)
                }
                url(BaseUrlConfig.BaseUrl)
            }
            install(Logging){
                level = LogLevel.BODY
            }
            install(HttpCookies){}
            install(HttpRedirect) {
                checkHttpMethod = false
            }
            configure()
        }
        val encodePhase = PipelinePhase("Encode")
        client.requestPipeline.insertPhaseBefore(HttpRequestPipeline.Send,encodePhase)
        client.requestPipeline.intercept(encodePhase){
            val time = Clock.System.now().toEpochMilliseconds()/1000
            val randomNumber1 = Random.nextInt(10..99)
            val randomNumber2 = Random.nextInt(0..9)
            this.context.headers.append("Encode", "${randomNumber1}${randomNumber2}_${encode(randomNumber1,randomNumber2,time)}")
        }
        val authPhase = PipelinePhase("Auth")
        client.receivePipeline.insertPhaseBefore(HttpReceivePipeline.Before,authPhase)
        client.receivePipeline.intercept(authPhase){
            if(it.status.value == 555){
                val kVault = get<KVault>()
                kVault.clear()
                get<RootAction>().reLogin()
            }
            if(it.status.value == 556){
                val kVault = get<KVault>()
                kVault.clear()
                get<RootAction>().reLogin()
            }
            if(it.status.value == 557){
                get<RootAction>().popManage()
            }
        }
        if(BaseUrlConfig.isDebug){
            val LogRequest = PipelinePhase("LogRequest")
            client.receivePipeline.insertPhaseAfter(HttpReceivePipeline.After,LogRequest)
            client.receivePipeline.intercept(LogRequest){
                println("----------------------request---------------------------")
                println("request ${it.request.url} ${it.request.method}")
                it.request.headers.forEach { s, strings ->
                    println("header --> $s -> ${strings}")
                }
            }

            val LogResponse = PipelinePhase("LogResponse")
            client.receivePipeline.insertPhaseAfter(HttpReceivePipeline.After,LogResponse)
            client.receivePipeline.intercept(LogResponse){
                println("----------------------response---------------------------")
                println("request ${it.request.url} ${it.request.method}")
                it.headers.forEach { s, strings ->
                    println("header --> $s -> $strings")
                }
//                println(it.content.readByte().toString())
            }
        }
        return@single client
    }
    single {
        TopBarState()
    }
    repositoryList()
    viewModel()
    single {
        initStore()
    }
    single {
        val kVault = get<KVault>()
    }
    single {
        LoginClient()
    }
    single {
        ShareClient()
    }
    single {
        WebClient()
    }
    single {
        val scope = CoroutineScope(Job())
        return@single Toast(scope)
    }
}
fun Module.repositoryList(){
    single {
        SplashRepository( get() )
    }
    single {
        LoginRepository( get() )
    }
    single {
        NewRepository(get())
    }
    single {
        PersonRepository(get())
    }
    single {
        PostRepository(get())
    }
    single {
        FeedbackRepository(get())
    }
    single {
        ModifierInformationRepository(get())
    }
    single {
        WeatherRepository(get())
    }
    single {
        ReportRepository(get())
    }
    single {
        ManageRepository(get())
    }
    single {
        RibbonRepository(get())
    }
    single {
        EmptyHouseRepository(get())
    }
}
fun Module.viewModel(){
    viewModelDefinition {
        AuthenticationViewModel( get(),get(),get())
    }
    single {
        ActionViewModel( get(),get())
    }
    single {
        SplashPageViewModel(get(),get())
    }
    single {
        PostListViewModel(get(),get(),get(),get())
    }
    single {
        FeedBackViewModel(get())
    }
    single {
        ReportViewModel(get())
    }
    single {
        PostDetailViewModel(get(),get(),get())
    }
    viewModelDefinition {
        ManageViewModel(get(),get())
    }
    viewModelDefinition {
        PersonViewModel(get(),get())
    }
    viewModelDefinition {
        WeatherViewModel(get())
    }
    viewModelDefinition {
        ReleasePageViewModel(get())
    }
    viewModelDefinition {
        ModifierInformationViewModel(get())
    }
    viewModelDefinition {
        LogViewModel()
    }
    viewModelDefinition {
        EmptyHouseVoyagerViewModel(get())
    }
}
fun HttpClientConfig<*>.configure() {
    configureForPlatform()
}