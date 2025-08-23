//package express.atc.backend.integration.cargoflow.config;
//
//import express.atc.backend.integration.cargoflow.service.impl.CargoflowAuthFilter;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.MediaType;
//import org.springframework.http.client.reactive.ReactorClientHttpConnector;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.netty.http.client.HttpClient;
//
//import javax.net.ssl.SSLException;
//import java.util.Collections;
//
//@Configuration
//@Slf4j
//@RequiredArgsConstructor
//public class CargoflowConfig {
//
//    @Value("${cargoflow.entity.endpoint}")
//    private String cargoflowEntityUrl;
//    @Value("${cargoflow.upload.endpoint}")
//    private String cargoflowUploadUrl;
//    @Value("${cargoflow.upload.attach}")
//    private String cargoflowAttachUrl;
//    @Value("${cargoflow.entity.orders}")
//    private String cargoflowListOrderUrl;
//
//    private final HttpClient httpClient;
//    private final CargoflowAuthFilter cargoflowAuthFilter;
//
//    @Bean("cargoflowEntityWebClient")
//    public WebClient cargoflowOrderWebClient() throws SSLException {
//        return WebClient.builder()
//                .clientConnector(new ReactorClientHttpConnector(httpClient))
//                .baseUrl(cargoflowEntityUrl)
//                .filter(cargoflowAuthFilter)
//                .defaultHeaders(httpHeaders -> {
//                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
//                    httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//                })
//                .build();
//    }
//
//    @Bean("cargoflowListOrderWebClient")
//    public WebClient cargoflowListOrderWebClient() throws SSLException {
//        return WebClient.builder()
//                .clientConnector(new ReactorClientHttpConnector(httpClient))
//                .baseUrl(cargoflowListOrderUrl)
//                .filter(cargoflowAuthFilter)
//                .defaultHeaders(httpHeaders -> {
//                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
//                    httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//                })
//                .build();
//    }
//
//    @Bean("cargoflowUploadWebClient")
//    public WebClient cargoflowUploadWebClient() throws SSLException {
//        return WebClient.builder()
//                .clientConnector(new ReactorClientHttpConnector(httpClient))
//                .baseUrl(cargoflowUploadUrl)
//                .filter(cargoflowAuthFilter)
//                .defaultHeaders(httpHeaders -> {
//                    httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//                    httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
//                })
//                .build();
//    }
//
//    @Bean("cargoflowAttachWebClient")
//    public WebClient cargoflowAttachWebClient() throws SSLException {
//        return WebClient.builder()
//                .clientConnector(new ReactorClientHttpConnector(httpClient))
//                .baseUrl(cargoflowAttachUrl)
//                .filter(cargoflowAuthFilter)
//                .defaultHeaders(httpHeaders -> {
//                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
//                    httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//                })
//                .build();
//    }
//
//}
