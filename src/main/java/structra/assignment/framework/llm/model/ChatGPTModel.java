/*
 * MIT License
 *
 * Copyright (c) 2024 Riko Torun, Artur Rausch, Lennart Köhler, Moritz Wiedemann, Tim Stöcker, Souren Ishkhanian
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package structra.assignment.framework.llm.model;

import com.google.gson.*;
import lombok.Getter;
import lombok.NonNull;
import structra.assignment.framework.llm.MachineLearningModel;
import structra.assignment.framework.llm.KeyProvider;
import structra.assignment.framework.llm.context.LLMRole;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * The {@code ChatGPTModel} class generates natural language responses based on user input. It
 * implements the {@link MachineLearningModel} interface, where the input is a user query in the
 * form of a {@link String}, and the output is the generated response, also in the form of a {@link
 * String}.
 *
 * <p>The JSON structure used by the OpenAI API for chat completions is as follows:
 *
 * <pre>{@code
 * "model": "gpt-3.5-turbo",
 * "messages": [
 *   {
 *     "role": "system",
 *     "content": "You are a quiz assistant" // this is where additional systemContext is
 *   },
 *   {
 *     "role": "user",
 *     "content": "What is OpenAI?" // this is where the prompt is
 *   },
 *   {
 *       // more requests (chat history)
 *   }
 * ]
 * }</pre>
 *
 * @author Lennart Köhler
 * @author Riko Torun
 * @author Moritz Wiedemann
 * @see KeyProvider
 * @see MachineLearningModel
 */
public class ChatGPTModel implements MachineLearningModel {

    private static final String ENDPOINT = "https://api.openai.com/v1/chat/completions";

    private String systemContext;
    private final ModelVersion modelVersion;
    private final Temperature temperature;
    private final Collection<JsonObject> chatHistory;
    private final KeyProvider keyProvider;

    /** Initializes a default ChatGPT model instance */
    public ChatGPTModel(KeyProvider keyProvider) {
        this(keyProvider, ModelVersion.MODEL_VERSION_3_5_TURBO, Temperature.LOW);
    }

    /**
     * Creates a new ChatGPT instance with the specified key provider, model version and
     * temperature.
     *
     * @param keyProvider The key provider for the API
     * @param modelVersion The version of the model to use
     * @param temperature The temperature setting for the model.
     */
    public ChatGPTModel(
            KeyProvider keyProvider, ModelVersion modelVersion, Temperature temperature) {

        if (keyProvider == null) {
            throw new IllegalArgumentException("keyProvider cannot be null");
        }

        if (modelVersion == null) {
            throw new IllegalArgumentException("modelVersion cannot be null");
        }

        if (temperature == null) {
            throw new IllegalArgumentException("temperature cannot be null");
        }

        this.keyProvider = keyProvider;
        this.systemContext = "";
        this.modelVersion = modelVersion;
        this.temperature = temperature;
        this.chatHistory = new ArrayList<>();
    }

    @Override
    public void setContext(String systemContext) {
        this.systemContext = systemContext;
    }

    /**
     * Initiates the generation process in a background thread.
     *
     * @param prompt The input prompt to be sent to the ChatGPT API.}
     */
    @Override
    public CompletableFuture<String> execute(String prompt) {
        CompletableFuture<String> future = new CompletableFuture<>();
        RequestRunner runner = new RequestRunner(prompt, future);
        CompletableFuture.runAsync(runner);
        return future;
    }

    /**
     * This class is necessary in order to avoid sharing ChatGPTModel.resultListener among multiple
     * threads (otherwise not thread). It performs the actual request execution process.
     */
    private class RequestRunner implements Runnable {

        private final String prompt;
        private final CompletableFuture<String> future;

        private RequestRunner(String prompt, CompletableFuture<String> future) {
            this.prompt = prompt;
            this.future = future;
        }

        @Override
        public void run() {
            try {
                synchronized (chatHistory) {
                    chatHistory.add(createJSONEntry(LLMRole.USER, prompt));
                }

                HttpsURLConnection connection = openConnection();
                connection.setConnectTimeout(5000);

                String jsonRequestBody = createJsonRequestBody();
                writeMessageToModel(connection, jsonRequestBody);
                String response = getModelResponse(connection);
                System.out.println(response);
                future.complete(response);

                synchronized (chatHistory) {
                    chatHistory.add(createJSONEntry(LLMRole.ASSISTANT, response));
                }
            } catch (IOException | JsonIOException e) {
                future.completeExceptionally(e);
            }
        }

        /**
         * Opens a connection to the AI model server.
         *
         * @return The URLConnection object.
         * @throws IOException If an I/O error occurs.
         */
        @NonNull
        private HttpsURLConnection openConnection() throws IOException {
            URL obj = URI.create(ENDPOINT).toURL();
            HttpsURLConnection connection = (HttpsURLConnection) obj.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + keyProvider.getApiKey());
            connection.setRequestProperty("Content-Type", "application/json");
            return connection;
        }

        /**
         * Writes the formatted message to the model.
         *
         * @param connection The URLConnection object.
         * @param message The formatted message to be sent.
         * @throws IOException If an I/O error occurs.
         */
        private void writeMessageToModel(@NonNull URLConnection connection, String message)
                throws IOException {
            connection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(message);
            writer.flush();
            writer.close();
        }

        /**
         * This method constructs a JSON object containing:
         *
         * <ul>
         *   <li>The model version to be used
         *   <li>An array of messages (system context if available, and user prompt)
         *   <li>The temperature setting for response generation
         * </ul>
         *
         * @return A string representation of the JSON request body.
         */
        @NonNull
        private String createJsonRequestBody() throws JsonIOException, JsonSyntaxException {
            return new Gson()
                    .toJson(
                            Map.of(
                                    LLMRole.MODEL.toString(),
                                    modelVersion.version,
                                    "messages",
                                    createMessageRequestBody(),
                                    "temperature",
                                    temperature.value));
        }

        @NonNull
        private JsonArray createMessageRequestBody() throws JsonIOException {
            JsonArray message = new JsonArray();

            // add the context
            message.add(createJSONEntry(LLMRole.SYSTEM, systemContext));

            // put history (including current prompt) in the message body
            if (!chatHistory.isEmpty()) {
                synchronized (chatHistory) {
                    for (JsonObject chatMessage : chatHistory) {
                        message.add(chatMessage);
                    }
                }
            }
            return message;
        }

        /**
         * Waits for the response from the AI model.
         *
         * @param connection The URLConnection object.
         * @return The response from the model as a string.
         * @throws IOException If an I/O error occurs.
         */
        @NonNull
        private String getModelResponse(@NonNull URLConnection connection) throws IOException {
            try {
                JsonObject firstChoice = getFirstChoice(connection);
                JsonObject message = firstChoice.getAsJsonObject("message");

                return message.get("content").getAsString();
            } catch (FileNotFoundException e) {
                throw new FileNotFoundException("The API endpoint was not found: " + ENDPOINT);
            } catch (JsonIOException e) {
                throw new RuntimeException("Error parsing JSON response: " + e.getMessage());
            }
        }

        private JsonObject getFirstChoice(@NonNull URLConnection connection)
                throws IOException, JsonIOException {
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonArray choices = jsonResponse.getAsJsonArray("choices");
            return choices.get(0).getAsJsonObject();
        }

        private JsonObject createJSONEntry(LLMRole role, String message) throws JsonIOException {
            JsonObject object = new JsonObject();
            object.addProperty("role", role.toString());
            object.addProperty("content", message);
            return object;
        }
    }

    /** An enum representing the temperature setting for response generation. */
    @Getter
    public enum Temperature {
        LOW(0.2), // More deterministic, less creative output
        MEDIUM(0.5), // Balances coherence and creativity
        HIGH(0.8), // More random and creative outputs
        VERY_HIGH(1); // Most random and creative outputs

        public final double value;

        Temperature(double value) {
            this.value = value;
        }
    }

    /** An enum representing the model version to be used for response generation. */
    @Getter
    public enum ModelVersion {
        MODEL_VERSION_3_5_TURBO("gpt-3.5-turbo"),
        MODEL_VERSION_4O("gpt-4o");

        private final String version;

        ModelVersion(String version) {
            this.version = version;
        }
    }
}
