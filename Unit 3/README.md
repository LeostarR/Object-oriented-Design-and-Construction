## Introduction

The training objective of the third unit assignments is to **master the understanding and implementation of JML specifications**, with three assignments for iterative development.

Tasks to be completed include implementing a simple simulation and query of social relationships (first assignment), implementing group and message features in the social relationship simulation system (second assignment), and implementing different message types and related operations in the social relationship system (third assignment).

Click here to refer to my code: [https://github.com/LeostarR/Object-oriented-Design-and-Construction](https://github.com/LeostarR/Object-oriented-Design-and-Construction)

## Summary of First Assignment

### Design and Architecture

This assignment requires completing the following tasks:

1. Implement `MyPerson` and `MyNetwork` classes according to the official interface and JML.
2. Inherit from the abstract exception classes provided by the official to implement four exception classes.
3. Write OK tests for the `query_triple_sum` method in the `Network` class.

#### `MyPerson`

![](https://pic.superbed.cc/item/66ab414afcada11d37631e20.png)

`id`, `name`, `age`, `acquaintance`, and `value` are the five properties of this class. The interface requires providing access methods for three of these properties. `isLinked` checks if there's a social relationship between two people, `queryValue` queries the social value (if any), `addPerson` and `addValue` add social relationships. Due to dynamic changes in social relationships, I designed `acquaintance` and `value` as `ArrayList` structures for dynamic management.

#### `MyNetwork`

![](https://pic.superbed.cc/item/66ab4167fcada11d37632d39.png)

`people` is implemented as per the interface requirements. `contains` checks if the social network includes a person, `addPerson` and `addRelation` add people and relationships respectively, `queryValue` queries the social value between two people (if any), `isCircle` checks if two people are connected through relationships with others. `queryBlockSum` queries the number of the largest connected component, `queryTripleSum` queries the number of triangles. `queryTripleSumOKTest` is a method for OK testing of the `queryTripleSum` method.

#### Exception Classes:

##### `MyEqualPersonIdException`

![](https://pic.superbed.cc/item/66ab4183fcada11d37632df3.png)

##### `MyEqualRelationException`

![](https://pic.superbed.cc/item/66ab419afcada11d37633f46.png)

##### `MyPersonIdNotFoundException`

![](https://pic.superbed.cc/item/66ab41b2fcada11d3763438a.png)

##### `MyRelationNotFoundException`

![](https://pic.superbed.cc/item/66ab41fffcada11d376374c9.png)

The implementation of the four exception classes is largely similar: defining a static variable `sum`, incrementing `sum` each time an exception is thrown, and defining a static `HashMap` to store the occurrence count of exceptions for each ID.

#### Design Details and Bug Analysis

If implemented strictly according to the literal meaning of JML, this assignment would simply be rote copying without challenge. We found that using graph traversal for `isCircle` or `queryTripleSum` significantly increases CPU runtime.

Therefore, we used a Union-Find data structure to solve the problem of long CPU runtime: defined `HashMap<Integer, Integer> symbol = new HashMap<>()` to indicate the Union-Find set for each node, initially setting it to itself when adding a person. Each call to `isCircle` invokes the `find()` method to check if both nodes belong to the same Union-Find set. The `find()` method recursively finds all nodes on the path from this point to the root node and updates their Union-Find sets:

```java
public int find(int key) {
    if (/*the Union-Find set of key is not itself*/) {
        symbol.put(key, find(symbol.get(key)));
    }
    return /*the Union-Find set obtained after searching*/;
}
```

And during each `addRelation()`, we use `merge()` to combine two nodes into the same Union-Find set:

```java
public void merge(int id1, int id2) {
    int root1 = /*Union-Find set of id1*/;
    int root2 = /*Union-Find set of id2*/;
    if (root1 != root2) {
        symbol.put(root1, root2);
    }
}
```

Thus optimizing `isCircle()`.

Similarly, for `queryBlockSum` and `queryTripleSum`, we can utilize the above approach: define global variables `globalQts` and `globalQbs`. When `addPerson` is called, increment the connected component count; in `addRelation`, before calling `merge`, check if both nodes belong to the same Union-Find set; if not, decrement the connected component count. For the number of triangles, just check how many points are connected to both points after each `addRelation`.

#### UML Class Diagram

![](https://pic.superbed.cc/item/66ab4272fcada11d376387ee.png)

## Summary of Second Assignment

### Design and Architecture

Based on the first assignment, additional tasks are required:

1. Implement the `Group` and `Message` classes according to the official interface JML and enhance more methods in the `Network` class.
2. Implement more exception classes.
3. Write OK tests for the `modifyRelation` method in the `Network` class.

#### `MyGroup`

![](https://pic.superbed.cc/item/66ab4291fcada11d37638911.png)

This class simulates social groups, where `id` is the group ID, `people: ArrayList<Person>` is the collection of people in the group, `addPerson` and `delPerson` add and remove members from the group, `hasPerson` checks if a member exists in the group, `getValueSum`, `getAgeMean`, `getAgeVar` calculate values related to the group and its members' ages, and `getSize` returns the total number of members.

#### `MyMessage`

![](https://pic.superbed.cc/item/66ab42aafcada11d37638bb9.png)

This class defines five member attributes: `id`, `socialValue`, `type`, `person1`, `person2`, `group`, with corresponding access methods.

#### `MyNetwork`

![](https://pic.superbed.cc/item/66ab42c8fcada11d37638cdd.png)

Compared to the first assignment, this class has added more methods. `modifyRelation` adjusts the relationship between two people, either changing the social value or deleting the relationship, and also requires writing an OK test method for this function. `addGroup` adds a group, `addToGroup` adds a person to a specified group, `delFromGroup` removes a person from a specified group. `queryGroupValueSum` and `queryGroupAgeVar` return specific numbers related to the group. `addMessage`, `getMessage`, and `sendMessage` add, receive, and send messages respectively. `queryBestAcquaintance` queries the ID of the highest social value acquaintance of the corresponding ID who has a smaller ID. `queryCoupleSum` queries the number of pairs where two IDs are each other's `BestAcquaintance`.

#### Exception Classes

##### `MyAcquaintanceNotFoundException`

![](https://pic.superbed.cc/item/66ab42e2fcada11d37638ec1.png)

##### `MyEqualGroupIdException`

![](https://pic.superbed.cc/item/66ab4302fcada11d3763905a.png)

##### `MyEqualMessageIdException`

![](https://pic.superbed.cc/item/66ab4320fcada11d37639186.png)

##### `MyGroupIdNotFoundException`

![](https://pic.superbed.cc/item/66ab433bfcada11d37639314.png)

##### `MyMessageIdNotFoundException`

![](https://pic.superbed.cc/item/66ab4354fcada11d37639436.png)

New exception classes are designed similarly to those in the first assignment and will not be repeated.

#### Design Details and Bug Analysis

With experience from the first assignment, we know certain JML instructions suggest avoiding large loops. For example, `queryValueSum` calculates the sum of values for all people in a group and others they have relations with. Only places that need to modify `valueSum` are `addRelation` and `modifyRelation`.

However, for `ageMean` and `ageVar`, which are related to the number of people in the group, changes cannot determine the size of the modification beforehand, so calculations must occur after changes. A flag can be set to return the last calculation result if no additions or deletions have occurred, improving efficiency.

For `queryBestAcquaintance`, we directly return a variable in the corresponding person that dynamically changes. Specifically, every time `addRelation` and `modifyRelation` are called, we consider this variable. Adding a relation checks if the `value` is greater than the original `maxValue`; modifying a value considers whether it exceeds the original `maxValue` or if the original value was `maxValue`, and removing a relation considers if the maximum value is deleted (many scenarios were not fully considered, leading to serious bugs during strong testing).

`queryCoupleSum` depends on `queryBestAcquaintance`. Since `BestAcquaintance` only changes after `addRelation` and `modifyRelation`, affecting two points, we create a method to calculate the `coupleSum` involved for two points (minimum 0, maximum 2). Calling this method before and after changing the relationship and then subtracting gives the change in `CoupleSum`.

In addition, the Union-Find part from the first assignment had to be modified due to the addition of edge deletion functionality. In this case, we must ensure that each entry in `HashMap` (id1->id2) is stored in order; otherwise, abnormal situations may arise due to edge deletion. A new `disconnect` method deletes the relationship between two nodes and reconstructs:

```java
public void disconnect(int id1, int id2) {
    int i1 = Integer.min(id1, id2);
    int i2 = Integer.max(id1, id2);
    pairs.remove(i1);
    for (HashMap.Entry<Integer, Integer> entry : symbol.entrySet()) {
        entry.setValue(entry.getKey());
    }
    for (HashMap.Entry<Integer, Integer> entry : pairs.entrySet()) {
        merge(entry.getKey(), entry.getValue());
    }
}
```

#### UML Class Diagram

![](https://pic.superbed.cc/item/66ab436bfcada11d37639674.png)

## Summary of Third Assignment

### Design and Architecture

Based on the second assignment, the following content needs to be added:

1. Implement `MyEmojiMessage`, `MyNoticeMessage`, and `MyRedEnvelopeMessage` according to the official interface JML and enhance the `MyNetwork` class.
2. Add three new exception classes.
3. Write OK tests for `deleteColdEmoji`.

#### `MyEmojiMessage`

![](https://pic.superbed.cc/item/66ab4386fcada11d376397e2.png)

Compared to the `MyMessage` class, a new `emojiId` field and corresponding access methods are added.

#### `MyNoticeMessage`

![](https://pic.superbed.cc/item/66ab43a0fcada11d37639859.png)

Compared to the `MyMessage` class, a new `string` field and corresponding access methods are added.

#### `MyRedEnvelopeMessage`

![](https://pic.superbed.cc/item/66ab43c0fcada11d3763994c.png)

Compared to the `MyMessage` class, a new `money` field and corresponding access methods are added.

#### `MyNetwork`

![](https://pic.superbed.cc/item/66ab43d6fcada11d37639a22.png)

New methods to be implemented include `containsEmojiId()` (check if emoji exists), `storeEmojiId()` (add emoji), `queryMoney()` (query money field), `queryPopularity()` (query popularity field), `deleteColdEmoji()` (delete emojis with heat less than limit), `clearNotice()` (clear notices), `queryLeastMoments()` (find the shortest self-loop). Also, the original `sendMessage` method needs to be enhanced.

#### Exception Classes

##### `MyEmojiIdNotFoundException`

![](https://pic.superbed.cc/item/66ab43f3fcada11d37639b6a.png)

##### `MyEqualEmojiIdException`

![](https://pic.superbed.cc/item/66ab4407fcada11d37639cd0.png)

##### `MyPathNotFoundException`

![](https://pic.superbed.cc/item/66ab4420fcada11d37639e17.png)

New exception classes are implemented identically to previous ones and will not be repeated.

#### Design Details and Bug Analysis

The third assignment requires high algorithmic standards, especially for finding the smallest loop, which involves enumerating non-tree edges combined with Dijkstra's algorithm while prioritizing queue enumeration to reduce loop counts.

Additionally, unresolved issues from the first assignment led to multiple hacks during mutual testing (@_@). For instance, adding a relationship with oneself when a person's acquaintances list is empty should throw an exception but did not. Revisiting the JML revealed the issue: the condition `id != person.getId()` was outside the loop brackets. Placing this check inside the loop resolved the issue, but since the person's acquaintances list was empty, the loop was never entered, thus not returning `true`. Similar issues arose with `ageVar` and `ageMean` in the second assignment due to JML bracket recognition problems.

![](https://pic.superbed.cc/item/66ab4443fcada11d37639f6b.png)

#### UML Class Diagram

![](https://pic.superbed.cc/item/66ab4450fcada11d37639fee.png)

## Testing Process

##### Understanding Black-box Testing and White-box Testing

Black-box testing involves testing based on software functionality and requirements without considering internal implementation details. Testers focus on input and output relationships, verifying if the software functions meet expectations. The goal is to identify functional defects, interface errors, performance issues, etc., without concerning the specific code implementation.

White-box testing involves testing based on the internal structure and implementation details of the software. Testers have access to source code and can delve into the internal logic and data flow. The goal is to verify the correctness of internal logic, compliance with coding standards, potential errors, security vulnerabilities, etc.

##### Understanding Unit Testing, Functional Testing, Integration Testing, Stress Testing, and Regression Testing

- Unit Testing: Verifying the smallest testable units of software, such as functions, methods, or classes. It aims to test units in isolation to ensure their correctness.
- Functional Testing: Verifying the functionality of software modules and components to confirm they work as expected according to requirement specifications or user expectations.
- Integration Testing: Testing between multiple independent software modules or components to validate their interaction and interface correctness.
- Stress Testing: Testing under normal or beyond-normal workload conditions to evaluate performance, stability, and reliability.
- Regression Testing: Re-executing existing test cases after software modifications or fixes to ensure new changes do not introduce new errors or affect existing functionality.

##### Data Construction Strategy

1. Normal Values: Select normal values that conform to program expectations as test data. These values should cover various functionalities and logical conditions, including boundary values, typical values, and general cases.
2. Boundary Values: Choose test data close to boundary conditions. Boundary values are most likely to cause errors and require special attention.
3. Random Values: Use randomly generated test data to cover various situations and possible input combinations, leveraging the evaluation machine.

## Thoughts on OK Testing

##### Role of OK Testing

1. OK testing helps developers better understand requirements and identify potential issues or contradictions in specifications during development. Ensuring all specification requirements are met improves developer accuracy.
2. OK testing provides an objective, verifiable method to check if code meets specification requirements, ensuring correctness and reducing issues caused by unclear or incorrect specifications.
3. OK testing enhances code maintainability by reducing redundancy and unnecessary complexity, making code easier to understand and maintain.

##### Improvements and Suggestions

1. Increase OK test coverage to discover more issues and defects. This can be achieved by increasing test cases and considering different input combinations.
2. Automate testing to reduce costs and improve efficiency and accuracy. Existing automated testing frameworks can be utilized.

## Learning Reflection

##### Separation of Specification and Implementation

Separating specifications from implementation aims to reduce program complexity, enhance readability, and maintainability. Modularization and reusability are improved, and testing efficiency and quality are increased as specifications can serve as the basis for test cases without depending on specific implementations.

##### Reflections

- From Novice to Proficient in JML: JML allows developers to specify program properties, preconditions, postconditions, and invariants. It offers a structured and standardized way to define expected program behavior. Learning JML enabled me to write more reliable and maintainable code.
- Applying JML in programming throughout this unit involved iterative development heavily reliant on JML language to simulate a social network. Reflecting on the process of continuously discovering issues and successfully debugging them was very rewarding.

---



## 介绍

第三单元作业的训练目标是**掌握JML规格的理解与实现**，三次作业为迭代开发。

需要完成的任务为实现简单社交关系的模拟和查询（第一次作业），实现社交关系模拟系统中的群组和消息功能（第二次作业）和实现社交关系系统中不同消息类型以及相关操作（第三次作业）。

点击此处可以参考我的代码：https://github.com/LeostarR/Object-oriented-Design-and-Construction

## 第一次作业总结

### 设计与架构

本次作业需要完成这些工作：

1. 根据官方接口和JML实现`MyPerson`类和`MyNetwork`类
2. 继承官方提供的各抽象异常类，实现四个异常类
3. 为`Network`类中的`query_triple_sum`方法书写OK测试

#### `MyPerson`

![](https://pic.superbed.cc/item/66ab414afcada11d37631e20.png)

 `id`, `name`, `age`, `acquaintance`和`value`是该类的五个属性，接口要求为其中三个属性提供访问方法。`isLinked`用于判断两者是否有社交关系，`queryValue`用于查询社交值（如果有），`addPerson`和`addValue`用于添加社交关系。由于社交关系是动态变化的，因此我在设计的时候将acquaintance和value两个属性都定义为了`ArrayList`的结构，便于实现动态管理。

#### `MyNetwork`

![](https://pic.superbed.cc/item/66ab4167fcada11d37632d39.png)

 `people`为按照接口要求实现的变量。`contains`方法判断社交网络中是否包含此人，`addPerson`和`addRelation`分别用于添加人和关系，`queryValue`用于查询两人之间的社交值（如果有），`isCircle`判断两人是否相连（通过与其他人的关系）。`queryBlockSum`用于查询最大连通分支的数目，`queryTripleSum`用于查询三元环的数目。`queryTripleSumOKTest`是一个对`queryTripleSum`方法进行OK测试的方法。

#### Exception：

##### `MyEqualPersonIdException`

![](https://pic.superbed.cc/item/66ab4183fcada11d37632df3.png)

##### `MyEqualRelationException`

![](https://pic.superbed.cc/item/66ab419afcada11d37633f46.png)

##### `MyPersonIdNotFoundException`

![](https://pic.superbed.cc/item/66ab41b2fcada11d3763438a.png)

##### `MyRelationNotFoundException`

![](https://pic.superbed.cc/item/66ab41fffcada11d376374c9.png)

 四种异常类的实现方法大同小异：定义静态变量sum，每次throw时将sum加1，同时定义静态HashMap存储每个id发生的异常次数。

#### 设计细节与bug分析

 如果完全按照JML的字面意思来实现的话，本次作业就是完完全全的照葫芦画瓢，没有任何难度。我们发现`isCircle`如果采用图遍历的方式每次调用时查找，CPU运行时间会大大增加，同样对于`queryTripleSum`。

因此本次作业采用并查集来解决CPU运行时间过长的问题：定义`HashMap<Integer, Integer> symbol = new HashMap<>()`指示每个节点对应的并查集，初始（添加这个人）时该集合设定为自己。每次调用`isCircle`时会调用`find()`方法判断两者是否同属一个并查集。而`find()`方法会递归地寻找从这个点到根节点的所有节点，并将这些节点的并查集全部更新：

```java
public int find(int key) {
    if (/*key的并查集不是自己*/) {
        symbol.put(key, find(symbol.get(key)));
    }
    return /*查找之后得到的并查集*/;
}
```

而在每次`addRelation()`时，我们会调用`merge()`将两节点合并到同一个并查集中：

```java
public void merge(int id1, int id2) {
    int root1 = /*id1的并查集*/;
    int root2 = /*id2的并查集*/;
    if (root1 != root2) {
        symbol.put(root1, root2);
    }
}
```

这样就完成了对`isCircle()`的优化。

 同样对于`queryBlockSum`和`queryTripleSum`的方法也可以利用上述的方法：设定全局变量`globalQts`和`globalQbs`。每次`addPerson`时需要将连通分支数目加一，在`addRelation`中`merge`之前判断两人是否同属一个并查集，若不是，则将连通分支数目减一。而三元环的数目只需每次`addRelation`之后判断有多少个点同时与这两点相连即可。

#### UML类图

![](https://pic.superbed.cc/item/66ab4272fcada11d376387ee.png)

## 第二次作业总结

### 设计与架构

在第一次作业基础上需要增加这些工作：

1. 按照官方接口的JML实现`Group`类和`Message`类，并完善`Network`类中的更多方法
2. 实现更多的异常类
3. 为`Network`类中的`modifyRelation`方法书写OK测试

#### `MyGroup`

![](https://pic.superbed.cc/item/66ab4291fcada11d37638911.png)

 此类用于模拟社交群组，`id`为组id，`people: ArrayList<Person>`为组中人的集合，`addPerson`和`delPerson`用于向组中加入和删除成员，`hasPerson`用于判断组中是否存在某个成员，`getValueSum`，`getAgeMean`，`getAgeVar`用于计算整个组和成员年龄相关的值，`getSize`返回成员总数。

#### `MyMessage`

![](https://pic.superbed.cc/item/66ab42aafcada11d37638bb9.png)

 此类定义了五个成员属性：`id`, `socialValue`, `type`, `person1`, `person2`, `group`，并分别配置了访问方法。

#### `MyNetwork`

![](https://pic.superbed.cc/item/66ab42c8fcada11d37638cdd.png)

相比第一次作业，此类新增了更多方法。`modifyRelation`用于调整两个人之间的关系，可以是改变社交值，也可以是删除关系，此外还需要为该方法编写OK测试方法。`addGroup`用于添加组，`addToGroup`用于将人添加到指定组中，`delFromGroup`则是将人从指定组中删去。`queryGroupValueSum`和`queryGroupAgeVar`用于返回组群中特定含义的数。`addMessage`，`getMessage`和`sendMessage`分别用于添加消息，接受消息和发送消息。`queryBestAcquaintance`用于查询对应id的有关系的人中社交值最高并且id更小的一个id。`queryCoupleSum`用于查询两个id互为对方的`BestAcquaintance`的对数。

#### Exception

##### `MyAcquaintanceNotFoundException`

![](https://pic.superbed.cc/item/66ab42e2fcada11d37638ec1.png)

##### `MyEqualGroupIdException`

![](https://pic.superbed.cc/item/66ab4302fcada11d3763905a.png)

##### `MyEqualMessageIdException`

![](https://pic.superbed.cc/item/66ab4320fcada11d37639186.png)

##### `MyGroupIdNotFoundException`

![](https://pic.superbed.cc/item/66ab433bfcada11d37639314.png)

##### `MyMessageIdNotFoundException`

![](https://pic.superbed.cc/item/66ab4354fcada11d37639436.png)

 新的异常类和第一次作业设计相同，不再赘述。

#### 设计细节与bug分析

 有了第一次作业的经验，我们知道，某些JML表面意思为做大量循环的方法要考虑直接循环的代价。所以可以考虑实现动态变化，在所有可能改变这个值的方法里修改这个值，在真正需要这个值的时候直接调用即可。例如`queryValueSum`，用于计算群组中所有的人和其他有关系的人的value之和，只有这些地方需要修改`valueSum`：`addRelation`之后，`modifyRelation`之后。

 但对于`ageMean`和`ageVar`两个与群组人数相关的变量，每次增加人数都不能确定改变的大小，因此必须在改变人数之后进行计算，这里可以设置标记，若没有增加或删除人，则可以返回上一次的计算结果，以达到提高效率的目的。

 对于`queryBestAcquaintance`，我们直接返回对应person中的一个变量，这个变量也是动态变化的。具体来说，每一次`addRelation`和`modifyRelation`时我们都要考虑这个变量，添加关系时，判断该`value`是否大于原`maxValue`，修改value时考虑是否会超过原`maxValue`，或者原value就是`maxValue`，以及删除关系时考虑最大值被删除等等（情况比较多，我在这里没有完全考虑清楚导致强测出现了很严重的bug）。

 `queryCoupleSum`实际上依赖于`queryBestAcquaintance`(所以这里也寄了)，由上文知道，`BestAcquaintance`只会在`addRelation`和`modifyRelation`之后发生变化，并且只会针对两个点，我们只需在此时新建一个方法，这个方法可以计算两个点涉及的`coupleSum`数目（最小为0，最大为2），在改变关系前后分别调用，再相减就能得到经过这次改变`CoupleSum`应该变化的大小。

 同时，我们第一次作业中对于并查集的部分也因为增加了删边的新功能而必须修改。在这种情况下，我们必须保证每一次存入`HashMap`中的(id1->id2)按顺序排列，否则可能会出现因删边导致异常的情形。新增`disconnect`方法用于删除两节点的关系并且重新构造：

```java
public void disconnect(int id1, int id2) {
    int i1 = Integer.min(id1, id2);
    int i2 = Integer.max(id1, id2);
    pairs.remove(i1);
    for (HashMap.Entry<Integer, Integer> entry : symbol.entrySet()) {
        entry.setValue(entry.getKey());
    }
    for (HashMap.Entry<Integer, Integer> entry : pairs.entrySet()) {
        merge(entry.getKey(), entry.getValue());
    }
}
```

#### UML类图

![](https://pic.superbed.cc/item/66ab436bfcada11d37639674.png)

## 第三次作业总结

### 设计与架构

在第二次作业基础上，需要新增这些内容：

1. 根据官方接口的JML实现`MyEmojiMessage`，`MyNoticeMessage`和`MyRedEnvelopeMessage`三个类，并完善`MyNetwork`类
2. 新增三个异常类
3. 对`deleteColdEmoji`编写OK测试方法

#### `MyEmojiMessage`

![](https://pic.superbed.cc/item/66ab4386fcada11d376397e2.png)

相比`MyMessage`类，新增了`emojiId`字段和对应的访问方法。

#### `MyNoticeMessage`

![](https://pic.superbed.cc/item/66ab43a0fcada11d37639859.png)

相比`MyMessage`类，新增了`string`字段和对应的访问方法。

#### `MyRedEnvelopeMessage`

![](https://pic.superbed.cc/item/66ab43c0fcada11d3763994c.png)

相比`MyMessage`类，新增了`money`字段和对应的访问方法。

#### `MyNetwork`

![](https://pic.superbed.cc/item/66ab43d6fcada11d37639a22.png)

 需要新实现的方法有`containsEmojiId()`(查找表情是否存在)，`storeEmojiId()`（添加表情）,`queryMoney()`（查询money字段）, `queryPopularity()`（查询popularity字段）, `deleteColdEmoji()`（删除heat值小于limit的表情）, `clearNotice()`(清除通知）,`queryLeatMoments()`（查找最短自环）。并要求对原`sendMessage`方法进行完善。

#### Exception

##### `MyEmojiIdNotFoundException`

![](https://pic.superbed.cc/item/66ab43f3fcada11d37639b6a.png)

##### `MyEqualEmojiIdException`

![](https://pic.superbed.cc/item/66ab4407fcada11d37639cd0.png)

##### `MyPathNotFoundException`

![](https://pic.superbed.cc/item/66ab4420fcada11d37639e17.png)

新的异常类与之前实现完全相同，不再赘述。

#### 设计细节与bug分析

 第三次作业对算法的要求极高，在查找最小环的过程需要通过枚举非树边和`djikstra`算法结合查找每一个点的最小环，同时必须按照优先队列的方式枚举以达到减小循环次数的目的。

 此外，笔者因为第一次作业的遗留问题在互测中被多次hack@_@（很奇怪居然前两次测试都没有测出来）。例如在将某个人的acquaintance为空时添加与自己本身的关系时，应该抛出异常的却没有抛出异常，再根据JML仔细查看发现了问题：判断`isLinked()`的方法前面一个括号包含了循环，后面的`id != person.getId()`并不包含在括号中。如果我把判断id和`person.getId()`放在循环中，但是此时person的acquaintance为空，因此不会进入循环也就不会返回`true`（以及第二次作业的`ageVar`和`ageMean`出现问题也是因为JML括号的识别问题）。

![](https://pic.superbed.cc/item/66ab4443fcada11d37639f6b.png)

#### UML类图

![](https://pic.superbed.cc/item/66ab4450fcada11d37639fee.png)

## 测试过程

##### 对黑箱测试、白箱测试的理解

黑箱测试是基于软件的功能和需求进行测试，而不考虑内部的实现细节和结构。测试人员对软件系统进行测试时，只关注输入和输出之间的关系，测试软件的功能是否符合预期。黑箱测试的目标是发现系统的功能缺陷、界面错误、性能问题等，而不关注代码的具体实现。

白箱测试是基于软件的内部结构和实现细节进行测试。测试人员具有对源代码的访问权限，可以深入了解软件的内部逻辑和数据流。白箱测试的目标是验证软件的内部逻辑是否正确、代码是否符合编码标准、是否存在潜在的错误和安全漏洞等。

##### 对单元测试、功能测试、集成测试、压力测试、回归测试的理解

- 单元测试是对软件中最小的可测试单元进行验证的过程。单元可以是函数、方法或类等独立的代码块。单元测试的目标是在尽可能独立的环境中对单元进行测试，以确保其功能的正确性。
- 功能测试是对软件的功能进行验证的测试类型。它涉及对软件的各个功能模块和组件进行测试，以确认其是否按照需求规格说明书或用户期望的方式正常工作。功能测试通常以用户的角度来进行，通过模拟真实用户的操作来检查软件的功能是否符合预期。
- 集成测试是在多个独立的软件模块或组件之间进行测试，以验证它们的协同工作和接口的正确性。集成测试的目标是发现模块之间的集成问题、接口错误、数据传递问题等。它可以确保各个模块在集成后能够正常工作，并且相互之间没有冲突或不一致。
- 压力测试是对软件系统在正常或超出正常工作负载条件下进行测试的过程。其目的是评估系统的性能、稳定性和可靠性，以确定在负载增加的情况下系统的响应是否满足需求。压力测试通过模拟高并发、大数据量或异常负载情况来测试系统的极限情况。
- 回归测试是在软件进行更改或修复后，重新执行既有的测试用例，以确保新的修改没有引入新的错误或破坏了原有的功能。回归测试的目标是验证软件在经过修改后仍然具有稳定性和兼容性，不会对现有功能产生负面影响。

##### 数据构造策略

1. 正常值：选择符合程序预期输入的正常值作为测试数据。这些值应涵盖程序的各种功能和逻辑情况，包括边界值、典型值和一般情况。
2. 边界值：选择接近边界条件的测试数据。边界值通常是最有可能导致错误的情况，因此在测试过程中应特别关注这些值。例如，构造一个不存在长度大于等于3的自环，验证`queryLeastMoments`的正确性。
3. 随机值：使用随机生成的测试数据来测试程序。通过生成大量的随机数据来覆盖各种情况和可能的输入组合，利用评测机。

## 对OK测试的思考

##### OK测试的作用

1. OK测试可以帮助开发人员更好地理解需求规格，并在开发过程中发现规格中可能存在的问题或矛盾。通过对规格中的每个要求都进行OK测试，可以确保程序实现了所有规格要求，同时也能够提高开发人员对规格的理解和准确性。
2. OK测试提供了一种客观、可验证的方法来检查代码实现是否符合规格要求。它能够帮助开发人员确保代码的正确性，并减少因为规格不清晰或错误而导致的问题。
3. OK测试可以提高代码的可维护性。由于OK测试要求开发人员明确规格要求和实现的一致性，因此可以帮助减少代码中的冗余和不必要的复杂性，使得代码更易于理解和维护。

##### 改进和建议

1. 提高OK测试的覆盖率。为了发现更多的问题和缺陷，需要尽可能覆盖更多的规格要求。这可以通过增加测试用例、考虑不同的输入组合来实现。
2. 自动化测试。将OK测试自动化可以减少测试成本和人力成本，并提高测试效率和准确性。可以使用现有的自动化测试框架来实现。

## 学习体会

##### 规格与实现分离

 规格与实现分离的目的是为了降低程序的复杂度，提高程序的可读性和可维护性。通过将规格描述与程序实现分离，可以使得程序的设计更加模块化、可重用，更容易理解和修改。此外，规格与实现分离还可以提高程序的测试效率和质量，因为规格描述可以作为测试用例的基础，而不必依赖于程序的具体实现。

##### 感想

- 从0到深入了解JML：JML是一种允许开发人员指定程序属性、前置条件、后置条件和不变量的语言。JML提供了一种结构化和规范的方法来定义程序的预期行为。学习JML使我能够编写更可靠和易于维护的代码。
- 应用JML编写程序，整个单元通过迭代式开发，依赖于大量JML语言完成一个社交网络的模拟。回首整个单元不断发现问题，debug成功的过程，其实是一件很有成就感的事情。
