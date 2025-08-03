package models.mongodb;

import dev.morphia.UpdateOptions;
import dev.morphia.query.filters.Filters;
import dev.morphia.query.updates.UpdateOperators;
import entities.Keyword;
import entities.formdata.KeywordFormData;
import entities.mongodb.MongoDbKeyword;
import models.KeywordsModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MongoDbKeywordsModel extends MongoDbModel<MongoDbKeyword> implements KeywordsModel {
    @Override
    public Keyword create(int id, String nameDe, String nameEn, List<String> labels) {
        Keyword keyword = new MongoDbKeyword(id, nameDe, nameEn, labels);
        mongoDb.getDs().save(keyword);
        return keyword;
    }

    @Override
    public Keyword create(KeywordFormData data, List<String> labels) {
        Keyword keyword = null;
        for (int i = 0; i < 10; i++) {
            try {
                keyword = new MongoDbKeyword(getNextNumId(), data.nameDe, data.nameEn, labels);
                mongoDb.getDs().save(keyword);
                break;
            } catch (Exception e) {
                // perhaps ID collision, try again. Throw exception in the last attempt.
                if (i == 9) {
                    throw e;
                }
            }
        }
        return keyword;
    }

    @Override
    public void update(KeywordFormData data, List<String> labels) {
        MongoDbKeyword mongoDbKeyword = (MongoDbKeyword)data.entity;
        mongoDbKeyword.setName("de", data.nameDe);
        mongoDbKeyword.setName("en", data.nameEn);
        mongoDbKeyword.setLabels(labels);
        query(mongoDbKeyword).update(new UpdateOptions(), UpdateOperators.set("names", mongoDbKeyword.getNames()), UpdateOperators.set("labels", mongoDbKeyword.getLabels()));
    }

    @Override
    public Map<Keyword, Boolean> getKeywordsMap(List<String> keywordStrings) {
        Map<Keyword, Boolean> keywords = new HashMap<>();
        if (keywordStrings == null || keywordStrings.isEmpty()) {
            return keywords;
        }
        kl: for (Keyword keyword : getAll().toList()) {
            for (String lang : keyword.getLanguages()) {
                if (keywordStrings.contains(keyword.getName(lang))) {
                    keywords.put(keyword, Boolean.TRUE);
                    continue kl;
                }
                if (keywordStrings.contains("-" + keyword.getName(lang))) {
                    keywords.put(keyword, Boolean.FALSE);
                    continue kl;
                }
            }
        }
        return keywords;
    }

    @Override
    public Keyword getByName(String name) {
        return query().filter(Filters.or(Filters.eq("names.de", name), Filters.eq("names.en", name))).first();
    }
}
