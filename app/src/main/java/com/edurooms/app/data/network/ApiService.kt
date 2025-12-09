package com.edurooms.app.data.network
import com.edurooms.app.data.models.*
import retrofit2.Response
import retrofit2.http.*
import com.edurooms.app.data.utils.Constants


interface ApiService {

    // Auth endpoints
    @POST(Constants.ENDPOINT_LOGIN)
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET(Constants.ENDPOINT_PERFIL)
    suspend fun obtenerPerfil(@Header("Authorization") token: String
    ): Response<PerfílResponse>

    // Aulas endpoints
    @GET(Constants.ENDPOINT_AULAS)
    suspend fun obtenerAulas(): Response<List<Aula>>

    @GET(Constants.ENDPOINT_AULA_DETALLE)
    suspend fun obtenerAula(@Path("id") id: Int): Response<Aula>

    @GET("reservas/usuario/mis-reservas")
    suspend fun obtenerMisReservas(@Header("Authorization") token: String
    ): Response<List<Reserva>>

    @POST("reservas")
    suspend fun crearReserva(
        @Header("Authorization") token: String,
        @Body request: CrearReservaRequest
    ): Response<CrearReservaResponse>

    @GET("reservas/{id}")
    suspend fun obtenerReservaPorId(
        @Path("id") id: Int
    ): Response<Reserva>

    @DELETE("reservas/{id}")
    suspend fun cancelarReserva(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Map<String, String>>

    @GET("reservas/disponibilidad")
    suspend fun obtenerHorariosDisponibles(
        @Query("aula_id") aulaId: Int,
        @Query("fecha") fecha: String
    ): Response<DisponibilidadResponse>

    @GET(Constants.ENDPOINT_INCIDENCIAS)
    suspend fun obtenerIncidencias(): Response<List<Incidencia>>

    @GET("incidencias/aula/{aula_id}")
    suspend fun obtenerIncidenciasAula(@Path("aula_id") aulaId: Int
    ): Response<List<Incidencia>>

    @POST("incidencias")
    suspend fun crearIncidencia(
        @Header("Authorization") token: String,
        @Body request: CrearIncidenciaRequest
    ): Response<Incidencia>

    // Usuarios endpoints (Admin)
    @POST("usuarios")
    suspend fun crearUsuario(
        @Header("Authorization") token: String,
        @Body request: CrearUsuarioRequest
    ): Response<CrearUsuarioResponse>
    @GET("usuarios")
    suspend fun obtenerUsuarios(
        @Header("Authorization") token: String
    ): Response<ObtenerUsuariosResponse>

    @GET("usuarios/{id}")
    suspend fun obtenerUsuarioPorId(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Map<String, Any>>

    @GET("usuarios/existe/{email}")
    suspend fun validarEmailExiste(@Path("email") email: String
    ): Response<EmailValidationResponse>

    @PUT("usuarios/{id}")
    suspend fun editarUsuario(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body datos: Map<String, String>
    ): Response<UsuarioData>

    @DELETE("usuarios/{id}")
    suspend fun eliminarUsuario(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Map<String, String>>

    @PUT("usuarios/{id}/cambiar-password")
    suspend fun cambiarPassword(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body datos: Map<String, String>
    ): Response<CambiarPasswordResponse>
}
