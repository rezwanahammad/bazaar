package com.example.bazaar.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CloudinaryImageService {

    private final Cloudinary cloudinary;

    public String uploadProductImage(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            return null;
        }

        if (!isConfigured()) {
            log.warn("Cloudinary config missing. Skipping upload and using fallback image URL.");
            return null;
        }

        try {
                String publicId = "product-" + UUID.randomUUID();
                Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    imageFile.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "bazaar/products",
                        "public_id", publicId,
                        "overwrite", false,
                        "unique_filename", true,
                            "resource_type", "image"
                    )
            );
            Object secureUrl = uploadResult.get("secure_url");
                if (secureUrl != null) {
                log.info("Cloudinary uploaded image successfully. publicId={}, url={}", publicId, secureUrl);
                return secureUrl.toString();
                }
                return null;
        } catch (Exception exception) {
            log.warn("Cloudinary upload failed. Continuing with fallback image URL. Reason: {}", exception.getMessage());
            return null;
        }
    }

    private boolean isConfigured() {
        Object cloudName = cloudinary.config.cloudName;
        Object apiKey = cloudinary.config.apiKey;
        Object apiSecret = cloudinary.config.apiSecret;
        return cloudName != null && !cloudName.toString().isBlank()
                && apiKey != null && !apiKey.toString().isBlank()
                && apiSecret != null && !apiSecret.toString().isBlank();
    }
}
