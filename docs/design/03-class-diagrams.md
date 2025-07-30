## 클래스 다이어그램

```mermaid
classDiagram

class Product {
  +Brand brand
  +String name
  +Long price
  +int stock
  +int likeCount
  +ProductStatus status

  +increaseStock(int quantity) void
  +decreaseStock(int quantity) void
  +increaseLikeCount() void
  +decreaseLikeCount() void
  +isAvailable() boolean
}

class Option {
  +String color
  +String size
  +int additionalPrice
  +Product product

  +changeAdditionalPrice(int newPrice) void
  +updateOption(String color, String size, int additionalPrice) void
}

class Brand {
  +String name
  +Boolean isActive

  +deactivate() void
  +activate() void
}

class Category {
  +String name

  +updateName(String name) void
}

class ProductCategory {
  +Product product
  +Category category

  +of(Product product, Category category) ProductCategory
}

class BrandCategory {
  +Brand brand
  +Category category

  +of(Brand brand, Category category) BrandCategory
}

class Like {
  +User user
  +Product product
  +LocalDateTime createdAt

  +of(User user, Product product) Like
}

class Order {
  +User user
  +LocalDateTime orderedAt
  +String shippingAddress
  +int totalAmount

  +addOrderItem(OrderItem orderItem) void
  +cancel() void
  +calculateTotalAmount() int
}

class OrderItem {
  +Order order
  +Product product
  +Option option
  +int quantity
  +int price

  +calculatePrice() int
  +changeQuantity(int quantity) void
}

class User {
  +String userId
  +String name
  +Gender gender
  +LocalDate birth
  +String email
  +String password

  +changePassword(String newPassword) void
  +updateProfile(String name, Gender gender, LocalDate birth, String email) void
}

class BaseEntity {
  +Long id
  +ZonedDateTime createdAt
  +ZonedDateTime updatedAt
  +ZonedDateTime deletedAt
  +delete() void
  +restore() void
  +guard() void
}

%% 상속 관계
Product --|> BaseEntity
Option --|> BaseEntity
Brand --|> BaseEntity
Category --|> BaseEntity
ProductCategory --|> BaseEntity
BrandCategory --|> BaseEntity
Like --|> BaseEntity
Order --|> BaseEntity
OrderItem --|> BaseEntity
User --|> BaseEntity

%% 연관 관계
Option --> Product : product
Product --> Brand : brand
ProductCategory --> Product : product
ProductCategory --> Category : category
BrandCategory --> Brand : brand
BrandCategory --> Category : category
Like --> User : user
Like --> Product : product
Order --> User : user
OrderItem --> Order : order
OrderItem --> Product : product
OrderItem --> Option : option

```
