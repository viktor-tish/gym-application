import re

with open("src/test/java/org/vttish/gymcrmsystem/persistence/dao/impl/InMemoryTrainingDaoTest.java", "r") as f:
    content = f.read()

# Add imports
content = content.replace("import static org.junit.jupiter.api.Assertions.assertEquals;", "import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;\nimport static org.junit.jupiter.api.Assertions.assertEquals;\nimport static org.junit.jupiter.api.Assertions.assertFalse;")

# Add methods before the last closing brace
methods = """
  @Test
  public void delete_shouldRemoveTraining_whenExists() {
    testStorage.put(testTraining.getId(), testTraining);

    trainingDao.delete(testTraining.getId());

    assertFalse(testStorage.containsKey(testTraining.getId()));
    assertEquals(0, testStorage.size());
  }

  @Test
  public void delete_shouldNotFail_whenDoesNotExist() {
    assertDoesNotThrow(() -> trainingDao.delete(UUID.randomUUID()));
  }

  @Test
  public void existsById_shouldReturnTrue_whenIdMatches() {
    testStorage.put(testTraining.getId(), testTraining);

    boolean exists = trainingDao.existsById(testTraining.getId());

    assertTrue(exists);
  }

  @Test
  public void existsById_shouldReturnFalse_whenIdDoesNotMatch() {
    testStorage.put(testTraining.getId(), testTraining);

    boolean exists = trainingDao.existsById(UUID.randomUUID());

    assertFalse(exists);
  }
}
"""

content = re.sub(r'}\s*$', methods, content)

with open("src/test/java/org/vttish/gymcrmsystem/persistence/dao/impl/InMemoryTrainingDaoTest.java", "w") as f:
    f.write(content)
