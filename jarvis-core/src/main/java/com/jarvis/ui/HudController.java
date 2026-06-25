package com.jarvis.ui;

import com.jarvis.service.IaService;
import com.jarvis.service.SystemService;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class HudController {

    @FXML private TextArea txtConsola;
    @FXML private TextField txtEntrada;

    private IaService iaService;
    private SystemService systemService;

    @FXML
    public void initialize() {
        Dotenv dotenv = Dotenv.configure().directory("./").load();
        this.iaService = new IaService(dotenv);
        this.systemService = new SystemService();

        txtConsola.appendText("[SYSTEM] Subsistemas inicializados.\n");
        txtConsola.appendText("[SYSTEM] Interfaz de control táctica en línea...\n");
    }

    @FXML
    public void procesarEntradaManual() {
        String peticion = txtEntrada.getText().trim();
        if (peticion.isEmpty()) return;

        txtConsola.appendText("\n[CRISTIAN]: " + peticion + "\n");
        txtEntrada.clear();

        String peticionMinusculas = peticion.toLowerCase();

        // =================================================================
        // 1. INTERCEPCIÓN LOCAL (Velocidad Ultrarrápida - Bypass de IA)
        // =================================================================
        if (peticionMinusculas.contains("reproduce música") || peticionMinusculas.contains("abre spotify")) {
            txtConsola.appendText("[RODOLFO]: Abriendo reproductor multimedia inmediatamente, señor.\n");
            systemService.ejecutarComandoNativo("open -a Spotify"); // Comando nativo para Mac
            return; // Salimos de la función para NO usar la IA y ahorrar tiempo
        }
        else if (peticionMinusculas.contains("abre el navegador") || peticionMinusculas.contains("abre safari")) {
            txtConsola.appendText("[RODOLFO]: Iniciando enlace web...\n");
            systemService.ejecutarComandoNativo("open -a Safari");
            return;
        }
        else if (peticionMinusculas.contains("abre youtube")) {
            txtConsola.appendText("[RODOLFO]: Abriendo YouTube...\n");
            systemService.ejecutarComandoNativo("open https://www.youtube.com");
            return;
        }

        // =================================================================
        // 2. PROCESAMIENTO COGNITIVO (Llamada a Gemini para cosas complejas)
        // =================================================================
        new Thread(() -> {
            String respuesta = iaService.enviarConsulta(peticion);

            Platform.runLater(() -> {
                String respuestaLimpia = respuesta.trim();

                if (respuestaLimpia.startsWith("CMD:")) {
                    String comandoNativo = respuestaLimpia.replace("CMD:", "").trim();
                    txtConsola.appendText("[RODOLFO]: Ejecutando protocolo -> " + comandoNativo + "\n");
                    systemService.ejecutarComandoNativo(comandoNativo);
                } else {
                    txtConsola.appendText("[RODOLFO]: " + respuestaLimpia + "\n");
                }
            });
        }).start();
    }
}