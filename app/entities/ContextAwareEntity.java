package entities;

import utils.Context;

public interface ContextAwareEntity {
    void inject(Context context);
}
