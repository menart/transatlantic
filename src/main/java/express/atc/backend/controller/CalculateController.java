package express.atc.backend.controller;

import express.atc.backend.calculate.CalcCustomsFee;
import express.atc.backend.dto.CalculateDto;
import express.atc.backend.dto.ErrorResponseDto;
import express.atc.backend.dto.OrdersDto;
import express.atc.backend.exception.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@RequestMapping(path = "/api/calculate/", produces = "application/json")
@Tag(name = "Calculate controller", description = "Контроллер для расчета платежей")
public class CalculateController {

    private final CalcCustomsFee calcCustomsFee;

    @Operation(summary = "Расчет таможенных платежей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Расчет по таможенным платежам",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CalculateDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Невалидные параметры в запросе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
    })
    @PostMapping("customs-fee")
    public CalculateDto calcCustomersFee(@RequestBody OrdersDto orders) throws ApiException {
        return calcCustomsFee.calculate(orders);
    }
}
