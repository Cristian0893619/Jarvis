package com.jarvis.service;

import com.google.gson.Gson;
import com.jarvis.model.GeminiModel;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.*;

import java.io.IOException;

public class IaService {
    private final OkHttpClient httpClient;
    private final Gson gson;
    private final String apiKey;

    // URL estable garantizada a la versión más reciente
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    public IaService(Dotenv dotenv) {
        this.httpClient = new OkHttpClient();
        this.gson = new Gson();

        // El .trim() elimina saltos de línea y espacios invisibles accidentales
        String rawKey = dotenv.get("GEMINI_API_KEY");
        this.apiKey = (rawKey != null) ? rawKey.trim() : "";
    }

    public String enviarConsulta(String mensajeUsuario) {
        if (apiKey.isEmpty() || apiKey.equals("tu_api_key_aqui_de_gemini")) {
            return "[CEREBRO] Error crítico: API Key no configurada en el archivo .env";
        }

        // INGENIERÍA DE PROMPTS: Le damos personalidad e instrucciones de sistema operativo
        String promptSistema = "Eres el asistente virtual J.A.R.V.I.S. Mi nombre es Cristian y el tuyo es Rodolfo. " +
                "Responde de forma profesional, eficiente y ligeramente sarcástica, al estilo Tony Stark. " +
                "IMPORTANTE: Si te pido abrir una aplicación, buscar algo en internet o ejecutar una acción en la PC, " +
                "DEBES responder ÚNICAMENTE con el comando de terminal para macOS prefijado con 'CMD:'. " +
                "Ejemplo 1: Si pido abrir el bloc de notas, responde: CMD:open -a TextEdit " +
                "Ejemplo 2: Si pido buscar en google, responde: CMD:open https://google.com " +
                "Si es una conversación normal, responde normalmente.\n\n" +
                "Cristian dice: " + mensajeUsuario;

        GeminiModel.Request requestBodyObj = new GeminiModel.Request(promptSistema);
        String jsonBody = gson.toJson(requestBodyObj);

        RequestBody body = RequestBody.create(
                jsonBody,
                MediaType.parse("application/json; charset=utf-8")
        );

        String urlCompleta = GEMINI_URL + "?key=" + apiKey;

        Request request = new Request.Builder()
                .url(urlCompleta)
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            // 1. Manejo específico del límite de peticiones (Error 429)
            if (response.code() == 429) {
                return "[CEREBRO] Múltiples peticiones detectadas. Los servidores de Google me piden esperar unos segundos. Dame un respiro, señor.";
            }

            // 2. Manejo de errores de autenticación o modelo (Error 400, 403, 404)
            if (!response.isSuccessful()) {
                return "[CEREBRO] Error de conexión (" + response.code() + "). Verifica tu API Key o conexión a internet.";
            }

            if (response.body() == null) {
                return "[CEREBRO] Respuesta del servidor vacía.";
            }

            String jsonResponse = response.body().string();
            GeminiModel.Response respuestaIa = gson.fromJson(jsonResponse, GeminiModel.Response.class);

            return respuestaIa.getResponseText();

        } catch (IOException e) {
            return "[CEREBRO] Error de red interno: " + e.getMessage();
        }
    }
}