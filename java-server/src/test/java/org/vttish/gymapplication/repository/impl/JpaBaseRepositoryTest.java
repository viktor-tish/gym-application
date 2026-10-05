package org.vttish.gymapplication.repository.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.vttish.gymapplication.entity.BaseEntity;

@ExtendWith(MockitoExtension.class)
class JpaBaseRepositoryTest {

  @Mock
  private EntityManager entityManager;

  @Mock
  private TypedQuery<DummyEntity> queryMock;

  @Mock
  private TypedQuery<Long> countQueryMock;

  static class DummyEntity extends BaseEntity {
  }

  static class DummyRepository extends JpaBaseRepository<DummyEntity> {
    public DummyRepository() {
      super(DummyEntity.class);
    }
  }

  private DummyRepository dummyRepository;

  @BeforeEach
  void setUp() {
    dummyRepository = new DummyRepository();
    ReflectionTestUtils.setField(dummyRepository, "entityManager", entityManager);
  }

  @Test
  void save_WhenIdIsNull_ShouldCallPersist() {
    DummyEntity entity = new DummyEntity();

    DummyEntity result = dummyRepository.save(entity);

    verify(entityManager, times(1)).persist(entity);
    verify(entityManager, never()).merge(any());
    assertSame(entity, result);
  }

  @Test
  void save_WhenIdIsNotNull_ShouldCallMerge() {
    DummyEntity entity = new DummyEntity();
    ReflectionTestUtils.setField(entity, "id", UUID.randomUUID());
    when(entityManager.merge(entity)).thenReturn(entity);

    DummyEntity result = dummyRepository.save(entity);

    verify(entityManager, never()).persist(any());
    verify(entityManager, times(1)).merge(entity);
    assertSame(entity, result);
  }

  @Test
  void findById_WhenIdIsNull_ShouldReturnEmpty() {
    Optional<DummyEntity> result = dummyRepository.findById(null);

    assertTrue(result.isEmpty());
    verify(entityManager, never()).find(any(), any());
  }

  @Test
  void findById_WhenIdExists_ShouldReturnEntity() {
    UUID id = UUID.randomUUID();
    DummyEntity entity = new DummyEntity();
    when(entityManager.find(DummyEntity.class, id)).thenReturn(entity);

    Optional<DummyEntity> result = dummyRepository.findById(id);

    assertTrue(result.isPresent());
    assertSame(entity, result.get());
  }

  @Test
  void findById_WhenIdDoesNotExist_ShouldReturnEmpty() {
    UUID id = UUID.randomUUID();
    when(entityManager.find(DummyEntity.class, id)).thenReturn(null);

    Optional<DummyEntity> result = dummyRepository.findById(id);

    assertTrue(result.isEmpty());
  }

  @Test
  void findAll_ShouldReturnListOfEntities() {
    List<DummyEntity> expectedList = List.of(new DummyEntity(), new DummyEntity());
    when(entityManager.createQuery(anyString(), eq(DummyEntity.class))).thenReturn(queryMock);
    when(queryMock.getResultList()).thenReturn(expectedList);

    List<DummyEntity> result = dummyRepository.findAll();

    assertEquals(2, result.size());
    assertSame(expectedList, result);
    verify(entityManager).createQuery("SELECT e FROM DummyEntity e", DummyEntity.class);
  }

  @Test
  void existsById_WhenIdIsNull_ShouldReturnFalse() {
    boolean exists = dummyRepository.existsById(null);

    assertFalse(exists);
    verify(entityManager, never()).createQuery(anyString(), any());
  }

  @Test
  void existsById_WhenCountIsGreaterThanZero_ShouldReturnTrue() {
    UUID id = UUID.randomUUID();
    when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(countQueryMock);
    when(countQueryMock.setParameter("id", id)).thenReturn(countQueryMock);
    when(countQueryMock.getSingleResult()).thenReturn(1L);

    boolean exists = dummyRepository.existsById(id);

    assertTrue(exists);
    verify(entityManager).createQuery(
        "SELECT COUNT(e) FROM DummyEntity e WHERE e.id = :id",
        Long.class
    );
  }

  @Test
  void existsById_WhenCountIsZero_ShouldReturnFalse() {
    UUID id = UUID.randomUUID();
    when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(countQueryMock);
    when(countQueryMock.setParameter("id", id)).thenReturn(countQueryMock);
    when(countQueryMock.getSingleResult()).thenReturn(0L);

    boolean exists = dummyRepository.existsById(id);

    assertFalse(exists);
  }

  @Test
  void delete_WhenEntityExists_ShouldCallRemove() {
    UUID id = UUID.randomUUID();
    DummyEntity entity = new DummyEntity();
    when(entityManager.find(DummyEntity.class, id)).thenReturn(entity);

    dummyRepository.delete(id);

    verify(entityManager, times(1)).remove(entity);
  }

  @Test
  void delete_WhenEntityDoesNotExist_ShouldNotCallRemove() {
    UUID id = UUID.randomUUID();
    when(entityManager.find(DummyEntity.class, id)).thenReturn(null);

    dummyRepository.delete(id);

    verify(entityManager, never()).remove(any());
  }

  @Test
  void delete_WhenIdIsNull_ShouldNotCallRemove() {
    dummyRepository.delete(null);

    verify(entityManager, never()).remove(any());
  }
}
