import re

with open("src/test/java/org/vttish/gymcrmsystem/persistence/dao/impl/InMemoryTrainerDaoTest.java", "r") as f:
    content = f.read()

# Add import
content = content.replace("import static org.junit.jupiter.api.Assertions.assertEquals;", "import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;\nimport static org.junit.jupiter.api.Assertions.assertEquals;")

# Add methods before the last closing brace
methods = """
  @Test
  public void delete_shouldRemoveTrainer_whenExists() {
    testStorage.put(testTrainer.getUserId(), testTrainer);

    trainerDao.delete(testTrainer.getUserId());

    assertFalse(testStorage.containsKey(testTrainer.getUserId()));
    assertEquals(0, testStorage.size());
  }

  @Test
  public void delete_shouldNotFail_whenDoesNotExist() {
    assertDoesNotThrow(() -> trainerDao.delete(UUID.randomUUID()));
  }
}
"""

content = re.sub(r'}\s*$', methods, content)

with open("src/test/java/org/vttish/gymcrmsystem/persistence/dao/impl/InMemoryTrainerDaoTest.java", "w") as f:
    f.write(content)
