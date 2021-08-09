package com.myapp.repository.rowmapper;

import com.myapp.domain.D;
import com.myapp.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link D}, with proper type conversions.
 */
@Service
public class DRowMapper implements BiFunction<Row, String, D> {

    private final ColumnConverter converter;

    public DRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link D} stored in the database.
     */
    @Override
    public D apply(Row row, String prefix) {
        D entity = new D();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        return entity;
    }
}
