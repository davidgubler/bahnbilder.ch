package entities.search;

import biz.FreeTextSearch;
import entities.*;

import java.util.Map;

public class TokenResult {
    private final String token;

    private final Map<FreeTextSearch.SearchCriterion<NumIdEntity>, Map<NumIdEntity, Float>> results;

    public TokenResult(String token, Map<FreeTextSearch.SearchCriterion<NumIdEntity>, Map<NumIdEntity, Float>> results) {
        this.token = token;
        this.results = results;
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

    public Map<NumIdEntity, Float> get(FreeTextSearch.SearchCriterion sc) {
        return results.get(sc);
    }
}
