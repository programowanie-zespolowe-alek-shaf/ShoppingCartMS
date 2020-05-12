package pl.agh.shopping.card.application.rest.url;

import pl.agh.shopping.card.application.rest.MicroService;

public interface URLProvider {

    String getBaseURL(MicroService microService);
}
