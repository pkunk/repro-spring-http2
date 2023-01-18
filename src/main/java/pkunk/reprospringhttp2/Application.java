package pkunk.reprospringhttp2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;

@RestController
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    private WebClient.Builder webClientBuilder;

    @GetMapping(value = "/dto", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Dto> getDto() {
        return ResponseEntity.noContent().build();
    }

//    @GetMapping(value = "/dto", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Dto> getDto() {
//        return ResponseEntity.ok(new Dto());
//    }

    @GetMapping(value = "/test-ok", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Dto> testOk() {
        return getResponse(HttpProtocol.HTTP11);
    }

    @GetMapping(value = "/test-fail", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Dto> testFail() {
        return getResponse(HttpProtocol.H2C);
    }

    private ResponseEntity<Dto> getResponse(HttpProtocol protocol) {
        HttpClient httpClient = HttpClient.create()
                .protocol(protocol)
                .secure();

        var response = webClientBuilder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build().get()
                .uri("http://localhost:8080/dto")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Dto.class)
                .checkpoint()
                .block();

        return response == null
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(response);
    }
}

record Dto() {
}
