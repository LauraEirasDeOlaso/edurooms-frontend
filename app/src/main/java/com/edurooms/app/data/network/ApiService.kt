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

    @GET(Constants.ENDPOINT_AULA_DETALLE)
    suspend fun obtenerAula(@Path("id") id: Int): Response<Aula>

    @GET("reservas/usuario/mis-reservas")
    suspend fun obtenerMisReservas(@Header("Authorization") token: String): Response<List<Reserva>>

    @POST("reservas")
    suspend fun crearReserva(
        @Header("Authorization") token: String,
        @Body request: CrearReservaRequest
    ): Response<CrearReservaResponse>

    @GET(Constants.ENDPOINT_INCIDENCIAS)
    suspend fun obtenerIncidencias(): Response<List<Incidencia>>

    @GET("incidencias/aula/{aula_id}")
    suspend fun obtenerIncidenciasAula(@Path("aula_id") aulaId: Int): Response<List<Incidencia>>

    // El endpoint requiere @Header("Authorization")
    @POST("incidencias")
    suspend fun crearIncidencia(
        @Header("Authorization") token: String,
        @Body request: CrearIncidenciaRequest
    ): Response<Incidencia>
}