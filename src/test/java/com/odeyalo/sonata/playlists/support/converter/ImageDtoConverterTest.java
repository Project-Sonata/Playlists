package com.odeyalo.sonata.playlists.support.converter;

import com.odeyalo.sonata.playlists.dto.ImageDto;
import com.odeyalo.sonata.playlists.model.Image;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import testing.asserts.ImageDtoAssert;

@SpringBootTest(webEnvironment = WebEnvironment.NONE, classes = ImageDtoConverterImpl.class)
class ImageDtoConverterTest {

    @Autowired
    ImageDtoConverterImpl imageDtoConverter;

    @Test
    void toImageDto() {
        Image image = Image.builder()
                .url("https://cdn.sonata.com/i/uniqueimageid")
                .width(240)
                .height(200)
                .build();

        ImageDto imageDto = imageDtoConverter.toImageDto(image);

        ImageDtoAssert.fromImage(imageDto).height().isEqualTo(image.getHeight());
        ImageDtoAssert.fromImage(imageDto).width().isEqualTo(image.getWidth());
        ImageDtoAssert.fromImage(imageDto).url().isEqualTo(image.getUrl());
    }
}