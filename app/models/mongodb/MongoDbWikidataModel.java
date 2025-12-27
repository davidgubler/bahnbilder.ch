package models.mongodb;

import dev.morphia.UpdateOptions;
import dev.morphia.query.filters.Filters;
import dev.morphia.query.updates.UpdateOperators;
import entities.Search;
import entities.Wikidata;
import entities.Operator;
import entities.mongodb.MongoDbWikidata;
import entities.tmp.OperatorSummary;
import models.WikidataModel;
import utils.Config;
import utils.Context;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.List;

public class MongoDbWikidataModel extends MongoDbModel<MongoDbWikidata> implements WikidataModel {

    private static final String USER_AGENT = "TrainOperatorFetcher for " + Config.getSelfHost("de");

    private final HttpClient client;

    public MongoDbWikidataModel() {
        client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
    }

    private Function<HttpResponse<byte[]>, String> stringResponseHandler() {
        return httpResponse -> {
            if (httpResponse.statusCode() == 404) {
                return null;
            }
            if (httpResponse.statusCode() < 200 || httpResponse.statusCode() >= 300) {
                System.out.print(new String(httpResponse.body()));
                throw new CompletionException("HTTP response " + httpResponse.statusCode(), null);
            }
            try {
                return new String(httpResponse.body());
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        };
    }

    private String fetchJson(String ref) throws ExecutionException, InterruptedException {
        URI uri;
        try {
            uri = new URI("https://www.wikidata.org/wiki/Special:EntityData/" + ref + ".json");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        HttpRequest req = HttpRequest.newBuilder(uri).header("User-Agent", USER_AGENT).build();
        return client.sendAsync(req, java.net.http.HttpResponse.BodyHandlers.ofByteArray()).thenApply(stringResponseHandler()).get();
    }

    @Override
    public Wikidata get(String ref) {
        try {
            MongoDbWikidata wikidata = query().filter(Filters.eq("ref", ref)).first();
            if (wikidata == null) {
                wikidata = new MongoDbWikidata(ref, fetchJson(ref));
                getDs().save(wikidata);
            } else if (wikidata.needsRefresh()) {
                wikidata.setData(fetchJson(wikidata.getRef()));
                query(wikidata).update(new UpdateOptions(), UpdateOperators.set("data", wikidata.getData()), UpdateOperators.set("lastRefresh", wikidata.getLastRefresh()));
            }
            return wikidata;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<OperatorSummary> getOperatorSummaries(Operator operator) {
        List<Wikidata> wikidata = new ArrayList<>();
        operator.getWikiDataIds().forEach(ref -> {
            try {
                wikidata.add(get(ref));
            } catch (Exception e) {
                // too bad
            }
        });
        Collections.sort(wikidata);
        Collections.reverse(wikidata);

        List<OperatorSummary> operatorSummaries = new ArrayList<>();
        LocalDate nextInception = null;
        for(Wikidata d : wikidata) {
            LocalDate dissolved = d.getDissolved();
            if (dissolved == null && nextInception != null) {
                dissolved = nextInception;
            }

            operatorSummaries.add(new OperatorSummary(operator, d, d.getLogoUrl(), d.getLogoSrc(), d.getInceptedYear(), dissolved == null ? null : dissolved.plusDays(-1).getYear(), d.getRef()));
            nextInception = d.getIncepted();
        };
        return operatorSummaries;
    }
}
