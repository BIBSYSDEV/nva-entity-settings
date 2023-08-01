package no.sikt.nva.settings.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import no.sikt.nva.settings.model.SettingsDao;
import no.sikt.nva.settings.model.SettingsDto;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.apigateway.exceptions.BadGatewayException;
import nva.commons.apigateway.exceptions.GatewayResponseSerializingException;
import nva.commons.apigateway.exceptions.NotFoundException;
import nva.commons.core.Environment;
import nva.commons.core.JacocoGenerated;
import nva.commons.core.paths.UriWrapper;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.net.URI;
import java.util.Objects;
import java.util.UUID;

public class  SettingsService {

    private static final String SETTING_NOT_FOUND_CLIENT_MESSAGE = "Settings not found: ";
    private static final String ENVIRONMENT_TABLE_NAME = "TABLE_NAME";
    private static final String ENVIRONMENT_AWS_REGION = "AWS_REGION";
    private static final String DEFAULT_TABLE_NAME = "Settings";
    private static final String EU_VEST_1 = "eu-vest-1";
    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbTable<SettingsDao> settingsTable;

    private static DynamoDbClient dbClientFromEnvironment(Environment environment) {
        var regionName =
            environment
                .readEnvOpt(ENVIRONMENT_AWS_REGION)
                .orElse(EU_VEST_1);
        return
            DynamoDbClient.builder()
                .region(Region.of(regionName))
                .build();
    }

    private static String tableNameFromEnvironment(Environment environment) {
        return
            environment
                .readEnvOpt(ENVIRONMENT_TABLE_NAME)
                .orElse(DEFAULT_TABLE_NAME);
    }

    @JacocoGenerated
    public SettingsService(Environment environment) {
        this(
            dbClientFromEnvironment(environment),
            tableNameFromEnvironment(environment)
        );
    }

    /**
     * Creates a new DynamoDBClient.
     */
    public SettingsService(DynamoDbClient dynamoDbClient,String tableName) {
        this.enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();
        settingsTable = enhancedClient.table(tableName, TableSchema.fromBean(SettingsDao.class));
    }

    /**
     * Gets the contentsDocument by given isbn.
     *
     * @param id identifier
     * @return contentsDocument as json string
     * @throws NotFoundException contentsDocument not found
     */
    public SettingsDto getSetting(URI id) throws ApiGatewayException {
        try {
            var key = Key.builder()
                .partitionValue(extractUuidFromUri(id).toString())
                .build();
            var settingDao = settingsTable
                .getItem(requestBuilder -> requestBuilder.key(key));

            return Objects.requireNonNullElseGet(settingDao, () -> createEmptySetting(id))
                .toSettingDto();

        } catch (DynamoDbException e) {
            throw new NotFoundException(SETTING_NOT_FOUND_CLIENT_MESSAGE);
        } catch (JsonProcessingException e) {
            throw new GatewayResponseSerializingException(e);
        }
    }

    /**
     * Updates the contentsDocument identified by its isbn.
     *
     * @param setting to be updated
     * @return
     * @throws ApiGatewayException exception while connecting to database
     */
    public SettingsDto update(SettingsDto setting) throws ApiGatewayException {
        try {
            var oldSetting = getSetting(setting.settingsId())
                .toSettingDao();
            return settingsTable.updateItem(oldSetting.merge(setting))
                .toSettingDto();
        } catch (DynamoDbException | JsonProcessingException e) {
            throw new BadGatewayException(e.getMessage());
        }
    }

    private SettingsDao createEmptySetting(URI id) {
        var settingsId = extractUuidFromUri(id).toString();
        var newSetting = SettingsDao.Builder.builder()
            .withSettingsId(settingsId)
                .build();
        settingsTable.putItem(newSetting);
        return newSetting;
    }

    private UUID extractUuidFromUri(URI id) {
        return
            UUID.fromString(
                UriWrapper.fromUri(id)
                    .getLastPathElement()
            );
    }

}
