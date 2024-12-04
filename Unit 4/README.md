## Introduction

The main task of the fourth unit assignments is to complete a library simulation system, aiming to train the design and abstraction skills of program architecture.

Click here to refer to my code: [https://github.com/LeostarR/Object-oriented-Design-and-Construction](https://github.com/LeostarR/Object-oriented-Design-and-Construction)

## Forward Modeling and Development Practices in This Unit

### Requirements Analysis

In the process of forward modeling and development, requirements analysis needs to be conducted first. According to the guidelines, we need to accomplish the following tasks:

- Choose an appropriate way to represent books and provide suitable containers. In the first assignment, since inter-school borrowing was not involved, the situation was relatively simple; therefore, I directly used strings (book names) to represent books, and all containers adopted the form of `HashMap<String, Integer>` to represent corresponding books and their quantities. However, in subsequent assignments, inter-school borrowing functionality was added, meaning each book has its source school and whether it allows inter-school borrowing. Therefore, a refactoring was necessary by creating a `Book` class, initializing the source (`source`), book name (`bookName`), and permission for inter-school borrowing (`permission`) in the constructor, and configuring corresponding access methods. After refactoring, all containers storing books should be converted to `HashMap<Book, Integer>`. Notably, when using the `Book` class as a key, it's essential to ensure that the `hashCode()` and `equals()` methods have been overridden to correctly compare and look up keys in `HashMap`, ensuring identical book objects are considered equal keys. For example:

  ```java
  @Override
  public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((bookName == null) ? 0 : bookName.hashCode());
      return result;
  }

  @Override
  public boolean equals(Object obj) {
      if (this == obj) {
          return true;
      }
      if (obj == null || getClass() != obj.getClass()) {
          return false;
      }
      Book other = (Book) obj;
      return bookName.equals(other.bookName);
  }
  ```

- Create classes for each type of management personnel and provide corresponding methods for handling books, with most standard outputs completed within these classes. See the next section **Class Design**

- Handle dates. Notice that the assignment requires organizing and outputting on the morning of every organization day. Therefore, for book borrowing standard input, handle according to these steps:

  1. Determine if the date changes; if no change, continue processing current information.
  2. If the date changes, process unprocessed information from that day (unsuccessful borrowing, inter-school borrowing or school reservation).
  3. Starting from the day after the changed date, traverse all organization days before the changed date, processing in order (completing new book purchases, organizing books and outputting organization information, distributing reserved books).

     Use existing methods from `java.time.LocalDate`, for example:

  ```java
  public static HashSet<String> findArDays() {
      LocalDate initDate = LocalDate.of(2023,1,1);
      LocalDate startDate = LocalDate.of(2023, 1, 1);
      LocalDate endDate = LocalDate.of(2023, 12, 31);
      HashSet<String> datesSet = new HashSet<>();
      while (startDate.isBefore(endDate)) { //isBefore can compare dates
          if (initDate.until(startDate, ChronoUnit.DAYS) % 3 == 0) { //until returns the number of days between dates
              datesSet.add('[' + startDate.toString() + ']');
          }
          startDate = startDate.plusDays(1);
      }
      datesSet.add('[' + initDate.toString() + ']');
      return datesSet;
  }
  ```

  (A static method before the program starts to find all organization days in this year, then use `contains()` to determine if it's an organization day.)

### Class Design

#### `Student`

Each student is unique. Since different schools may have students with the same student ID, each `Student` class should contain both the school name and student name (ID). Additionally, each student needs a container for borrowed and returned books, as well as information about the borrowing date of currently owned books:

![](https://pic.superbed.cc/item/66ab3f09fcada11d3762cafe.png)

For damaged books, instead of placing damage as a field in the `Book` class, I created a `HashSet<Book>` in the `Student` class to store damaged books. Below is an example of borrowing and returning books:

```java
public void borrowB(Book book, String date) {
    this.bs.add(book);
    this.borrowDate.put(book.getName(), date);
}

public Book returnB(String bookName) {
    Book reBook = null;
    for (Book book: bs) {
        if (book.getName().equals(bookName)) {
            reBook = book;
            break;
        }
    }
    this.bs.remove(reBook);
    this.borrowDate.remove(bookName);
    return reBook;
}
```

In the main function `Main`, I used `HashMap<String, HashMap<String, Student>> students` to store all students.

#### `Lib`

Represents each school's library. Provides methods for querying books, checking if borrowing is valid (for reservations), intra-school borrowing, and inter-school borrowing.

![](https://pic.superbed.cc/item/66ab3f29fcada11d3762ce0a.png)

#### `Bar` (borrowing and returning librarian)

Similar to the Borrowing and Returning Administrator class mentioned earlier.

![](https://pic.superbed.cc/item/66ab3f49fcada11d3762cf88.png)

#### `Mace`(self-service machine)

Similar to the Borrowing and Returning Administrator class mentioned earlier.

![](https://pic.superbed.cc/item/66ab3f93fcada11d3762e56a.png)

#### `Rai`（logistics division）

Used for repairing damaged books, providing `raiLib` to store intra-school borrowed books and `outBooks` for inter-school borrowed books. Also provides methods for transporting and receiving books.

![](https://pic.superbed.cc/item/66ab3fa9fcada11d3762ecc6.png)

#### `Odr`(ordering librarian)

Records daily information for intra-school reservations (waiting for return and purchasing) through `orderList` and `buyList`, and provides a container `counter` for storing books and `buyMap` for newly purchased books. The class also offers methods for distributing books and returning them to the library.

![](https://pic.superbed.cc/item/66ab3fcefcada11d3762f4b5.png)

### Design Details and Bug Analysis

- Use generics to store all administrator objects for each school:

  ```java
  // Using TreeMap allows for alphabetical output during iteration
  TreeMap<String, ArrayList<Object>> schools = new TreeMap<>();
  // ArrayList<Object> allows for storing different types
  public static void initDepartment(ArrayList<Object> departments, String schoolName) {
      Bar bar = new Bar(schoolName); // 1->Bar BorrowingAndReturningLibrarian
      departments.add(bar);
      Odr odr = new Odr(schoolName); // 2->Odr OrderLibrarian
      departments.add(odr);
      Mace mace = new Mace(schoolName); // 3->Mace ServiceMachine
      departments.add(mace);
      Rai rai = new Rai(schoolName); // 4->Rai LogisticsDivision
      departments.add(rai);
      Arg arg = new Arg(schoolName); // 5->Arg ArrangingLibrarian
      departments.add(arg);
      Pcd pcd = new Pcd(schoolName); // 6->Pcd PurchasingDepartment
      departments.add(pcd);
  }
  ```

- Pay special attention to processing reservation information after the last message:

  ```java
  for (int i = 0; i < m; i++) { // m pieces of book borrowing and returning information
      ......
      if (i == m - 1) {
          process(proList, schools, transList, students); // Process all unsuccessful borrowing information
          transport(transList, schools, today);
      }
  }
  ```

- Note that date addition and subtraction should use the `plusDays()` method, not manually converting after incrementing `day++`, for example:

  ```java
  public static String findArrDay(String year, String month, String day) {
      return LocalDate.of(year, month, day).plusDays(1).toString();
      // Incorrect demonstration:
      //String nextDay = (Integer.parseInt(day) + 1).toString();
      //return LocalDate.of(year, month, nextDay).toString();
  }
  ```

## Tracing Relationship Between Final Code and UML Model Design

The second assignment involved refactoring due to the issue of inter-school borrowing. Each book contains more information, so merely using strings to represent them was insufficient; underlying containers and methods had to be modified. Moreover, considering multiple schools and administrators required changing the storage structure of schools (in contrast, my first assignment was a singleton pattern).

Zoom in to check details.

![](https://pic.superbed.cc/item/66ab3fedfcada11d3762fc47.png)

The final code and UML diagram align closely. From the UML class diagram, it's clear that classes were established for each library administrator, with internal methods executing corresponding functions, allowing direct calls to object methods from the main class.

## Evolution of Architectural Design Thinking Across Four Units

- First Unit (Expression Simplification): The rudiments of architectural design, introducing recursive descent parsing of expressions. We implemented specific parsing and calculation through various classes, understanding it as division of labor where each part performs vastly different yet complementary and interdependent work.
- Second Unit (Multithreading and Elevators): Initial exposure to multithreaded programming, supporting the entire module's operation through a scheduler class.
- Third Unit (JML and Specifications): Mastering programming norms, learning interfaces, inheritance, and polymorphism (such as `emojiMessage, moneyMessage`, etc.) in object-oriented implementation.
- Fourth Unit (Library Modeling via UML): Drawing UML class diagrams based on requirements and conducting forward modeling accordingly.

## Evolution of Testing Mindset Across Four Units

The first two units mainly relied on test machines provided by classmates in discussion forums and manually constructed data for testing, recognizing automated testing as an efficient testing method.

The third unit primarily relied on JUnit for unit testing, quickly checking and testing the correctness of class and method implementations through writing unit test classes and methods, effectively excluding bugs.

Testing points in the fourth unit did not deliberately increase difficulty but focused on UML diagram design, making it easy to pass tests with manually constructed data.

## Course Takeaways

- Deepened understanding of object-oriented thinking. Object-oriented emphasizes transforming concepts and behaviors from the problem domain into objects and solving problems through their interactions. This approach makes system design more modular, scalable, and maintainable. For instance, expressions in the first unit were divided into expression, term, factor classes, and numerous librarians were individually established in the fourth unit.
- Familiarity with a programming language. Java and C share many similarities, and learning object-oriented principles gradually helped me master a new language.
- Recognition of some design patterns. Patterns like Factory, Observer, and Singleton provide classic solutions to specific problems, crucial for building high-quality software systems.
- New insights into testing. Never imagine achieving success without any testing; even if the overall idea is correct, many small bugs can still appear. Therefore, proper testing is indispensable.

---



## 介绍

第四单元作业主要任务是完成一个图书馆模拟系统，以训练对程序架构的设计和抽象能力。

点击此处可以参考我的代码：https://github.com/LeostarR/Object-oriented-Design-and-Construction

## 本单元所实践的正向建模与开发

### 需求分析

在正向建模与开发的过程中，首先需要进行需求分析。根据指导书，我们需要完成这些任务：
- 选用合适的方式表示书本，并提供合适的容器。在第一次作业中，由于不涉及校际借阅，情况较为简单，因此我直接用字符串（书本名称）指代这本书，所有的容器都采用`HashMap<String, Integer>`的形式表示对应书本及其数量。但是后续作业中新增了校际借阅的功能，这意味着每一本书都有其来源学校，并且还存在是否允许借阅的权限问题。因此必须重构，对书建类`Book`类，在构造方法中提供对来源地（`source`）、书名（`bookName`）、是否允许校际借阅（`permisssion`）的初始化并配置对应访问方法，重构之后所有存储书的容器就应该转化为`HashMap<Book, Integer>`。值得注意的是，将Book类作为键时，需要确保重新实现了`hashCode()`和`equals()`方法，以便在`HashMap`中正确地进行键的比较和查找，这样可以确保相同的书对象将被视为相等的键。比如：
  ```java
  @Override
  public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((bookName == null) ? 0 : bookName.hashCode());
  	return result;
  }
  
  @Override
  public boolean equals(Object obj) {
      if (this == obj) {
  		return true;
  	}
      if (obj == null || getClass() != obj.getClass()) {
  	    return false;
      }
      Book other = (Book) obj;
      return bookName.equals(other.bookName);
  }
  ```
- 对于每种职责的管理人员需要为其建类并提供相应的方法处理书本，其中大部分标准输出在这些类里完成。请看下一小节 **类设计**
- 处理日期。注意到作业要求整理日早晨必须整理并输出，因此对于图书借阅的标准输入要按照这样的步骤来处理：
  1. 判断是否日期发生变化，若没有发生变化，继续处理当前信息
  2. 日期发生变化，处理当天没有成功处理的信息（没有成功借书，走校际借阅or校内预定（等书或者购买））
  3. 从变化前日期后一天开始，遍历所有在变化后日期之前的整理日，按顺序（完成新书购置，整理图书并输出整理信息，发放预定图书）处理
  其中日期可以调用`java.time.LocalDate`现有的方法，比如：
  ```java
  public static HashSet<String> findArDays() {
      LocalDate initDate = LocalDate.of(2023,1,1);
      LocalDate startDate = LocalDate.of(2023, 1, 1);
      LocalDate endDate = LocalDate.of(2023, 12, 31);
      HashSet<String> datesSet = new HashSet<>();
      while (startDate.isBefore(endDate)) { //isBefore可以比较日期前后
          if (initDate.until(startDate, ChronoUnit.DAYS) % 3 == 0) { //until返回日期间隔天数
  			datesSet.add('[' + startDate.toString() + ']');
          }
          startDate = startDate.plusDays(1);
      }
      datesSet.add('[' + initDate.toString() + ']');
      return datesSet;
  }
  ```
  （在程序开始之前的静态方法，找出这一年所有整理日，之后通过`contains()`可以判断是否为整理日）

### 类设计

#### `Student`

 每一个学生都是独一无二的，由于后续作业表明不同学校可能有相同学号的学生，因此每个Student类中需要包含学校名和学生名(学号）两个字段。此外，每个学生借书还书还需要提供书的容器，以及保存当前拥有书籍借阅日期的信息：

![](https://pic.superbed.cc/item/66ab3f09fcada11d3762cafe.png)

对于书籍的损坏，我并没有将损坏作为一个字段放在`Book`类中，而是对于在`Student`类中新建`HashSet<Book>`中保存已损坏的书籍。下面是借书还书的示例：

```java
public void borrowB(Book book, String date) {
    this.bs.add(book);
    this.borrowDate.put(book.getName(), date);
}

 public Book returnB(String bookName) {
    Book reBook = null;
    for (Book book: bs) {
        if (book.getName().equals(bookName)) {
            reBook = book;
            break;
        }
    }
    this.bs.remove(reBook);
    this.borrowDate.remove(bookName);
    return reBook;
 }
```

在主函数Main中，我是用`HashMap<String, HashMap<String, Student>> students`来保存所有的学生的。

#### `Lib`

用于表示每个学校的图书馆。提供查询图书方法，以及查询借阅是否合法（针对预定），校内借阅，校际借阅的方法。

![](https://pic.superbed.cc/item/66ab3f29fcada11d3762ce0a.png)

#### `Bar`（borrowing and returning librarian）

![](https://pic.superbed.cc/item/66ab3f49fcada11d3762cf88.png)

 借还管理员类中应包含学校名称，接受校内归还的图书以及未成功借出的图书存储到`barLib`，校际借阅归的图书还到`outBooks`。其中包含了借书还书，赔偿罚款，运走接受书籍（校际借阅），判断日期间隔的方法。

![](https://pic.superbed.cc/item/66ab3f73fcada11d3762db00.png)

#### `Mace`(self-service machine)

![](https://pic.superbed.cc/item/66ab3f93fcada11d3762e56a.png)

与上文借还管理员类相似。

#### `Rai`（logistics division）

![](https://pic.superbed.cc/item/66ab3fa9fcada11d3762ecc6.png)

后勤管理处用于修补损坏的图书，提供`raiLib`存储校内借阅的图书，`outBooks`存储校际借阅的图书。同时提供运送书籍和接收书籍的方法。

#### `Odr`(ordering librarian)

![](https://pic.superbed.cc/item/66ab3fcefcada11d3762f4b5.png)

预定管理员通过`orderList`和`buyList`记录每一天需要进行校内预定（等待还书和购买）的信息，同时提供一个存储书籍的容器`counter`和存储购买的新书的容器`buyMap`。该类还提供分发书籍，将书籍放回图书馆等方法。

### 设计细节与bug分析

- 利用泛型存储每个学校的所有管理员对象：

  ```java
  //使用TreeMap在遍历时可以按照字典序输出
  TreeMap<String, ArrayList<Object>> schools = new TreeMap<>();
  //ArrayList<Object>可以允许不同的类型存储
  public static void initDepartment(ArrayList<Object> departments, String schoolName) {
      Bar bar = new Bar(schoolName);// 1->Bar BorrowingAndReturningLibrarian
      departments.add(bar);
      Odr odr = new Odr(schoolName);// 2->Odr OrderLibrarian
      departments.add(odr);
      Mace mace = new Mace(schoolName);// 3->Mace ServiceMachine
      departments.add(mace);
      Rai rai = new Rai(schoolName);// 4->Rai LogisticsDivision
      departments.add(rai);
      Arg arg = new Arg(schoolName);// 5->Arg ArrangingLibrarian
      departments.add(arg);
      Pcd pcd = new Pcd(schoolName);// 6->Pcd PurchasingDepartment
      departments.add(pcd);
  }
  ```

- 特别注意最后一条信息之后也要处理当天的预定信息：

  ```java
  for (int i = 0;i < m;i++) {//m条图书借还信息
      ......
  	if (i == m - 1) {
          process(proList, schools, transList, students);//处理所有没能借成功的信息
          transport(transList, schools, today);
      }
  }
  ```

- 注意日期的加减，应该调用`plusDays()`方法，而不是将day++然后手动转化，比如：

  ```java
  public static String findArrDay(String year, String month, String day) {
      return LocalDate.of(year, month, day).plusDays(1).toString();
      //错误示范：
      //String nextDay = (Integer.parseInt(day) + 1).toString();
      //return LocalDate.of(year, month, nextDay).toString();
  }
  ```

## 最终的代码和UML模型设计之间的追踪关系

本单元作业在第二次作业进行了重构。因为考虑到校际借阅的问题，每个图书中包含的信息量增大，不能仅仅用字符串来表示，这样底层的容器，方法必须全部修改。此外还要考虑多个学校多个管理员以及顺序输出，因此schools存储结构也要改变（相比之下，我的第一次作业就是一种单例模式）。

![](https://pic.superbed.cc/item/66ab3fedfcada11d3762fc47.png)

Zoom in to check details.

最终的代码和UML图基本吻合。从UML类图可以看出，图书馆的各个管理员都建立了类，每个类内部提供方法执行对应的职能，在主类中直接调用对象的方法即可。

## 四个单元中架构设计思维的演进

- 第一单元（表达式化简）：架构设计的雏形，第一次接触到用递归下降方法解析表达式。我们需要通过各个类来实现具体的解析和计算，可以简单理解为分工合作，各个部分完成的工作大不相同却又相辅相成，相互依赖
- 第二单元（多线程和电梯）：初步接触多线程编程，通过调度器类支持整个模块的运转
- 第三单元（JML与规格）：掌握编程的规范化，了解了接口，继承以及多态（`emojiMessage, moneyMessage等`)在面向对象中的实现
- 第四单元（通过UML对图书馆建模）：根据需求绘制UML类图，根据图进行正向建模

## 四个单元中测试思维的演进

前两个单元主要依靠讨论区同学们提供的评测机以及手动构造数据来进行测试，认识到自动化测试是一种高效的测试方法。

第三单元主要依靠JUnit进行方法进行单元测试，通过编写单元测试类和方法，来实现对类和方法实现正确性的快速检查和测试，可以很好排除bug。

第四单元的测试点并没有刻意去增加强度，而是希望我们把重点放在UML图的设计中，手动构造数据即可很容易地通过测试。

## 课程收获

- 深入理解了面向对象的思维方式。面向对象强调将问题领域的概念和行为转化为对象，并通过它们之间的交互来解决问题。这种思维方式使得系统的设计更加模块化、可扩展和可维护。比如说第一单元表达式分为表达式，项，因子类等，第四单元将众多图书管理员各自建立类实现。
- 熟悉了一门编程语言。不得不说，Java和C语言在很多方面都有相似之处，通过面向对象的学习可以让我在实践中逐渐掌握一门新的语言。
- 认识了一些设计模式。设计模式如工厂模式、观察者模式和单例模式等，提供了解决特定问题的经典解决方案。这些模式对于构建高质量的软件系统至关重要。
- 对测试有了新的理解。千万不要妄想着一遍不做任何测试就能成功，即使整体思路不存在问题，还是会出现许多小bug，因此做好测试是十分有必要的。
