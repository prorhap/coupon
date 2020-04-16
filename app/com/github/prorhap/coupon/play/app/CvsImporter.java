package com.github.prorhap.coupon.play.app;

import com.github.prorhap.coupon.play.common.CouponDateFormatConstant;
import com.github.prorhap.coupon.play.config.CvsImporterModule;
import com.github.prorhap.coupon.play.model.Coupon;
import com.github.prorhap.coupon.play.model.CouponFactory;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.stream.Stream;

public class CvsImporter implements CouponDateFormatConstant {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String CVS_IMPORT_DATE_FORMAT = "yyyyMMdd";

    private final CouponFactory couponFactory;
    private final CvsImporterKafkaProducer cvsImporterKafkaProducer;

    @Inject
    public CvsImporter(CouponFactory couponFactory, CvsImporterKafkaProducer cvsImporterKafkaProducer) {
        this.couponFactory = couponFactory;
        this.cvsImporterKafkaProducer = cvsImporterKafkaProducer;
    }

    public void start(String fileName) throws Exception {
        logger.info("start to import from {}", fileName);

        long current = new Date().getTime();

        Stream<String> stream = Files.lines(Paths.get(fileName));
        stream.forEach(line -> {
            try {
                if (!StringUtils.isEmpty(line)) {
                    String[] splitedLine = line.split(",");
                    if (splitedLine.length == 3) {
                        Coupon coupon = couponFactory.create(
                                splitedLine[0].trim()+current,
                                DateUtils.parseDate(splitedLine[1].trim(), CVS_IMPORT_DATE_FORMAT),
                                DateUtils.parseDate(splitedLine[2].trim(), CVS_IMPORT_DATE_FORMAT));

                        cvsImporterKafkaProducer.sendCreation(coupon);
                    } else {
                        logger.error("skip line = " + line);
                    }
                }
            } catch (Exception e) {
                logger.error("skip line = " + line, e);
            }
        });
        logger.info("finish");
    }

    public static void main(String[] args) throws Exception {
        Injector injector = Guice.createInjector(new CvsImporterModule(null, ConfigFactory.load()));
        injector.getInstance(CvsImporter.class).start(args[0]);
    }
}
