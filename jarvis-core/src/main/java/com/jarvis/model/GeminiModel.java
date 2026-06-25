package com.jarvis.model;

import java.util.List;

public class GeminiModel {

    // --- Estructura para enviar la petición (Request) ---
    public static class Request {
        public List<Content> contents;

        public Request(String text) {
            this.contents = List.of(new Content(text));
        }
    }

    public static class Content {
        public List<Part> parts;

        public Content(String text) {
            this.parts = List.of(new Part(text));
        }
    }

    public static class Part {
        public String text;

        public Part(String text) {
            this.text = text;
        }
    }

    // --- Estructura para recibir la respuesta (Response) ---
    public static class Response {
        public List<Candidate> candidates;

        public String getResponseText() {
            if (candidates != null && !candidates.isEmpty() &&
                    candidates.get(0).content != null &&
                    candidates.get(0).content.parts != null &&
                    !candidates.get(0).content.parts.isEmpty()) {
                return candidates.get(0).content.parts.get(0).text;
            }
            return "No se recibió una respuesta clara del cerebro.";
        }
    }

    public static class Candidate {
        public Content content;
    }
}