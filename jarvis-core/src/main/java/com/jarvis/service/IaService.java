package com.jarvis.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class IaService {
    private final OkHttpClient httpClient;
    private final Gson gson;

    // SOLUCIÓN AL TIMEOUT: Usamos 127.0.0.1 en lugar de localhost para evitar el bloqueo de IPv6
    private static final String OLLAMA_URL = "http://127.0.0.1:11434/api/generate";

    public IaService(Object ignored) {
        // SOLUCIÓN: Subimos los tiempos a 5 minutos (300 segundos) para darle
        // tiempo al procesador de tu Mac de cargar el modelo en la RAM.
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    public String enviarConsulta(String mensajeUsuario) {
        // 1. Definimos la personalidad e instrucciones
        String promptSistema = "Eres el asistente virtual J.A.R.V.I.S. Mi nombre es Cristian y el tuyo es Rodolfo. " +
                "Responde de forma profesional, eficiente y ligeramente sarcástica, al estilo Tony Stark. " +
                "Si te pido abrir una aplicación, DEBES responder ÚNICAMENTE con el comando de terminal para macOS prefijado con 'CMD:'. " +
                "Ejemplo: CMD:open -a TextEdit\n\n" +
                "Cristian dice: " + mensajeUsuario;

        // 2. AQUÍ VA EL BLOQUE QUE PREGUNTASTE: Empaquetamos los datos para Phi-3
        Map<String, Object> requestMap = new HashMap<>();
        // Reemplazamos phi3 por el modelo ultraligero
        requestMap.put("model", "tinyllama");
        requestMap.put("prompt", promptSistema);
        requestMap.put("stream", false);

        // 3. Convertimos el paquete a formato JSON
        String jsonBody = gson.toJson(requestMap);

        RequestBody body = RequestBody.create(
                jsonBody,
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(OLLAMA_URL)
                .post(body)
                .build();

        // 4. Enviamos la petición y esperamos la respuesta
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return "[CEREBRO] Error de conexión (" + response.code() + "). Verifica que la terminal esté corriendo 'ollama run phi3'.";
            }

            if (response.body() == null) {
                return "[CEREBRO] Respuesta del núcleo vacía.";
            }

            String jsonResponse = response.body().string();

            // 5. Extraemos el texto de la respuesta de Ollama
            JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
            if (jsonObject.has("response")) {
                return jsonObject.get("response").getAsString();
            } else {
                return "[CEREBRO] Falla en decodificación de matriz cognitiva.";
            }

        } catch (IOException e) {
            return "[CEREBRO] Enlace fallido: " + e.getMessage();
        }
    }
}