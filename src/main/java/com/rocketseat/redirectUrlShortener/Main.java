package com.rocketseat.redirectUrlShortener;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /* Para estabelecer uma conexão com o S3 precisa desse cliente */
    private final S3Client s3Client = S3Client.builder().build();

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        // Pega o ID do caminho, ex: https://url-shortener-storage/ID
        String pathParameters = (String) input.get("rawPath");
        // Remove a barra do caminho
        String shortUrlCode = pathParameters.replace("/", "");

        if (shortUrlCode == null || shortUrlCode.isEmpty()){
            throw new IllegalArgumentException("Invalid input: 'shortUrlCode' is required");
        }

        // Monta o Get para a requisição que vai ser feita na função lambda que tem os cadastros das urls
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket("url-shortener-storage-lambda")
                .key(shortUrlCode + ".json")
                .build();

        // A variavel do tipo inputStream é necessária porque a resposta do S3 é enviada em forma de stream, que são
        // pequenos pacotes, que depois esses pacotes serão concatenados.
        InputStream s3ObjectStream;

        try {
            // Faz a requisição do get montado no stream
            s3ObjectStream = s3Client.getObject(getObjectRequest);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching data from S3" + e.getMessage(), e);
        }

        // Vai pegar a url de dentro da stream
        UrlData urlData;
        try {
            // Lê o valor que tem na string e transforma o json em um objeto java (desserialização)
            urlData = objectMapper.readValue(s3ObjectStream, UrlData.class);
        }  catch (Exception exception) {
            throw new RuntimeException("Error deserializing URL data" + exception.getMessage(), exception);
        }

        // Pega o timeStamp do dia de hoje em milisegundos e converte em segundos para comparar com o que foi salvo no S3
        long currentTimeInSeconds = System.currentTimeMillis() / 1000;

        Map<String, Object> response = new HashMap<>();

        if (currentTimeInSeconds > urlData.getExpirationTime()){
            // Em caso de URL expirada
            response.put("statusCode", 410);
            response.put("body", "This URL has expired.");
            return response;
        }

        /* Agora vai usar o redirecionamento do proprio browser com o status 302 para redirecionar a URL para o endereço correto a partir do ID */
        response.put("statusCode", 302);

        Map<String, String> headers = new HashMap<>();
        headers.put("Location", urlData.getOriginalUrl());
        response.put("headers", headers);

        return response;

    }
}