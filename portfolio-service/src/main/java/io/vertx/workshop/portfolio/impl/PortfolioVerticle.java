package io.vertx.workshop.portfolio.impl;

import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ProxyHelper;
import io.vertx.workshop.common.MicroServiceVerticle;
import io.vertx.workshop.portfolio.PortfolioService;

import java.util.Map;

import static io.vertx.workshop.portfolio.PortfolioService.ADDRESS;
import static io.vertx.workshop.portfolio.PortfolioService.EVENT_ADDRESS;

/**
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public class PortfolioVerticle extends MicroServiceVerticle {

  @Override
  public void start() throws Exception {
    super.start();
    PortfolioServiceImpl service = new PortfolioServiceImpl(vertx, discovery, config().getDouble("money", 10000.00));
    ProxyHelper.registerService(PortfolioService.class, vertx, service, ADDRESS);

    for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
      System.out.println(entry.getKey() + " = " + entry.getValue());
    }

    publishEventBusService("portfolio", ADDRESS, PortfolioService.class, ar -> {
      if (ar.failed()) {
        ar.cause().printStackTrace();
      } else {
        System.out.println("Portfolio service published : " + ar.succeeded());
      }
    });

    // The portfolio event service
    publishMessageSource("portfolio-events", EVENT_ADDRESS, JsonObject.class, ar -> {
      if (ar.failed()) {
        ar.cause().printStackTrace();
      } else {
        System.out.println("Portfolio Events service published : " + ar.succeeded());
      }
    });
  }
}
