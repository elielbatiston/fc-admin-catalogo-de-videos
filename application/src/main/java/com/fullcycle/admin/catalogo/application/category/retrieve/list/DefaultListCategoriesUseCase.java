package com.fullcycle.admin.catalogo.application.category.retrieve.list;

import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.pagination.SearchQuery;

public class DefaultListCategoriesUseCase extends ListCategoriesUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultListCategoriesUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = categoryGateway;
    }

    @Override
    public Pagination<CategoryListOutput> execute(final SearchQuery aQuery) {
        return this.categoryGateway.findAll(aQuery)
            .map(CategoryListOutput::from);
    }
}
