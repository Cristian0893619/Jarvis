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
        // Dotenv ya no es estrictamente necesario para la API Key, pero lo mantenemos para estructura
        this.iaService = new IaService(null);
        this.systemService = new SystemService();

        txtConsola.appendText("[SYSTEM] Nucleo local Phi-3 conectado.\n");
        txtConsola.appendText("[SYSTEM] J.A.R.V.I.S. listo, Cristian.\n");
    }

    @FXML
    public void procesarEntradaManual() {
        String peticion = txtEntrada.getText().trim();
        if (peticion.isEmpty()) return;

        txtConsola.appendText("\n[CRISTIAN]: " + peticion + "\n");
        txtEntrada.clear();

        String peticionMinusculas = peticion.toLowerCase();

        // 1. INTERCEPCIÓN LOCAL (Ejecución instantánea)
        if (procesarComandosLocales(peticionMinusculas)) {
            return;
        }

        // 2. PROCESAMIENTO COGNITIVO (Phi-3 local)
        new Thread(() -> {
            String respuesta = iaService.enviarConsulta(peticion);

            Platform.runLater(() -> {
                if (respuesta.startsWith("CMD:")) {
                    String comando = respuesta.replace("CMD:", "").trim();
                    txtConsola.appendText("[RODOLFO]: Entendido. Ejecutando: " + comando + "\n");
                    systemService.ejecutarComandoNativo(comando);
                } else {
                    txtConsola.appendText("[RODOLFO]: " + respuesta.trim() + "\n");
                }
            });
        }).start();
    }

    private boolean procesarComandosLocales(String input) {
        if (input.contains("spotify") || input.contains("música")) {
            if (input.contains("reproduce") || input.contains("play")) {
                systemService.ejecutarComandoNativo("osascript -e 'tell application \"Spotify\" to play'");
                txtConsola.appendText("[RODOLFO]: Música activada.\n");
                return true;
            }
            if (input.contains("pausa")) {
                systemService.ejecutarComandoNativo("osascript -e 'tell application \"Spotify\" to pause'");
                txtConsola.appendText("[RODOLFO]: Pausado.\n");
                return true;
            }
        }
        return false;
    }
}