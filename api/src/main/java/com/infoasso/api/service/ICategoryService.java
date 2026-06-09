package com.infoasso.api.service;

import com.infoasso.api.dto.category.CategoryCreateDto;
import com.infoasso.api.dto.category.CategoryReadDto;
import com.infoasso.api.model.CategoryType;

import java.util.List;

public interface ICategoryService  {
    List<CategoryReadDto> findAll();

    CategoryReadDto findById(Long id);

    CategoryReadDto createCategory(CategoryCreateDto category);

    List<CategoryReadDto> searchByLabelAndType(CategoryType type, String query);}
