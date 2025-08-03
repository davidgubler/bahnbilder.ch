package models.context;

import com.google.inject.Inject;
import entities.Keyword;
import entities.formdata.KeywordFormData;
import models.KeywordsModel;
import utils.Context;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ContextKeywordsModel extends ContextModel implements KeywordsModel {

    @Inject
    private KeywordsModel keywordsModel;

    public ContextKeywordsModel(Context context) {
        this.context = context;
    }

    @Override
    public void clear() {
        call(() -> { keywordsModel.clear(); return null; });
    }

    @Override
    public Keyword create(int id, String nameDe, String nameEn, List<String> labels) {
        return call(() -> keywordsModel.create(id, nameDe, nameEn, labels));
    }

    @Override
    public Keyword create(KeywordFormData data, List<String> labels) {
        return call(() -> keywordsModel.create(data, labels));
    }

    @Override
    public void update(KeywordFormData data, List<String> labels) {
        call(() -> { keywordsModel.update(data, labels); return null; });
    }

    @Override
    public Keyword get(Integer id) {
        return call(() -> keywordsModel.get(id));
    }

    @Override
    public Map<Keyword, Boolean> getKeywordsMap(List<String> keywordStrings) {
        return call(() -> keywordsModel.getKeywordsMap(keywordStrings));
    }

    @Override
    public Stream<? extends Keyword> getAll() {
        return call(() -> keywordsModel.getAll());
    }

    @Override
    public Keyword getByName(String name) {
        return call(() -> keywordsModel.getByName(name));
    }
}
