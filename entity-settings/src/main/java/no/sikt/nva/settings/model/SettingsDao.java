package no.sikt.nva.settings.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import no.unit.nva.commons.json.JsonUtils;
import nva.commons.core.JacocoGenerated;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.Instant;
import java.util.Objects;

import static nva.commons.core.attempt.Try.attempt;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonDeserialize(builder = SettingsDao.Builder.class)
@DynamoDbBean
public class SettingsDao {
    private String settingsId;
    private Instant updated;
    private String payload;

    private SettingsDao(Builder builder) {
        setSettingsId(builder.settingsId);
        setUpdated(builder.updated);
        setPayload(builder.payload);
    }

    @DynamoDbPartitionKey
    public String getSettingsId() {
        return settingsId;
    }

    public void setSettingsId(String settingsId) {
        this.settingsId = settingsId;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Instant getUpdated() {
        return updated;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
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
            && Objects.equals(getUpdated(), that.getUpdated())
            && Objects.equals(getPayload(), that.getPayload());
    }

    @Override
    @JacocoGenerated
    public int hashCode() {
        return Objects.hash(getSettingsId(), getUpdated(), getPayload());
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

    public SettingsDao merge(SettingsDto setting) {
        setPayload(setting.payload().toString());
        setUpdated(Instant.now());
        return this;
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private String settingsId;
        public Instant updated;
        private String payload;

        private Builder() {
            this.updated = Instant.now();
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder withSettingsId(String settingsId) {
            this.settingsId = settingsId;
            return this;
        }

        public Builder withUpdated(Instant updated) {
            this.updated = updated;
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
