package com.edurooms.app.data.network
import com.edurooms.app.data.models.*
import retrofit2.Response
import retrofit2.http.*
import com.edurooms.app.data.utils.Constants


interface ApiService {

    // Auth endpoints
    @POST(Constants.ENDPOINT_LOGIN)
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST(Constants.ENDPOINT_REGISTRO)
    suspend fun registro(@Body request: RegistroRequest): Response<LoginResponse>

    @GET(Constants.ENDPOINT_PERFIL)
    suspend fun obtenerPerfil(@Header("Authorization") token: String): Response<UsuarioData>

    // Aulas endpoints
    @GET(Constants.ENDPOINT_AULAS)
    suspend fun obtenerAulas(): Response<List<Aula>>

    @GET(Constants.ENDPOINT_AULAS)
    suspend fun obtenerAula(@Path("id") id: Int): Response<Aula>
}