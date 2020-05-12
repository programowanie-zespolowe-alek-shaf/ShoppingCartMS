package pl.agh.shopping.card.application.rest;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pl.agh.shopping.card.application.rest.url.URLProvider;

@Component
@RequiredArgsConstructor
public class RestClient {

    private final RestTemplate restTemplate;
    private final URLProvider urlProvider;
    private final Log logger = LogFactory.getLog(getClass());

    public <T> T get(MicroService ms, String url, Class<T> type) {
        String baseURL = urlProvider.getBaseURL(ms);
        String fullUrl = baseURL + url;
        try {
            logger.info(String.format("START GET: MS=[%s] URL=[%s] TYPE=[%s]", ms, fullUrl, type.getName()));
            return restTemplate.getForObject(fullUrl, type);
        } finally {
            logger.info(String.format("END   GET: MS=[%s] URL=[%s] TYPE=[%s]", ms, fullUrl, type.getName()));
        }
    }
}
