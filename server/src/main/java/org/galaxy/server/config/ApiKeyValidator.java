package org.galaxy.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Component responsible for validating API keys at startup.
 */
@Component
@Profile("!test")
public class ApiKeyValidator {

    public ApiKeyValidator(@Value("${spring.ai.openai.api-key:}") String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalStateException(
                    "OPENAI_API_KEY is not set. Define it as an environment variable."
            );
        }
    }
}