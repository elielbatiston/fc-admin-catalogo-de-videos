package com.fullcycle.admin.catalogo.application.category.update;

import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalAnswers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateCategoryUseCaseTest {

    @InjectMocks
    private DefaultUpdateCategoryUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    @BeforeEach
    void cleanUp() {
        Mockito.reset(categoryGateway);
    }

    @Test
    public void givenAValidCommand_whenCallsUpdateCategory_shouldReturnCategoryId() {
        final var aCategory = Category.newCategory(
            "Film",
            null,
            true
        );

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedId = aCategory.getId();

        final var aCommand = UpdateCategoryCommand.with(
            expectedId.getValue(),
            expectedName,
            expectedDescription,
            expectedIsActive
        );

        when(categoryGateway.findById(eq(expectedId))).thenReturn(Optional.of(aCategory.clone()));
        when(categoryGateway.update(any())).thenAnswer(returnsFirstArg());

        final var actualOutput = useCase.execute(aCommand).get();

        assertNotNull(actualOutput);
        assertNotNull(actualOutput.id());

        Mockito.verify(categoryGateway, times(1)).findById(eq(expectedId));
        Mockito.verify(categoryGateway, times(1)).update(argThat(
            aUpdateCategory -> Objects.equals(expectedName, aUpdateCategory.getName())
                && Objects.equals(expectedDescription, aUpdateCategory.getDescription())
                && Objects.equals(expectedIsActive, aUpdateCategory.isActive())
                && Objects.equals(expectedId, aUpdateCategory.getId())
                && Objects.equals(aCategory.getCreatedAt(), aUpdateCategory.getCreatedAt())
                && aCategory.getUpdatedAt().isBefore(aUpdateCategory.getUpdatedAt())
                && Objects.isNull(aUpdateCategory.getDeletedAt()))
        );
    }

    @Test
    public void givenAInvalidName_whenCallUpdateCategory_thenShouldReturnDomainException() {
        final var aCategory = Category.newCategory(
            "Film",
            null,
            true
        );

        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedId = aCategory.getId();

        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;

        final var aCommand = UpdateCategoryCommand.with(
            expectedId.getValue(),
            expectedName,
            expectedDescription,
            expectedIsActive
        );

        when(categoryGateway.findById(eq(expectedId))).thenReturn(Optional.of(aCategory.clone()));

        final var notification = useCase.execute(aCommand).getLeft();

        assertEquals(expectedErrorCount, notification.getErrors().size());
        assertEquals(expectedErrorMessage, notification.firstError().message());

        verify(categoryGateway, times(0)).create(any());
    }

    @Test
    public void givenAValidCommandWithInactiveCategory_whenCallsUpdateCategory_shouldReturnInactiveCategoryId() {
        final var aCategory = Category.newCategory(
            "Film",
            null,
            true
        );

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;
        final var expectedId = aCategory.getId();

        final var aCommand = UpdateCategoryCommand.with(
            expectedId.getValue(),
            expectedName,
            expectedDescription,
            expectedIsActive
        );

        when(categoryGateway.findById(eq(expectedId))).thenReturn(Optional.of(aCategory.clone()));
        when(categoryGateway.update(any())).thenAnswer(returnsFirstArg());

        assertTrue(aCategory.isActive());
        assertNull(aCategory.getDeletedAt());

        final var actualOutput = useCase.execute(aCommand).get();

        assertNotNull(actualOutput);
        assertNotNull(actualOutput.id());

        verify(categoryGateway, times(1)).update(argThat(
            aUpdateCategory -> Objects.equals(expectedName, aUpdateCategory.getName())
                && Objects.equals(expectedDescription, aUpdateCategory.getDescription())
                && Objects.equals(expectedIsActive, aUpdateCategory.isActive())
                && Objects.equals(expectedId, aUpdateCategory.getId())
                && Objects.equals(aCategory.getCreatedAt(), aUpdateCategory.getCreatedAt())
                && aCategory.getUpdatedAt().isBefore(aUpdateCategory.getUpdatedAt())
                && Objects.nonNull(aUpdateCategory.getDeletedAt()))
        );
    }

    @Test
    public void givenAValidCommand_whenCallsGatwwayThrowsRandomException_shouldReturnAException() {
        final var aCategory = Category.newCategory(
            "Film",
            null,
            true
        );

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedId = aCategory.getId();

        final var expectedErrorMessage = "Gateway error";
        final var expectedErrorCount = 1;

        final var aCommand = UpdateCategoryCommand.with(
            expectedId.getValue(),
            expectedName,
            expectedDescription,
            expectedIsActive
        );

        when(categoryGateway.findById(eq(expectedId))).thenReturn(Optional.of(aCategory.clone()));
        when(categoryGateway.update(any())).thenThrow(new IllegalStateException(expectedErrorMessage));

        final var notification = useCase.execute(aCommand).getLeft();

        assertEquals(expectedErrorCount, notification.getErrors().size());
        assertEquals(expectedErrorMessage, notification.firstError().message());

        verify(categoryGateway, times(1)).update(argThat(
            aUpdateCategory -> Objects.equals(expectedName, aUpdateCategory.getName())
                && Objects.equals(expectedDescription, aUpdateCategory.getDescription())
                && Objects.equals(expectedIsActive, aUpdateCategory.isActive())
                && Objects.equals(expectedId, aUpdateCategory.getId())
                && Objects.equals(aCategory.getCreatedAt(), aUpdateCategory.getCreatedAt())
                && aCategory.getUpdatedAt().isBefore(aUpdateCategory.getUpdatedAt())
                && Objects.isNull(aUpdateCategory.getDeletedAt()))
        );
    }

    @Test
    public void givenACommandWithInvalidCategoryId_whenCallsUpdateCategory_shouldReturnNotFoundException() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;
        final var expectedId = "123";
        final var expectedErrorCount = 1;
        final var expectedMessage = "Category with ID 123 was not found";

        final var aCommand = UpdateCategoryCommand.with(
            expectedId,
            expectedName,
            expectedDescription,
            expectedIsActive
        );

        when(categoryGateway.findById(eq(CategoryID.from(expectedId)))).thenReturn(Optional.empty());

        final var actualException = assertThrows(DomainException.class, () -> useCase.execute(aCommand));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedMessage, actualException.getErrors().get(0).message());

        verify(categoryGateway, times(1)).findById(eq(CategoryID.from(expectedId)));

        verify(categoryGateway, times(0)).update(any());
    }
}
