package com.example.tutorialBackend.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.LocalDateTime;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.tutorialBackend.common.annotation.Retry;
import com.example.tutorialBackend.domain.BoardFile;
import com.example.tutorialBackend.dto.BoardFileView;
import com.example.tutorialBackend.dto.ParallelInitResponse;
import com.example.tutorialBackend.mapper.BoardFileMapper;
import com.example.tutorialBackend.repository.BoardFileRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NubesService {

    private static final String BUCKET_NAME = "wcs_toy";
    private static final String DIRECTORY_NAME = "test_contents";

    private static final Integer RETENTION_TIME_SEC = 300;
    private final RestTemplate restTemplate;

    @Qualifier("nonClosingRestTemplate")
    private final RestTemplate nonClosingRestTemplate;

    private final BoardFileRepository boardFileRepository;
    private final BoardFileMapper boardFileMapper;

    @Value("${nubes.url}")
    private String nubesDns;

    public NubesService(RestTemplate restTemplate,
        RestTemplate nonClosingRestTemplate, BoardFileRepository boardFileRepository,
        BoardFileMapper boardFileMapper) {
        this.restTemplate = restTemplate;
        this.nonClosingRestTemplate = nonClosingRestTemplate;
        this.boardFileRepository = boardFileRepository;
        this.boardFileMapper = boardFileMapper;
    }

    @Transactional
    @Retry(retry = 3, delay = 400L)
    public BoardFileView fileSerialUpload(String fileName, String path, Long fileSize, String createBy, InputStream in) {
        final URI uri = restTemplate.getUriTemplateHandler().expand(makeGatewayUrl(path, true));
        restTemplate.execute(uri, HttpMethod.POST, makeRequestCallback(in, fileSize), makeResponseExtractor());

        BoardFile boardFile = BoardFile.builder()
            .createdAt(LocalDateTime.now())
            .originalFileName(fileName)
            .nubesFileName(path)
            .size(fileSize)
            .createdBy(createBy)
            .build();

        boardFileRepository.save(boardFile);
        return boardFileMapper.toBoardFileView(boardFile);
    }

    public ParallelInitResponse fileParallelUploadInit(String path, Integer partSize, Long totalSize) {
        final URI uri = restTemplate.getUriTemplateHandler().expand(makeParallelUploadInitUrl(path, partSize));
        final MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("X-Upload-Content-Length", String.valueOf(totalSize));

        RequestEntity httpEntity = new RequestEntity(headers, HttpMethod.POST, uri);
        ResponseEntity responseEntity = restTemplate.exchange(httpEntity, Object.class);
        ParallelInitResponse parallelInitResponse = ParallelInitResponse.builder()
            .chunkGroupId(responseEntity.getHeaders().get("X-Upload-Key").get(0))
            .partSize(Integer.parseInt(responseEntity.getHeaders().get("X-Part-Size").get(0)))
            .build();
        return parallelInitResponse;
    }

    @Retry(retry = 10, delay = 400L)
    public void fileParallelUpload(String path, String uploadKey, Integer partNo, Long fileSize, InputStream in) {
        final URI uri = restTemplate.getUriTemplateHandler().expand(makeParallelUploadUrl(path, uploadKey, partNo));
        restTemplate.execute(uri, HttpMethod.PUT, makeRequestCallback(in, fileSize), makeResponseExtractor());

    }

    @Transactional
    public BoardFileView fileparallelUploadComplete(String fileName, String path, Long fileSize, String createdBy, String uploadKey) {
        final URI uri = restTemplate.getUriTemplateHandler().expand(makeParallelUploadCompleteUrl(fileName, uploadKey));
        ResponseEntity responseEntity = restTemplate.postForEntity(uri, null, Object.class);
        String length = responseEntity.getHeaders().get("Range").get(0);

        BoardFile boardFile = BoardFile.builder()
            .createdAt(LocalDateTime.now())
            .originalFileName(fileName)
            .nubesFileName(path)
            .size(fileSize)
            .createdBy(createdBy)
            .build();

        boardFileRepository.save(boardFile);
        return boardFileMapper.toBoardFileView(boardFile);
    }

    public Resource download(String path) {
        final URI uri = nonClosingRestTemplate.getUriTemplateHandler().expand(makeGatewayUrl(path, false));
        return nonClosingRestTemplate.getForObject(uri, InputStreamResource.class);
    }

    private String makeParallelUploadCompleteUrl(String path, String uploadKey) {
        String gateWayUrl = makeGatewayUrl(path, true);
        UriComponentsBuilder builder =
            UriComponentsBuilder.fromHttpUrl(gateWayUrl)
                .queryParam("upload-type", "parallel")
                .queryParam("upload-key", uploadKey);

        return builder.build().toString();
    }

    private ResponseExtractor<ResponseEntity> makeResponseExtractor() {
        return response -> ResponseEntity.status(response.getRawStatusCode()).headers(response.getHeaders()).build();
    }

    private String makeParallelUploadInitUrl(String path, Integer fileSize) {
        String gateWayUrl = makeGatewayUrl(path, true);
        UriComponentsBuilder builder =
            UriComponentsBuilder.fromHttpUrl(gateWayUrl)
                .queryParam("upload-type", "parallel")
                .queryParam("part-size", fileSize)
                .queryParam("retention-time-sec", RETENTION_TIME_SEC);

        return builder.build().toString();
    }

    private String makeParallelUploadUrl(String path, String uploadKey, Integer partNo) {
        String gateWayUrl = makeGatewayUrl(path, true);
        UriComponentsBuilder builder =
            UriComponentsBuilder.fromHttpUrl(gateWayUrl)
                .queryParam("upload-type", "parallel")
                .queryParam("upload-key", uploadKey)
                .queryParam("part-no", partNo);

        return builder.build().toString();
    }

    private String makeGatewayUrl(String path, boolean overwrite) {

        UriComponentsBuilder builder =
            UriComponentsBuilder.fromHttpUrl(nubesDns)
                .path("/v1/{bucket-name}/{directory}/{path}");

        if (overwrite) {
            builder = builder.queryParam("overwrite", "true");
        }

        return builder.build()
            .expand(BUCKET_NAME, DIRECTORY_NAME, path)
            .toString();
    }

    private RequestCallback makeRequestCallback(InputStream in, long chunkSize) {
        return request -> {
            try {
                final HttpHeaders headers = request.getHeaders();
                headers.add("X-Upload-Content-Length", String.valueOf(chunkSize));
                IOUtils.copy(in, request.getBody(), 1024 * 1024);
            } catch (IOException e) {
                log.warn("Upload Fail", e);
            }
        };
    }
}
