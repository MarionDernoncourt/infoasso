package com.infoasso.api.service;

import com.infoasso.api.controller.CategoryController;
import com.infoasso.api.dto.category.CategoryCreateDto;
import com.infoasso.api.dto.category.CategoryReadDto;
import com.infoasso.api.exceptions.ResourceAlreadyExistsException;
import com.infoasso.api.exceptions.RessourceNotFoundException;
import com.infoasso.api.model.Category;
import com.infoasso.api.model.CategoryType;
import com.infoasso.api.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
public class CategoryServiceIT {

    @Autowired
    private CategoryServiceImpl categoryService;
    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;

    @BeforeEach
    public void setUp() {
        category = new Category();
        category.setLabel("football");
        category.setType(CategoryType.SPORT);

        categoryRepository.save(category);

    }

    @Test
    public void findAll_whenSuccess() {
        List<CategoryReadDto> categories = categoryService.findAll();

        assertEquals(1, categories.size());
    }

    @Test
    public void findById_whenSuccess() {
        Long id = category.getId();
        CategoryReadDto categoryReadDto = categoryService.findById(id);

        assertEquals("football", categoryReadDto.getLabel());
        assertEquals(CategoryType.SPORT, categoryReadDto.getType());
    }

    @Test
    public void findById_whenNotFound() {
        assertThrows(RessourceNotFoundException.class, () -> categoryService.findById(999L));
    }

    @Test
    public void createCategory_whenSuccess() {
        CategoryCreateDto categoryToCreate = new CategoryCreateDto();
        categoryToCreate.setLabel("piano");
        categoryToCreate.setType(CategoryType.MUSIQUE);

        CategoryReadDto createdCategory = categoryService.createCategory(categoryToCreate);

        List<Category> categories = categoryRepository.findAll();

        assertEquals(2, categories.size());
        assertEquals(categoryToCreate.getLabel(), createdCategory.getLabel());
    }

    @Test
    public void createCategory_shouldNotCreateDuplicate_whenAlreadyExists() {
        CategoryCreateDto duplicateDto = new CategoryCreateDto();
        duplicateDto.setLabel("football");
        duplicateDto.setType(CategoryType.SPORT);

        CategoryReadDto result = categoryService.createCategory(duplicateDto);

        List<Category> allCategories = categoryRepository.findAll();

        assertEquals(1, allCategories.size());
        assertEquals(category.getId(), result.getId());
    }

    @Test
    public void createCategory_shouldBeCaseInsensitive() {
        CategoryCreateDto caseDto = new CategoryCreateDto();
        caseDto.setLabel("FOOTBALL");
        caseDto.setType(CategoryType.SPORT);

        categoryService.createCategory(caseDto);

        assertEquals(1, categoryRepository.findAll().size());
    }


}
