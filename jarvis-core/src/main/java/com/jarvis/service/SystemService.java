package com.jarvis.service;

import java.awt.Robot;
import java.io.IOException;

public class SystemService {
    private Robot robot;

    public SystemService() {
        try {
            this.robot = new Robot();
        } catch (Exception e) {
            System.err.println("[SYSTEM] Error al inicializar subsistema Robot: " + e.getMessage());
        }
    }

    public void ejecutarComandoNativo(String comando) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                processBuilder.command("cmd.exe", "/c", comando);
            } else {
                processBuilder.command("bash", "-c", comando);
            }

            processBuilder.redirectErrorStream(true);
            processBuilder.start();
        } catch (IOException e) {
            System.err.println("[SYSTEM] Error ejecutando comando: " + e.getMessage());
        }
    }
}