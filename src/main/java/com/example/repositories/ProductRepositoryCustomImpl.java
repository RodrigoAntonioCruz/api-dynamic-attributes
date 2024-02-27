package com.example.repositories;

import com.example.domains.Product;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import static com.example.mappers.ProductMapper.INSTANCE;
import static com.example.utils.Constants.BATCH_SIZE;
import static com.example.utils.Constants.KEY_ATTRIBUTES;
import static com.example.utils.Constants.KEY_VALUE;
import static com.example.utils.Constants.KEY_NAME;
import static com.example.utils.Constants.OPTION_I;
import static com.example.utils.Constants.KEY_ID;
import static com.example.utils.Constants.KEY_DESCRIPTION;
import static com.example.utils.Constants.KEY_CREATED_AT;
import static com.example.utils.Constants.KEY_UPDATED_AT;
import static com.example.utils.Constants.ONE;

import java.util.ArrayList;
import java.util.List;

@Repository
@AllArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<Product> findByKeyword(String keyword, Pageable pageable) {
        Criteria criteria = new Criteria();
        List<Criteria> attributes = new ArrayList<>();

        if (INSTANCE.isInteger(keyword)) {
            attributes.add(
                    Criteria.where(KEY_ATTRIBUTES).elemMatch(
                            Criteria.where(KEY_VALUE).is(Integer.valueOf(keyword)
                            )
                    )
            );
        } else if (INSTANCE.isDouble(keyword)) {
            attributes.add(
                    Criteria.where(KEY_ATTRIBUTES).elemMatch(
                            Criteria.where(KEY_VALUE).is(Double.valueOf(keyword)
                            )
                    )
            );
        } else {
            attributes.add(
                    Criteria.where(KEY_ATTRIBUTES).elemMatch(
                            Criteria.where(KEY_VALUE).elemMatch(
                                    Criteria.where(KEY_NAME).regex(keyword, OPTION_I)
                            )
                    )
            );
            attributes.add(
                    Criteria.where(KEY_ATTRIBUTES).elemMatch(
                            Criteria.where(KEY_VALUE).regex(keyword, OPTION_I)
                    )
            );
        }

        attributes.add(
                Criteria.where(KEY_ID).is(keyword)
        );
        attributes.add(
                Criteria.where(KEY_DESCRIPTION).regex(keyword, OPTION_I)
        );

        if (INSTANCE.isDate(keyword)) {
            LocalDate parsedDate = INSTANCE.parseDate(keyword);
            attributes.add(new Criteria().orOperator(
                    Criteria.where(KEY_CREATED_AT).gte(
                            parsedDate.atStartOfDay()).lt(parsedDate.plusDays(ONE).atStartOfDay()
                    ),
                    Criteria.where(KEY_UPDATED_AT).gte(
                            parsedDate.atStartOfDay()).lt(parsedDate.plusDays(ONE).atStartOfDay()
                    )
            ));
        }

        criteria.orOperator(attributes.toArray(new Criteria[0]));
        var page = mongoTemplate.find(new Query(criteria).with(pageable).cursorBatchSize(BATCH_SIZE), Product.class);
        return new PageImpl<>(page, pageable, page.size());
    }
}
