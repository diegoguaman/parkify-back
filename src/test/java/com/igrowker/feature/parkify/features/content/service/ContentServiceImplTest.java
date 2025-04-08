package com.igrowker.feature.parkify.features.content.service;

import com.igrowker.feature.parkify.features.content.config.FooterProperties;
import com.igrowker.feature.parkify.features.content.dto.FooterContentResponse;
import com.igrowker.feature.parkify.features.content.dto.SocialLinkDto;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("ContentServiceImpl Unit Tests")
class ContentServiceImplTest {

    private static final String TEST_ABOUT_LINK = "/test-about";
    private static final String TEST_CONTACT_LINK = "mailto:test@example.com";
    private static final String TWITTER_PLATFORM = "x-twitter";
    private static final String TWITTER_URL = "https://test.x.com";
    private static final String INSTA_PLATFORM = "instagram";
    private static final String INSTA_URL = "https://test.instagram.com";

    @Nested
    @DisplayName("When getFooterData is called")
    class GetFooterDataTests {

        @Test
        @DisplayName("Should map properties to DTO correctly when social links exist")
        void shouldMapPropertiesToDtoCorrectly_whenSocialLinksExist() {
            final FooterProperties testProperties = getProperties();
            final ContentServiceImpl contentServiceImpl = new ContentServiceImpl(testProperties);
            final FooterContentResponse actualResponse = contentServiceImpl.getFooterData();

            assertAll(
                    () -> assertThat(actualResponse.getAboutUsLink()).isEqualTo(TEST_ABOUT_LINK),
                    () -> assertThat(actualResponse.getContactLink()).isEqualTo(TEST_CONTACT_LINK),
                    () -> assertThat(actualResponse.getSocialLinks())
                            .isNotNull()
                            .hasSize(2)
                            .containsExactlyInAnyOrder(
                                    new SocialLinkDto(TWITTER_PLATFORM, TWITTER_URL),
                                    new SocialLinkDto(INSTA_PLATFORM, INSTA_URL)
                            )
            );
        }

        private @NotNull FooterProperties getProperties() {
            final FooterProperties testProperties = new FooterProperties();
            final FooterProperties.SocialLinkProperties twitterProps
                    = new FooterProperties.SocialLinkProperties();
            final FooterProperties.SocialLinkProperties instaProps
                    = new FooterProperties.SocialLinkProperties();

            testProperties.setAboutUsLink(TEST_ABOUT_LINK);
            testProperties.setContactLink(TEST_CONTACT_LINK);
            twitterProps.setUrl(TWITTER_URL);
            instaProps.setUrl(INSTA_URL);
            testProperties.setSocial(Map.of(
                    TWITTER_PLATFORM, twitterProps,
                    INSTA_PLATFORM, instaProps
            ));
            return testProperties;
        }

        @Test
        @DisplayName("Should return empty social links list when social map is empty")
        void shouldReturnEmptySocialLinksList_whenSocialMapIsEmpty() {
            final FooterProperties emptySocialProperties = new FooterProperties();
            emptySocialProperties.setAboutUsLink(TEST_ABOUT_LINK);
            emptySocialProperties.setContactLink(TEST_CONTACT_LINK);
            emptySocialProperties.setSocial(Collections.emptyMap());

            final ContentServiceImpl serviceWithEmptySocial
                    = new ContentServiceImpl(emptySocialProperties);
            final FooterContentResponse actualResponse = serviceWithEmptySocial.getFooterData();

            assertAll(
                    () -> assertThat(actualResponse.getAboutUsLink()).isEqualTo(TEST_ABOUT_LINK),
                    () -> assertThat(actualResponse.getContactLink()).isEqualTo(TEST_CONTACT_LINK),
                    () -> assertThat(actualResponse.getSocialLinks())
                            .isNotNull()
                            .isEmpty()
            );
        }

        @Test
        @DisplayName("Should return empty social links list when social map is null (if possible)")
        void shouldReturnEmptySocialLinksList_whenSocialMapIsNull() {
            final FooterProperties nullSocialProperties = new FooterProperties();

            nullSocialProperties.setAboutUsLink(TEST_ABOUT_LINK);
            nullSocialProperties.setContactLink(TEST_CONTACT_LINK);
            nullSocialProperties.setSocial(null);

            final ContentServiceImpl serviceWithNullSocial = new ContentServiceImpl(nullSocialProperties);

            assertThrows(NullPointerException.class, serviceWithNullSocial::getFooterData,
                    "Expected NullPointerException when social map is null and accessed"
            );

        }
    }
}