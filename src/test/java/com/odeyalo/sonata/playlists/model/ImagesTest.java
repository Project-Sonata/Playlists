package com.odeyalo.sonata.playlists.model;

import org.junit.jupiter.api.Test;
import testing.asserts.ImagesAsserts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ImagesTest {

    @Test
    void testIsEmptyForNonEmptyCollectionAndExpectFalse() {
        Images images = Images.of(Image.urlOnly("https://cdn.sonata.com/i/mikuuu"));

        ImagesAsserts.forImages(images).isNotEmpty();
    }

    @Test
    void testIsEmptyForEmptyCollectionAndExpectTrue() {
        Images images = Images.empty();

        ImagesAsserts.forImages(images).isEmpty();
    }

    @Test
    void sizeWithOneElementAndExpectOneAsReturnValue() {
        Images images = Images.of(Image.urlOnly("https://cdn.sonata.com/i/mikuuu"));
        ImagesAsserts.forImages(images).size(1);
    }

    @Test
    void sizeForEmptyCollectionExpectZero() {
        Images images = Images.empty();
        ImagesAsserts.forImages(images).size(0);
    }

    @Test
    void containsElementAndExpectTrue() {
        Image image = Image.urlOnly("https://cdn.sonata.com/i/mikuuu");
        Images images = Images.of(image);

        ImagesAsserts.forImages(images).containsElement(image);
    }

    @Test
    void containsElementThatDoesNotExistAndExpectFalse() {
        Images images = Images.of(Image.urlOnly("https://cdn.sonata.com/i/mikuuu"));

        ImagesAsserts.forImages(images).doesNotContainElement(Image.urlOnly("https://notexisting.com/i/hello"));
    }

    @Test
    void getByExistingValueIndexAndExpectValueToBeReturned() {
        Image expected = Image.urlOnly("https://cdn.sonata.com/i/mikuuu");
        Images images = Images.of(expected);

        Image actual = images.get(0);

        assertThat(expected).isEqualTo(actual);
    }

    @Test
    void getByNotExistingValueIndexAndExpectException() {
        Image expected = Image.urlOnly("https://cdn.sonata.com/i/mikuuu");
        Images images = Images.of(expected);

        assertThatThrownBy(() -> images.get(10)).isExactlyInstanceOf(IndexOutOfBoundsException.class);

    }

    @Test
    void streamForImagesWithElementAndExpectNotNull() {
        Images images = Images.of(Image.urlOnly("https://cdn.sonata.com/i/mikuuu"));

        assertThat(images.stream()).isNotNull();
    }

    @Test
    void streamForEmptyImagesAndExpectNotNull() {
        Images images = Images.empty();

        assertThat(images.stream()).isNotNull();
    }

    @Test
    void iteratorForImagesWithElementAndExpectNotNull() {
        Images images = Images.of(Image.urlOnly("https://cdn.sonata.com/i/mikuuu"));

        assertThat(images.iterator()).isNotNull();
    }

    @Test
    void iteratorForEmptyImagesAndExpectNotNull() {
        Images images = Images.empty();

        assertThat(images.iterator()).isNotNull();
    }
}