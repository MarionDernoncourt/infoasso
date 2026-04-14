package com.infoasso.api.controller;

import com.infoasso.api.dto.category.CategoryCreateDto;
import com.infoasso.api.dto.category.CategoryReadDto;
import com.infoasso.api.service.ICategoryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/category")
public class CategoryController {

    private final static Logger logger = LoggerFactory.getLogger(CategoryController.class);

    private final ICategoryService categoryService;

    public CategoryController(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("")
    public ResponseEntity<List<CategoryReadDto>> getAllCategories(){
        logger.info("GET / : Request received for all categories");
        List<CategoryReadDto> categories = categoryService.findAll();
        logger.info("GET / : Response 200 OK : Number of categories : " + categories.size());
        return ResponseEntity.status(HttpStatus.OK).body(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryReadDto> getCategory(@PathVariable Long id){
        logger.info("GET / : Request received for category with id : " + id);
        CategoryReadDto category = categoryService.findById(id);
        logger.info("GET / : Response 200 OK : Category : " + category);
        return ResponseEntity.status(HttpStatus.OK).body(category);
    }

    @PostMapping("")
    public ResponseEntity<CategoryReadDto> createCategory(@Valid @RequestBody CategoryCreateDto category){
        logger.info("POST / : Request received for category : " + category.getType());
        CategoryReadDto categoryCreated = categoryService.createCategory(category);
        logger.info("POST / : Response 201 CREATED : Category : " + categoryCreated);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryCreated);
    }

}
