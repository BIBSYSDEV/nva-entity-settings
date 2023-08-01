package no.sikt.nva.settings.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import no.sikt.nva.settings.model.SettingsDto;
import no.sikt.nva.settings.service.AccessControl;
import no.sikt.nva.settings.service.SettingsService;
import nva.commons.apigateway.AccessRight;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.core.Environment;
import nva.commons.core.JacocoGenerated;

public class UpdateSettingsHandler  extends ApiGatewayHandler<SettingsDto, SettingsDto> {

    private final SettingsService settingsService;

    @JacocoGenerated
    public UpdateSettingsHandler() {
        this(new Environment());
    }

    @JacocoGenerated
    public UpdateSettingsHandler(Environment environment) {
        this(environment, new SettingsService(environment));
    }

    public UpdateSettingsHandler(Environment environment, SettingsService settingsService) {
        super(SettingsDto.class, environment);
        this.settingsService = settingsService;
    }
    @Override
    protected SettingsDto processInput(SettingsDto inputSettings, RequestInfo requestInfo, Context context)
        throws ApiGatewayException {
        AccessControl
            .validate(requestInfo, AccessRight.USER);
        return
            settingsService
                .update(inputSettings);
    }

    @Override
    protected Integer getSuccessStatusCode(SettingsDto input, SettingsDto output) {
        return null;
    }


}
