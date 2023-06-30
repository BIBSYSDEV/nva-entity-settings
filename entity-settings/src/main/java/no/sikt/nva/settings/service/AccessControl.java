package no.sikt.nva.settings.service;

import nva.commons.apigateway.AccessRight;
import nva.commons.apigateway.RequestInfo;
import nva.commons.apigateway.exceptions.UnauthorizedException;

public class AccessControl {

    public static void validate(RequestInfo requestInfo, AccessRight accessRight) throws UnauthorizedException {

        if (!requestInfo.userIsAuthorized(accessRight.name())) {
            throw new UnauthorizedException();
        }
    }

}
