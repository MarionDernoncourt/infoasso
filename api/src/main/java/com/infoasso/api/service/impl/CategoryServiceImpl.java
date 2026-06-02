package com.infoasso.api.service.impl;

import com.infoasso.api.dto.category.CategoryCreateDto;
import com.infoasso.api.dto.category.CategoryReadDto;
import com.infoasso.api.exceptions.ResourceNotFoundException;
import com.infoasso.api.model.Category;
import com.infoasso.api.repository.CategoryRepository;
import com.infoasso.api.service.ICategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements ICategoryService {

    private final static Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryReadDto> findAll() {
        logger.info("Searching for all categories");
        List<Category> categories = categoryRepository.findAll();
        logger.info("Found " + categories.size() + " categories");
        return categories.stream().map(this::mapToDto).toList();
    }

    @Override
    public CategoryReadDto findById(Long id) {
        logger.info("Searching for category with id " + id);
        Category category = categoryRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Category", id));
        logger.info("Found category with id " + id);
        return mapToDto(category);
    }

    @Override
    @Transactional
    public CategoryReadDto createCategory(CategoryCreateDto category) {
        logger.info("Trying to create or find Category : {} ({})", category.getLabel(), category.getType());
        Category newCategory = categoryRepository.findByLabelIgnoreCaseAndType(category.getLabel(), category.getType())
                .orElseGet(() -> {
                    logger.info("Creating new category: {} of type {}", category.getLabel(), category.getType());
                    Category newCat = new Category();
                    newCat.setLabel(category.getLabel());
                    newCat.setType(category.getType());
                    return categoryRepository.save(newCat);
                });
        return mapToDto(newCategory);
    }


    private CategoryReadDto mapToDto(Category category) {
        CategoryReadDto dto = new CategoryReadDto();
        dto.setId(category.getId());
        dto.setLabel(category.getLabel());
        dto.setType(category.getType());
        return dto;
    }


}
