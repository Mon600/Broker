package com.shastage.store.domain.service;


import com.shastage.store.domain.model.Image;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor

public class RedisService {

    private final ImageService imageService;

    @CachePut(value = "image", key = "#image.id.toString()")
    public Image saveImage(Image image) {
        return image;
    }

    @Cacheable(value = "image", key = "#uuid")
    public Image getImage(String uuid) {
        return imageService.findById(UUID.fromString(uuid));
    }

    @CacheEvict(value = "image", key = "#uuid")
    public void deleteImage(String uuid) {
        imageService.deleteById(UUID.fromString(uuid));
    }

    @CachePut(value = "image", key = "#result.id.toString()")
    public Image updateImage(String uuid, byte[] image) {
        return imageService.update(UUID.fromString(uuid), image);
    }
}
