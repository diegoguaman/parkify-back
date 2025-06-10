package com.igrowker.feature.parkify.features.parkingV2.dto.request;

import java.util.List;

import com.igrowker.feature.parkify.features.parkingV2.entities.AccessType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class ParkingRequestDTO {

    @Schema(description = "Nombre del parking", example = "Parking Palermo", required = true)
    @NotBlank(message = "El nombre del parking es obligatorio")
    private String parkingName;

    @Schema(description = "Dirección del parking", example = "Av. Libertador 1234", required = true)
    @NotBlank(message = "La dirección del parking es obligatoria")
    private String parkingAddress;

    @Schema(description = "Teléfono de contacto del parking", example = "1134567890", required = true)
    @NotBlank(message = "El teléfono del parking es obligatorio")
    private String parkingPhone;

    @Schema(description = "URL de imagen representativa", example = "https://example.com/image.jpg")
    private String imageUrl;

    @Schema(description = "Total de espacios disponibles en el parking", example = "20", required = true)
    @NotNull(message = "El número total de espacios es obligatorio")
    private Integer totalSpots;

    @Schema(description = "Cantidad de espacios actualmente disponibles", example = "15", required = true)
    @NotNull(message = "El número de espacios disponibles es obligatorio")
    private Integer availableSpots;

    @Schema(description = "Lista de características adicionales", example = "[\"techado\", \"vigilancia 24hs\"]")
    private List<String> extraFeatures;

    @Schema(description = "Promedio de calificaciones", example = "4.5")
    private Double ratingAvg;

    @Schema(description = "Cantidad de calificaciones recibidas", example = "25")
    private Integer ratingCount;

    @Schema(description = "Latitud de la ubicación", example = "-34.6037", required = true)
    @NotNull(message = "La latitud es obligatoria")
    private Double lat;

    @Schema(description = "Longitud de la ubicación", example = "-58.3816", required = true)
    @NotNull(message = "La longitud es obligatoria")
    private Double lng;

    @Schema(
        description = "Tipo de acceso al parking",
        example = "QR_CODE",
        allowableValues = {"QR_CODE", "PIN_CODE", "MANUAL_CONTACT", "OTHER"}
    )
    private AccessType accessType;

    @Schema(description = "Instrucciones adicionales de acceso al parking", example = "Llamar al portero al llegar")
    private String accessInstructions;
}
