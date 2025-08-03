package biz;

import entities.User;
import entities.formdata.FormData;
import utils.Context;

public interface CUDBusinessLogic<T extends FormData> {
    void create(Context context, T data, User user) throws ValidationException;
    void update(Context context, T data, User user) throws ValidationException;
    void delete(Context context, T data, User user) throws ValidationException;
}
