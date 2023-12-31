package no.sikt.nva.settings.model;

import static nva.commons.core.attempt.Try.attempt;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;
import no.unit.nva.commons.json.JsonUtils;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public record SettingsDto(URI id, @JsonProperty("@context") JsonNode context, JsonNode payload)  {

    @Override
    public String toString() {
        return attempt(() -> JsonUtils.dtoObjectMapper.writeValueAsString(this)).orElseThrow();
    }

    public SettingsDao toSettingDao() {
        return SettingsDao.Builder.builder()
            .withSettingsId(id().toString())
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
