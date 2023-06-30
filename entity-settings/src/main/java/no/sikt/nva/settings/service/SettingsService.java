package no.sikt.nva.settings.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import no.sikt.nva.settings.model.SettingsDto;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.apigateway.exceptions.ApiIoException;
import nva.commons.apigateway.exceptions.NotFoundException;
import nva.commons.core.Environment;
import nva.commons.core.JacocoGenerated;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SettingsService {

    public static final String SETTING_NOT_FOUND_CLIENT_MESSAGE = "Setting not found: ";

    private final DynamoDbClient dbClient;
    private final String tableName;

    @JacocoGenerated
    public SettingsService(Environment environment) {
        tableName = environment.readEnvOpt("TABLE_NAME").orElse("Settings");
        var region = environment.readEnvOpt("AWS_REGION").orElse("eu-vest-1");
        dbClient = DynamoDbClient.builder()
            .region(Region.of(region))
            .build();
    }

    /**
     * Creates a new DynamoDBClient.
     */
    public SettingsService(DynamoDbClient dbClient,String tableName) {
        this.tableName = tableName;
        this.dbClient = dbClient;
    }

    /**
     * Gets the contentsDocument by given isbn.
     *
     * @param id identifier
     * @return contentsDocument as json string
     * @throws NotFoundException contentsDocument not found
     */
    public SettingsDto getSetting(UUID id) throws NotFoundException {
        HashMap<String, AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put("ID", AttributeValue.builder().s(id).build());
        GetItemRequest request = GetItemRequest.builder()
            .key(keyToGet)
            .tableName(tableName)
            .build();
        try {
            GetItemResponse itemResponse = dbClient.getItem(request);
            if (itemResponse != null) {
                Map<String, AttributeValue> returnedItem = itemResponse.item();
                if (returnedItem != null && !returnedItem.isEmpty()) {
                    return parseAttributeValueMap(returnedItem);
                }
            }

            throw new NotFoundException(SETTING_NOT_FOUND_CLIENT_MESSAGE);
        } catch (DynamoDbException | JsonProcessingException e) {
            throw new NotFoundException(SETTING_NOT_FOUND_CLIENT_MESSAGE);
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
            HashMap<String, AttributeValue> keyToUpdate = new HashMap<>();
            keyToUpdate.put("ID", AttributeValue.builder().s(setting.settingsId()).build());
            Map<String, AttributeValueUpdate> attributeUpdates = this.findValuesToUpdate(setting);
            UpdateItemRequest updateItemRequest = UpdateItemRequest
                .builder()
                .key(keyToUpdate)
                .tableName(tableName)
                .attributeUpdates(attributeUpdates)
                .build();
            dbClient.updateItem(updateItemRequest);
        } catch (DynamoDbException e) {
            throw new ApiIoException(e, e.getMessage());
        }
        return setting;
    }


}
