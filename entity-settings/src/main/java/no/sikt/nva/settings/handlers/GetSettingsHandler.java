package no.sikt.nva.settings.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import no.sikt.nva.settings.service.AccessControl;
import no.sikt.nva.settings.service.SettingsService;
import no.sikt.nva.settings.model.SettingsDto;
import nva.commons.apigateway.AccessRight;
import nva.commons.apigateway.ApiGatewayHandler;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.ApiGatewayException;
import nva.commons.core.Environment;
import nva.commons.core.JacocoGenerated;
import nva.commons.core.paths.UriWrapper;
import org.apache.http.HttpStatus;

import java.util.UUID;

public class GetSettingsHandler  extends ApiGatewayHandler<Void, SettingsDto> {

    private final SettingsService settingsService;

    @JacocoGenerated
    public GetSettingsHandler() {
        this(new Environment());
    }

    @JacocoGenerated
    public GetSettingsHandler(Environment environment) {
        this(environment, new SettingsService(environment));
    }

    public GetSettingsHandler(Environment environment, SettingsService settingsService) {
        super(Void.class, environment);
        this.settingsService = settingsService;
    }
    @Override
    protected SettingsDto processInput(Void input, RequestInfo requestInfo, Context context)
        throws ApiGatewayException {
        AccessControl
            .validate(requestInfo, AccessRight.USER);
        return
            settingsService
                .getSetting(requestInfo.getRequestUri());
    }

    @Override
    protected Integer getSuccessStatusCode(Void input, SettingsDto output) {
        return HttpStatus.SC_OK;
    }
}
