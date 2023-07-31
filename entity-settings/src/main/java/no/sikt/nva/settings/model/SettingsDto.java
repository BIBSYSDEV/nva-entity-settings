package no.sikt.nva.settings.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import no.unit.nva.commons.json.JsonUtils;

import java.net.URI;
import java.util.Objects;

import static nva.commons.core.attempt.Try.attempt;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public record SettingsDto(URI settingsId, @JsonProperty("@context") JsonNode context, JsonNode payload)  {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SettingsDto that)) {
            return false;
        }
        return Objects.equals(settingsId(), that.settingsId())
            && Objects.equals(context(), that.context())
            && Objects.equals(payload(), that.payload());
    }

    @Override
    public int hashCode() {
        return Objects.hash(settingsId(), payload(), context());
    }

    @Override
    public String toString() {
        return attempt(() -> JsonUtils.dtoObjectMapper.writeValueAsString(this)).orElseThrow();
    }

    public SettingsDao toSettingDao() {
        return SettingsDao.Builder.builder()
            .withSettingsId(settingsId().toString())
            .withPayload(payload())
            .build();
    }

    public static final class Builder {
        private String settingsId;
        private String payload;
        private String context;

        private Builder() { }

        public static Builder builder() {
            return new Builder();
        }

        public Builder withSettingsId(String settingsId) {
            this.settingsId = settingsId;
            return this;
        }

        public Builder withPayload(String payload)  {
            this.payload = payload;
            return this;
        }

        public Builder withContext(String context)  {
            this.context = context;
            return this;
        }

        public Builder withDefaultContext() {
            return attempt(() -> withContext("{context:{}}")).get();
        }

        public SettingsDto build() throws JsonProcessingException {
            var contextNode =JsonUtils.dtoObjectMapper.readTree(context);
            var payloadNode = JsonUtils.dtoObjectMapper.readTree(payload);
            var uri = URI.create(settingsId);
            return new SettingsDto(uri, contextNode, payloadNode);
        }

    }
}
