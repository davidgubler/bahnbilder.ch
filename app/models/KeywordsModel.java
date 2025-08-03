package models;

import entities.Keyword;
import entities.formdata.KeywordFormData;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public interface KeywordsModel {
    void clear();

    Keyword create(int id, String nameDe, String nameEn, List<String> labels);

    Keyword create(KeywordFormData data, List<String> labels);

    void update(KeywordFormData data, List<String> labels);

    Keyword get(Integer id);

    Map<Keyword, Boolean> getKeywordsMap(List<String> keywordStrings);

    Stream<? extends Keyword> getAll();

    Keyword getByName(String name);
}
