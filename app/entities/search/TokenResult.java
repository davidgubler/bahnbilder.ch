package entities.search;

import biz.FreeTextSearch;
import entities.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TokenResult {
    private final String token;

    private final Map<FreeTextSearch.SearchCriterion<NumIdEntity>, Map<NumIdEntity, Float>> results;

    private final Map<FreeTextSearch.SearchCriterion<NumIdEntity>, Map<Integer, Float>> idResults;

    public TokenResult(String token, Map<FreeTextSearch.SearchCriterion<NumIdEntity>, Map<NumIdEntity, Float>> results) {
        this.token = token;
        this.results = results;

        Map<FreeTextSearch.SearchCriterion<NumIdEntity>, Map<Integer, Float>> idResults = new HashMap<>();
        for (FreeTextSearch.SearchCriterion sc : results.keySet()) {
            Map<Integer, Float> scResults = new HashMap<>();
            for (Map.Entry<NumIdEntity, Float> entry : results.get(sc).entrySet()) {
                scResults.put(entry.getKey().getId(), entry.getValue());
            }
            idResults.put(sc, Collections.unmodifiableMap(scResults));
        }
        this.idResults = Collections.unmodifiableMap(idResults);
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        String s = token;

        for (FreeTextSearch.SearchCriterion sc : FreeTextSearch.SEARCH_CRITERIA) {
            if (!results.get(sc).isEmpty()) {
                s += "->" + results.get(sc);
            }
        }
        return s;
    }

    public boolean ignored() {
        for (FreeTextSearch.SearchCriterion sc : FreeTextSearch.SEARCH_CRITERIA) {
            if (!results.get(sc).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public Map<Integer, Float> getIdResults(FreeTextSearch.SearchCriterion sc) {
        return idResults.get(sc);
    }
}
