package com.myapp.repository.rowmapper;

import com.myapp.domain.C;
import com.myapp.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link C}, with proper type conversions.
 */
@Service
public class CRowMapper implements BiFunction<Row, String, C> {

    private final ColumnConverter converter;

    public CRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link C} stored in the database.
     */
    @Override
    public C apply(Row row, String prefix) {
        C entity = new C();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        return entity;
    }
}
