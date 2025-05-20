package express.atc.backend.controller;

import express.atc.backend.service.TrackingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path = "/api/payment", produces = "plain/text")
@Tag(name = "Payment controller", description = "Контроллер для работы с Robokassa")
public class PaymentController {

    private final TrackingService trackingService;

    @GetMapping("ctrl")
    public String payment(@RequestParam("out_summ") String outSum,
                          @RequestParam("inv_id") long orderId,
                          @RequestParam("Shp_OrderNumber") String orderNumber,
                          @RequestParam("crc") String checkSum) {
        return trackingService.paymentControl(outSum, orderId, orderNumber, checkSum);
    }
}
