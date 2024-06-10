package testing.spring.web;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.util.LinkedMultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;

public final class FilePartStub implements FilePart {
    private final Flux<DataBuffer> content;
    private long length;
    private String filename = "miku.png";
    private String name = "image";

    public FilePartStub(Flux<DataBuffer> content, long length) {
        this.content = content;
        this.length = length;
    }

    public FilePartStub(Flux<DataBuffer> content) {
        this.content = content;
    }

    public FilePartStub(Flux<DataBuffer> content, long length, String filename) {
        this.content = content;
        this.length = length;
        this.filename = filename;
    }

    public FilePartStub(Flux<DataBuffer> content, long length, String filename, String name) {
        this.content = content;
        this.length = length;
        this.filename = filename;
        this.name = name;
    }

    @Override
    @NotNull
    public String filename() {
        return filename;
    }

    @Override
    @NotNull
    public Mono<Void> transferTo(@NotNull Path dest) {
        return DataBufferUtils.write(content, dest);
    }

    @Override
    @NotNull
    public String name() {
        return name;
    }

    @Override
    @NotNull
    public HttpHeaders headers() {
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(length));
        return HttpHeaders.readOnlyHttpHeaders(headers);
    }

    @Override
    @NotNull
    public Flux<DataBuffer> content() {
        return content;
    }
}
