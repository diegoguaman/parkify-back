package com.igrowker.feature.parkify.features.content.service;

import com.igrowker.feature.parkify.features.content.config.FooterProperties;
import com.igrowker.feature.parkify.features.content.dto.FooterContentResponse;
import com.igrowker.feature.parkify.features.content.dto.SocialLinkDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

    private final FooterProperties footerProperties;

    @Override
    public FooterContentResponse getFooterData() {
        final List<SocialLinkDto> socialLinks = footerProperties.getSocial().entrySet().stream()
                .map(entry -> new SocialLinkDto(
                        entry.getKey(), entry.getValue().getUrl())
                )
                .toList();

        return new FooterContentResponse(
                footerProperties.getAboutUsLink(),
                footerProperties.getContactLink(),
                socialLinks
        );
    }
}
