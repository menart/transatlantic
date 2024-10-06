package express.atc.backend.service.Impl;

import express.atc.backend.db.entity.FileEntity;
import express.atc.backend.db.repository.FileRepository;
import express.atc.backend.dto.FileDto;
import express.atc.backend.dto.UserDto;
import express.atc.backend.mapper.FileMapper;
import express.atc.backend.service.FilesStorageService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class FilesStorageServiceImpl implements FilesStorageService {

    private final Path root = Paths.get("uploads");
    private final FileRepository fileRepository;
    private final FileMapper fileMapper;

    @Override
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    @Override
    @Transactional
    public FileDto save(MultipartFile file, UserDto user) {
        var uuid = UUID.randomUUID();
        try {
            var size = Files.copy(file.getInputStream(), this.root.resolve(uuid.toString()));
            var entity = FileEntity.builder()
                    .uuid(uuid)
                    .filename(file.getOriginalFilename())
                    .type(file.getContentType())
                    .userId(user.getId())
                    .size(size)
                    .build();
            return fileMapper.toDto(fileRepository.save(entity));
        } catch (Exception e) {
            Path negativeFile = root.resolve(String.valueOf(uuid));
            if (Files.exists(negativeFile)) {
                try {
                    Files.delete(negativeFile);
                } catch (IOException exception) {
                    throw new RuntimeException(e.getMessage() + '\n' + exception.getMessage());
                }
            }
            if (e instanceof FileAlreadyExistsException) {
                throw new RuntimeException("A file of that name already exists.");
            }
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteAll() {
        return FileSystemUtils.deleteRecursively(root.toFile());
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files
                    .walk(this.root, 1)
                    .filter(path -> !path.equals(this.root))
                    .map(this.root::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }
}
