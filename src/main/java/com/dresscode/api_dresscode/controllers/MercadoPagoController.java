package com.dresscode.api_dresscode.controllers;

import com.dresscode.api_dresscode.dtos.OrdenDeCompraDTO;
import com.dresscode.api_dresscode.entities.DetalleOrden;
import com.dresscode.api_dresscode.entities.OrdenDeCompra;
import com.dresscode.api_dresscode.entities.Producto;
import com.dresscode.api_dresscode.entities.ProductoTalle;
import com.dresscode.api_dresscode.services.OrdenDeCompraService;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.*;
import com.mercadopago.net.HttpStatus;
import com.mercadopago.resources.preference.Preference;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/mercado-pago")
@RequiredArgsConstructor
@CrossOrigin("*")
public class MercadoPagoController {
    private final OrdenDeCompraService ordenDeCompraService;

    @Value("${mercadopago.access-token}")
    private String mercadoPagoAccessToken;

    @PostMapping("/mp")
    public ResponseEntity<String> mp(@RequestBody OrdenDeCompraDTO ordenDeCompraDTO) throws Exception {
        MercadoPagoConfig.setAccessToken(mercadoPagoAccessToken);
        List<PreferenceItemRequest> items = new ArrayList<>();

        OrdenDeCompra ordenDeCompra= ordenDeCompraService.crearOrdenConDetalles(ordenDeCompraDTO);

        for (DetalleOrden detalle : ordenDeCompra.getDetalles()) {
            ProductoTalle productoTalle = detalle.getProductoTalle();
            var producto = productoTalle.getProducto();
            Double precioFinal = detalle.getPrecioUnitario();
            PreferenceItemRequest item = PreferenceItemRequest.builder()
                    .id(producto.getId().toString())
                    .title(producto.getNombre())
                    .description(producto.getDescripcion())
                    .quantity(detalle.getCantidad())
                    .currencyId("ARS")
                    .unitPrice(BigDecimal.valueOf(precioFinal))
                    .build();
            items.add(item);
        }
        PreferenceBackUrlsRequest backUrls =
                PreferenceBackUrlsRequest.builder()
                        .success("https://www.seu-site/success")
                        .pending("https://www.seu-site/pending")
                        .failure("https://www.seu-site/failure")
                        .build();

        List<PreferencePaymentTypeRequest> excludedPaymentTypes = new ArrayList<>();
        excludedPaymentTypes.add(PreferencePaymentTypeRequest.builder().id("ticket").build());


        PreferencePaymentMethodsRequest paymentMethods = PreferencePaymentMethodsRequest.builder()
                .excludedPaymentTypes(excludedPaymentTypes)
                .installments(1)
                .build();
        //el campo installments(1) se refiere a la cantidad máxima de cuotas permitidas para realizar el pago

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(items)
                .backUrls(backUrls)
                .paymentMethods(paymentMethods)
                .autoReturn("approved")
                .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);


        String prefId = preference.getId();

        System.out.println("URL de pago: " + preference.getInitPoint());

        return ResponseEntity.status(HttpStatus.OK).body("{\"preferenceId\":\""+prefId+"\"}");

    }
}
