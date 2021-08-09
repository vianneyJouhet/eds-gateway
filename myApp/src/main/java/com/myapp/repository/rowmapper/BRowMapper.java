package com.myapp.repository.rowmapper;

import com.myapp.domain.B;
import com.myapp.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link B}, with proper type conversions.
 */
@Service
public class BRowMapper implements BiFunction<Row, String, B> {

    private final ColumnConverter converter;

    public BRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link B} stored in the database.
     */
    @Override
    public B apply(Row row, String prefix) {
        B entity = new B();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setAId(converter.fromRow(row, prefix + "_aa_id", Long.class));
        return entity;
    }
}
