package CSCI_841_Project.backend.service.implement;


import CSCI_841_Project.backend.dto.CategoryDTO;
import CSCI_841_Project.backend.entity.Category;
import CSCI_841_Project.backend.entity.User;
import CSCI_841_Project.backend.enums.CategoryType;
import CSCI_841_Project.backend.exception.NotFoundException;
import CSCI_841_Project.backend.mapper.CategoryMapper;
import CSCI_841_Project.backend.repository.CategoryRepository;
import CSCI_841_Project.backend.repository.UserRepository;
import CSCI_841_Project.backend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImplementation implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        User user = null; // ✅ Default to null in case it's a system category

        if (categoryDTO.getUserId() != null) {
            user = userRepository.findById(categoryDTO.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

        // ✅ Pass all required arguments to `toEntity()`
        Category category = categoryMapper.toEntity(categoryDTO, user);

        return categoryMapper.toDTO(categoryRepository.save(category));
    }

    @Override
    public CategoryDTO getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .filter(a -> !a.isDeleted())
                .orElseThrow(() -> new NotFoundException("Category with ID "  + categoryId + " Not Found"));
        return categoryMapper.toDTO(category);
    }

    @Override
    public List<CategoryDTO> getCategoriesByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));

        List<Category> categories = categoryRepository.findByUser_UserIdAndIsDeletedFalse(userId);

        if (categories.isEmpty()) {
            throw new NotFoundException("No categories found for user with ID " + userId);
        }

        return categories.stream().map(categoryMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findByIsDeletedFalse()
                .stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        existingCategory.setName(categoryDTO.getName());
        existingCategory.setType(CategoryType.valueOf(categoryDTO.getType()));
        existingCategory.setIcon(categoryDTO.getIcon());
        existingCategory.setColorCode(categoryDTO.getColorCode());

        return categoryMapper.toDTO(categoryRepository.save(existingCategory));
    }

    @Override
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setDeleted(true);
        categoryRepository.save(category);
    }

    @Override
    public void restoreCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        if (!category.isDeleted()) {
            throw new IllegalStateException("Category is already active.");
        }
        category.setDeleted(false);
        categoryRepository.save(category);
    }
}
