package com.loopers.domain.order;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.Objects;

@Embeddable
@Getter
public class Option {

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private String size;

    protected Option() {
    }

    private Option(String color, String size) {
        this.color = color;
        this.size = size;
    }

    public static Option of(String color, String size) {
        return new Option(color, size);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Option option)) return false;
        return Objects.equals(color, option.color) && Objects.equals(size, option.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, size);
    }
}
