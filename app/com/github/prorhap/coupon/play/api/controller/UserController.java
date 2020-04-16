package com.github.prorhap.coupon.play.api.controller;

import com.github.prorhap.coupon.play.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

public class UserController extends Controller {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UserService userService;

    @Inject
    public UserController(UserService userService) {
        this.userService = userService;
    }

    public CompletionStage<Result> get(String userId) {
        logger.info("-c-> get coupons for {}", userId);
        return userService.getCoupons(userId)
                .thenApplyAsync(response -> ok(Json.toJson(response)));
    }
}
