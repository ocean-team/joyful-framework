package org.joyful4j.persistence.entity;

import java.io.Serializable;

/**
 * Created by richey on 17-1-7.
 */
public interface Entity<ID extends Serializable> extends Serializable {
    ID getId();

    void setId(ID id);

    boolean isPersisted();

    boolean isTransient();
}
