# financial-manager

Задачи:

1. Сделать persistence слой для account +
2. Поправить работу с id при создании +
3. При сохранении добавить обработку ошибок +
4. Написать не достающие тесты
   4.1. domain +
   4.2. use case +
   4.3. repository +
   4.4. rest +
5. Сделать авторизацию, убрать userId из всех запросов
6. Поправить warning
7. Поправить работу с BigDecimal для route
8. UI
9. Переработка ошибок при сохранении в монгу, нужно посмотреть что методы возвращают

Рефакторинг:

1. Работу с сохранением и обновлением заменить на доменные события
2. Переработать работу с исключенями, добавить возможность писать сообщения
3. В тестах перейти с моков на тестовые реализации интерфейсов