package com.example.tutorialBackend.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.example.tutorialBackend.dto.BoardFileView;
import com.example.tutorialBackend.dto.ParallelInitResponse;
import com.example.tutorialBackend.dto.request.ParallelCompleteRequest;
import com.example.tutorialBackend.dto.request.ParallelInitRequest;
import com.example.tutorialBackend.service.NubesService;

@Controller
@RequestMapping("/api/file")
@CrossOrigin("*")
public class FileController {

    private final NubesService nubesService;

    public FileController(NubesService nubesService) {
        this.nubesService = nubesService;
    }

    @PostMapping("/upload/serial")
    public ResponseEntity<BoardFileView> uploadFile(HttpServletRequest request,
        @RequestHeader("fileName") String fileName,
        @RequestHeader("Content-Length") Long fileSize
    ) throws IOException {
        final String realFileName = UUID.randomUUID().toString();
        String decodeFileName = URLDecoder.decode(fileName, "UTF-8");

        try (
            final CheckedInputStream cin = new CheckedInputStream(request.getInputStream(), new CRC32())
        ) {
            return ResponseEntity.ok(nubesService.fileSerialUpload(decodeFileName, realFileName, fileSize,
                request.getRemoteAddr(), cin));
        }
    }

    @PostMapping("/upload/parallel/init")
    public ResponseEntity<ParallelInitResponse> parallelUploadInit(@RequestBody ParallelInitRequest request) throws
        UnsupportedEncodingException {
        String decodeFileName = URLDecoder.decode(request.getFileName(), "UTF-8");
        return ResponseEntity.ok(nubesService.fileParallelUploadInit(decodeFileName, request.getPartSize(), request.getTotalSize()));
    }

    @PostMapping("/upload/parallel/stream")
    public ResponseEntity parallelUploadChunk(
        HttpServletRequest request,
        @RequestHeader("fileName") String fileName,
        @RequestHeader("Content-Length") Long fileSize,
        @RequestHeader("uploadKey") String uploadKey,
        @RequestHeader("partNo") Integer partNo
    ) throws IOException {
        String decodeFileName = URLDecoder.decode(fileName, "UTF-8");
        try (
            final CheckedInputStream cin = new CheckedInputStream(request.getInputStream(), new CRC32())
        ) {
            nubesService.fileParallelUpload(decodeFileName, uploadKey, partNo, fileSize, cin);
            return ResponseEntity.ok().build();
        }
    }

    @PostMapping("/upload/parallel/complete")
    public ResponseEntity parallelUploadComplete(
        HttpServletRequest httpServletRequest, @RequestBody ParallelCompleteRequest request
    ) throws UnsupportedEncodingException {
        String realFileName = UUID.randomUUID().toString();
        String decodeFileName = URLDecoder.decode(request.getFileName(), "UTF-8");

        BoardFileView boardFileView = nubesService.fileparallelUploadComplete(decodeFileName, realFileName, request.getFileSize(),
                                                    httpServletRequest.getRemoteAddr(), request.getUploadKey());
        return ResponseEntity.ok(boardFileView);
    }

    @GetMapping("/download/{path}")
    public ResponseEntity<StreamingResponseBody> download(
        @PathVariable String path,
        @RequestParam("fileName") String fileName
    ) throws UnsupportedEncodingException {
        fileName = URLEncoder.encode(fileName, "UTF-8");

        final String resultFileName = "attachment; filename=\"" + fileName + "\"";
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, resultFileName)
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(outputStream -> {
                try (
                    InputStream inputStream = nubesService.download(path).getInputStream();
                ) {
                    StreamUtils.copy(inputStream, outputStream);
                }
            });
    }

}
