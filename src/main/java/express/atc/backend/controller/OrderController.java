package express.atc.backend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/order/", produces = "application/json")
@Tag(name = "Order controller", description = "Временная заглушка")
public class OrderController {

    @GetMapping
    public String hello(){
        return "Hello";
    }
}
