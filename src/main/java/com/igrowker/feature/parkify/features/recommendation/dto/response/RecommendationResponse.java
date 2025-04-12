package com.igrowker.feature.parkify.features.recommendation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class RecommendationResponse {
    private Long parkingId;
    private String name;
    private String address;

}
