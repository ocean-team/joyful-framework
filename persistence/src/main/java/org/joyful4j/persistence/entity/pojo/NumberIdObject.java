package org.joyful4j.persistence.entity.pojo;

import org.joyful4j.persistence.entity.Entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Created by richey on 17-1-7.
 */
@MappedSuperclass
public abstract class NumberIdObject<T extends Number> implements Entity<T> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected T id;

    public NumberIdObject() {
    }

    public NumberIdObject(T id) {
        this.id = id;
    }

    @Override
    public T getId() {
        return id;
    }

    @Override
    public void setId(T id) {
        this.id = id;
    }

    @Override
    public boolean isPersisted() {
        //TODO
        return false;
    }

    @Override
    public boolean isTransient() {
        //TODO
        return false;
    }

    @Override
    public boolean equals(Object object) {
        if(this == object) {
            return true;
        } else if(!(object instanceof NumberIdObject)) {
            return false;
        } else {
            NumberIdObject rhs = (NumberIdObject)object;
            return null != this.getId() && null != rhs.getId()?this.getId().equals(rhs.getId()):false;
        }
    }
}
