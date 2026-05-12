package com.gator.dto;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
public record CreateFeedRequest(@NotBlank String name, @NotBlank @URL String url) {}
