package com.github.prorhap.coupon.play.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.prorhap.coupon.play.common.CouponDateUtils;
import com.github.prorhap.coupon.play.common.CouponNotExistException;
import com.github.prorhap.coupon.play.dto.CouponDefaultRequest;
import com.github.prorhap.coupon.play.dto.CouponIssueRequest;
import com.github.prorhap.coupon.play.helper.*;
import com.github.prorhap.coupon.play.model.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultCouponServiceTest {

    @Mock
    private CouponCodeGenerator couponCodeGenerator;
    @Mock
    private KafkaProducingService kafkaProducingService;
    @Mock
    private RemoteCache remoteCache;
    @Mock
    private CouponRepository couponRepository;
    @Mock
    private CouponFactory couponFactory;
    @Mock
    private CouponDateUtils couponDateUtils;
    @Mock
    private EsClient esClient;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DefaultCouponService couponService;

    @Test
    public void itShouldGetCouponFromDdbWhenMemcacheReturnsEmpty() throws Exception {

        String userId = "alice";
        String couponCode = "AAAABBBBBBCCCCCCCCCC";
        CouponIssueRequest couponIssueRequest = new CouponIssueRequest(userId, couponCode);
        when(remoteCache.get(couponCode)).thenReturn(CompletableFuture.completedFuture(""));

        couponService.issueCoupon(couponIssueRequest);

        verify(couponRepository).getItemAsync(eq(couponCode), any(ItemMapper.class));
        verify(remoteCache, never()).set(eq(couponCode), anyString());
    }

    @Test(expected = CouponNotExistException.class)
    public void itShouldThrowExceptionWhenCouponDontExistAnywhere() throws Throwable {

        String userId = "alice";
        String couponCode = "AAAABBBBBBCCCCCCCCCC";
        CouponIssueRequest couponIssueRequest = new CouponIssueRequest(userId, couponCode);
        when(remoteCache.get(couponCode)).thenReturn(CompletableFuture.completedFuture(""));
        when(couponRepository.getItemAsync(eq(couponCode), any(ItemMapper.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        try {
            couponService.issueCoupon(couponIssueRequest).toCompletableFuture().get();
        } catch (Exception e) {
            throw e.getCause();
        }
    }

    @Test
    public void itShouldPutEventToKafkaWhenCouponIssueValidationIsCancel() throws Exception {
        String userId = "alice";
        String couponCode = "AAAABBBBBBCCCCCCCCCC";
        String jsonResult = "correctResult";

        CouponIssueRequest couponIssueRequest = new CouponIssueRequest(userId, couponCode);
        Date now = new Date();

        Coupon coupon = mock(Coupon.class);
        when(remoteCache.get(couponCode)).thenReturn(CompletableFuture.completedFuture(jsonResult));
        when(objectMapper.readValue(jsonResult, Coupon.class)).thenReturn(coupon);
        when(couponDateUtils.now()).thenReturn(now);
        when(coupon.isIssueable(now)).thenReturn(CouponIssueValidationResult.OK);

        couponService.issueCoupon(couponIssueRequest);

        verify(kafkaProducingService, times(1)).sendIssue(coupon);
    }

    @Test
    public void itShouldPutEventToKafkaWhenCouponUseValidationIsOk() throws Exception {
        String couponCode = "AAAABBBBBBCCCCCCCCCC";
        String jsonResult = "correctResult";
        CouponDefaultRequest couponUseRequest = new CouponDefaultRequest(couponCode);
        Date now = new Date();

        Coupon coupon = mock(Coupon.class);
        when(remoteCache.get(couponCode)).thenReturn(CompletableFuture.completedFuture(jsonResult));
        when(objectMapper.readValue(jsonResult, Coupon.class)).thenReturn(coupon);
        when(couponDateUtils.now()).thenReturn(now);
        when(coupon.isUseable(now)).thenReturn(CouponUseValidationResult.OK);

        couponService.useCoupon(couponUseRequest);

        verify(kafkaProducingService).sendUse(coupon);
    }

    @Test
    public void itShouldPutEventToKafkaWhenCouponCancelValidationIsCancel() throws Exception {
        String couponCode = "AAAABBBBBBCCCCCCCCCC";
        String jsonResult = "correctResult";
        CouponDefaultRequest couponCancelRequest = new CouponDefaultRequest(couponCode);
        Date now = new Date();

        Coupon coupon = mock(Coupon.class);
        when(remoteCache.get(couponCode)).thenReturn(CompletableFuture.completedFuture(jsonResult));
        when(objectMapper.readValue(jsonResult, Coupon.class)).thenReturn(coupon);
        when(couponDateUtils.now()).thenReturn(now);
        when(coupon.isCancelable(now)).thenReturn(CouponCancelValidationResult.OK);

        couponService.cancelCoupon(couponCancelRequest);

        verify(kafkaProducingService).sendCancel(coupon);
    }

}