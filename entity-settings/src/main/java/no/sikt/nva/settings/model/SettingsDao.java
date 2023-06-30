package no.sikt.nva.settings.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import no.unit.nva.commons.json.JsonUtils;
import nva.commons.core.JacocoGenerated;

import java.net.URI;
import java.util.Objects;

import static nva.commons.core.attempt.Try.attempt;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonDeserialize(builder = SettingsDao.Builder.class)
public class SettingsDao {
    private URI settingsId;
    private String payload;

    private SettingsDao(Builder builder) {
        setSettingsId(builder.settingsId);
        setPayload(builder.payload);
    }

    public URI getSettingsId() {
        return settingsId;
    }

    public void setSettingsId(URI settingsId) {
        this.settingsId = settingsId;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    @JacocoGenerated
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SettingsDao that)) {
            return false;
        }
        return Objects.equals(getSettingsId(), that.getSettingsId())
            && Objects.equals(getPayload(), that.getPayload());
    }

    @Override
    @JacocoGenerated
    public int hashCode() {
        return Objects.hash(getSettingsId(), getPayload());
    }

    @Override
    public String toString() {
        return attempt(() -> JsonUtils.dynamoObjectMapper.writeValueAsString(this)).orElseThrow();
    }

    public SettingsDto toSettingDto() throws JsonProcessingException {
        return SettingsDto.Builder.builder()
            .withSettingsId(getSettingsId())
            .withPayload(getPayload())
            .withDefaultContext()
            .build();
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private URI settingsId;
        private String payload;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder withSettingsId(URI settingsId) {
            this.settingsId = settingsId;
            return this;
        }

        public Builder withPayload(String payload) {
            this.payload = payload;
            return this;
        }

        public Builder withPayload(JsonNode payload) {
            this.payload = payload.asText();
            return this;
        }

        public SettingsDao build() {
            return new SettingsDao(this);
        }
    }
}
