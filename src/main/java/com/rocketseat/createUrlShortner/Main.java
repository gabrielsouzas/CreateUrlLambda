package com.rocketseat.createUrlShortner;

/* Para lidar com requisições será utilizada a interface RequestHandler que é uma dependencia da AWS*/

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main implements RequestHandler<Map<String, Object>, Map<String, String>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /* Para estabelecer uma conexão com o S3 precisa desse cliente */
    private final S3Client s3Client = S3Client.builder().build();

    @Override
    public Map<String, String> handleRequest(Map<String, Object> input, Context context) {
        /* Esse input vai conter tudo que for passado na requisição */
        String body = input.get("body").toString();

        /* Formato que o input chega
        *
        *   input {
        *       body: "{\originalUrl\":\https://rocketseat.com.br\"}"
        *           headers: {...}
        * }
        *
        *  */

        /* Variavel do tipo Map*/
        Map<String, String> bodyMap;

        /* Para evitar que a aplicação quebre ao receber dados inconsistentes */
        try {
            /* Vai tentar transformar o body em um objeto Map, ou seja, um obj do tipo chave-valor */
            bodyMap = objectMapper.readValue(body, Map.class);
        } catch (Exception exception) {
            throw new RuntimeException("Error parsing JSON body" + exception.getMessage(), exception);
        }

        /* Pega a url original do body */
        String originalUrl = bodyMap.get("originalUrl");

        /* Pega a data de expiração do body */
        String expirationTime = bodyMap.get("expirationTime");
        long expirationTimeInSeconds = Long.parseLong(expirationTime) * 3600;

        String shortUrlCode = UUID.randomUUID().toString().substring(0,8);

        UrlData urlData = new UrlData(originalUrl, expirationTimeInSeconds);

        try {
            /* Converter para um objeto JSON */
            String urlDataJson = objectMapper.writeValueAsString(urlData);

            /* Criar a requisição para ser enviada ao S3 */
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket("url-shortener-storage-lambda")
                    .key(shortUrlCode + ".json")
                    .build();

            /* Enviar a requisição */
            s3Client.putObject(request, RequestBody.fromString(urlDataJson));

        } catch (Exception exception) {
            throw new RuntimeException("Error saving data to S3" + exception.getMessage(), exception);
        }

        Map<String, String> response = new HashMap<>();
        response.put("code", shortUrlCode);

        return response;
    }
}