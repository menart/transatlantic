package express.atc.backend.service;

import express.atc.backend.dto.FileDto;
import express.atc.backend.dto.UserDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface FilesStorageService {

    void init();
    FileDto save(MultipartFile file, UserDto user);
    Resource load(String filename);
    boolean deleteAll();
    Stream<Path> loadAll();
}