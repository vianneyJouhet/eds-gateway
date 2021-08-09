package com.myapp.repository.rowmapper;

import com.myapp.domain.A;
import com.myapp.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link A}, with proper type conversions.
 */
@Service
public class ARowMapper implements BiFunction<Row, String, A> {

    private final ColumnConverter converter;

    public ARowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link A} stored in the database.
     */
    @Override
    public A apply(Row row, String prefix) {
        A entity = new A();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        return entity;
    }
}
